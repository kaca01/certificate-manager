package com.example.certificateback.service.implementation;

import com.example.certificateback.domain.Password;
import com.example.certificateback.domain.Role;
import com.example.certificateback.domain.User;
import com.example.certificateback.domain.UserActivation;
import com.example.certificateback.dto.ErrorDTO;
import com.example.certificateback.dto.UserDTO;
import com.example.certificateback.exception.BadRequestException;
import com.example.certificateback.exception.NotFoundException;
import com.example.certificateback.repository.IPasswordRepository;
import com.example.certificateback.repository.IRoleRepository;
import com.example.certificateback.repository.IUserActivationRepository;
import com.example.certificateback.repository.IUserRepository;
import com.example.certificateback.service.interfaces.IUserService;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserService implements IUserService, UserDetailsService {
	@Autowired
	private IUserRepository userRepository;
	@Autowired
	private IUserActivationRepository userActivationRepository;
	@Autowired
	private IPasswordRepository passwordRepository;
	@Autowired
	private IRoleRepository roleRepository;
	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleService roleService;

	@Override
	public User findByEmail(String email) throws UsernameNotFoundException {
		return userRepository.findByEmail(email).orElseThrow(()
				-> new UsernameNotFoundException(String.format("User with email '%s' is not found!", email)));
	}

	@Override
	public User findById(Long id) throws AccessDeniedException {
		return userRepository.findById(id).orElseGet(null);
	}

	@Override
	public List<UserDTO> findAll() throws AccessDeniedException {
		List<User> users = userRepository.findAll();
		List<UserDTO> usersDTO = new ArrayList<>();
		for (User u : users){
			usersDTO.add(new UserDTO(u));
		}
		return usersDTO;
	}

	@Override
	public UserDTO register(UserDTO registrationDTO) {
		if (this.userRepository.existsByEmail(registrationDTO.getEmail())) {
			throw new BadRequestException("User with that email already exists!");
		}
		User user = new User(registrationDTO);
		user.setEnabled(false);

		Password password = passwordRepository.save(new Password(passwordEncoder.encode(registrationDTO.getPassword())));
		user.getPasswords().add(password);

		List<Role> roles = new ArrayList<>();
		roles.add(roleRepository.findById(1L).get());
		user.setRoles(roles);
		user = userRepository.save(user);

		UserActivation activation = userActivationRepository.save(new UserActivation(user));

		try {
			sendActivationEmail(activation);
		} catch (MessagingException | UnsupportedEncodingException | javax.mail.MessagingException e) {
			throw new RuntimeException(e);
		}

		return new UserDTO(user);
	}

	private User findUserById(Long id)
	{
		return userRepository.findById(id).orElseThrow(
				() -> new NotFoundException("User does not exist!"));
	}

	@Override
	public ErrorDTO activateUser(Long activationId) {
		UserActivation activation = userActivationRepository.findById(activationId).orElseThrow(
				() -> new NotFoundException("Activation with entered id does not exist!"));
		User p = findUserById(activation.getUser().getId());
		if (new Date().before(new Date(activation.getDate().getTime() + activation.getLife()*1000L))) {
			p.setEnabled(true);
			userRepository.save(p);
			userActivationRepository.delete(activation);
			return new ErrorDTO("Successful account activation!");
		} else {
			userActivationRepository.delete(activation);
			passwordRepository.delete(p.getPasswords().get(0));
			userRepository.delete(p);
			throw new BadRequestException("Activation expired. Register again!");
		}
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return this.userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User with email '%s' is not found!", email)));
	}

	private void sendActivationEmail(UserActivation activation) throws MessagingException, UnsupportedEncodingException, javax.mail.MessagingException {
		User user = userRepository.findByEmail(activation.getUser().getEmail()).orElseThrow(()
				-> new NotFoundException("User does not exist!"));
		String toAddress = activation.getUser().getEmail();
		String fromAddress = "anastasijas557@gmail.com";
		String senderName = "CM app Support";
		String subject = "Activate Your CM Account";
		String content = "Hello [[name]], thank you for joining us!<br>"
				+ "To activate your account please follow this link: "
				+ "<a href='http://localhost:4200/activation/[[id]]'>activate</a><br>"
				+ "The Certificate Manager team.";

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom(fromAddress, senderName);
		helper.setTo(toAddress);
		helper.setSubject(subject);

		content = content.replace("[[name]]", user.getName() + " " + user.getSurname());
		content = content.replace("[[id]]", activation.getId().toString());
		helper.setText(content, true);

		mailSender.send(message);
	}
}

package com.example.certificateback.service.implementation;

import com.example.certificateback.domain.*;
import com.example.certificateback.dto.ResetPasswordDTO;
import com.example.certificateback.dto.UserDTO;
import com.example.certificateback.exception.BadRequestException;
import com.example.certificateback.exception.NotFoundException;
import com.example.certificateback.repository.*;
import com.example.certificateback.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.*;

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
	IResetPasswordRepository resetPasswordRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleService roleService;

	@Autowired
	private JavaMailSender mailSender;

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
//		if (this.userRepository.findByEmail(registrationDTO.getEmail())) {
//			throw new BadRequestException("User with that email already exists!");
//		}

		User user = new User(registrationDTO);
		user.setEnabled(false);

		Password password = passwordRepository.save(new Password(passwordEncoder.encode(registrationDTO.getPassword())));
		user.getPasswords().add(password);

		List<Role> roles = new ArrayList<>();
		roles.add(roleRepository.findById(1L).get());
		user.setRoles(roles);
		user = userRepository.save(user);

		UserActivation activation = userActivationRepository.save(new UserActivation(user));
		//sendActivationEmail(activation);

		return new UserDTO(user);
	}

	@Override
	public void sendResetEmail(String email) throws MessagingException, UnsupportedEncodingException {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User does not exist!"));
		// change toAddress
		String toAddress = "anastasijas557@gmail.com";
		String fromAddress = "anastasijas557@gmail.com";
		String senderName = "Certificate Manager Support";
		String subject = "Reset Your Password";
		String content = "Hi [[name]], let's reset your password.<br>"
				+ "Your verification code is:<br>"
				+ "<h2>[[code]]</h2>"
				+ "Thank you,<br>"
				+ "Your Certificate Manager Team.";

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);

		helper.setFrom(fromAddress, senderName);
		helper.setTo(toAddress);
		helper.setSubject(subject);

		content = content.replace("[[name]]", user.getName() + " " + user.getSurname());

		Random rnd = new Random();
		int number = rnd.nextInt(999999);
		String code = String.format("%06d", number);
		content = content.replace("[[code]]", code);

		helper.setText(content, true);

		mailSender.send(message);

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 7);
		Date toDate = cal.getTime();

		ResetPassword reset = resetPasswordRepository.findResetPasswordByUserId(user.getId());
		if(reset == null) {
			reset = new ResetPassword(user, toDate, code);
		}
		else {
			reset.setExpiredDate(toDate);
			reset.setCode(code);
		}

		resetPasswordRepository.save(reset);
	}

	@Override
	public void resetEmail(String email, ResetPasswordDTO resetPasswordDTO) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User does not exist!"));

		ResetPassword resetPassword = resetPasswordRepository.findResetPasswordByUserId(user.getId());
		Date expiredDate = resetPassword.getExpiredDate();

		if(!resetPasswordDTO.getCode().equals(resetPassword.getCode()) || expiredDate.before(new Date()))
			throw new BadRequestException("Code is expired or not correct!");

		// TODO check if this is correct
		Password password = passwordRepository.save(new Password(passwordEncoder.encode(resetPasswordDTO.getNewPassword())));
		user.getPasswords().add(password);
		userRepository.save(user);
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return this.userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User with email '%s' is not found!", email)));
	}

}

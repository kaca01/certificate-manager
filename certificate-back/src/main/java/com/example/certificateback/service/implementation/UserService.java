package com.example.certificateback.service.implementation;

import com.example.certificateback.domain.*;
import com.example.certificateback.dto.ResetPasswordDTO;
import com.example.certificateback.dto.UserDTO;
import com.example.certificateback.exception.BadRequestException;
import com.example.certificateback.exception.NotFoundException;
import com.example.certificateback.repository.*;
import com.example.certificateback.service.interfaces.IUserService;
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import static com.twilio.example.ValidationExample.ACCOUNT_SID;
import static com.twilio.example.ValidationExample.AUTH_TOKEN;

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

		saveResetPassword(user, code);
	}

	@Override
	public void resetEmail(String email, ResetPasswordDTO resetPasswordDTO) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User does not exist!"));

		ResetPassword resetPassword = resetPasswordRepository.findResetPasswordByUserId(user.getId());
		Date expiredDate = resetPassword.getExpiredDate();

		if(!resetPasswordDTO.getCode().equals(resetPassword.getCode()) || expiredDate.before(new Date()))
			throw new BadRequestException("Code is expired or not correct!");

		Password password = passwordRepository.save(new Password(passwordEncoder.encode(resetPasswordDTO.getNewPassword())));
		user.getPasswords().add(password);
		userRepository.save(user);
	}

	@Override
	public void sendSMS(String phone) {
		User user = userRepository.findByPhone(phone).orElseThrow(() -> new NotFoundException("User does not exist!"));

		Twilio.init(System.getenv("TWILIO_ACCOUNT_SID"), System.getenv("TWILIO_AUTH_TOKEN"));

		Verification.creator(
						"VAca2e1d4eb5f1ba4be26dc368c51754af", // this is your verification sid
						"+381621164208", // recipient phone number
						"sms") // this is your channel type
				.create();

		// the message code is null because there is no need to save it since it is checked automatically
		saveResetPassword(user, null);
	}

	@Override
	public void checkSMS(String phone, ResetPasswordDTO resetPasswordDTO) {
		User user = userRepository.findByPhone(phone).orElseThrow(() -> new NotFoundException("User does not exist!"));

		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

		try {
			VerificationCheck verificationCheck = VerificationCheck.creator(
							"VAca2e1d4eb5f1ba4be26dc368c51754af") // pass verification SID here
					.setTo("+381621164208")
					.setCode(resetPasswordDTO.getCode()) // pass generated OTP here
					.create();

			System.out.println(verificationCheck.getStatus());

			if(!verificationCheck.getStatus().equals("approved"))
				throw new BadRequestException("Code is expired or not correct!");

			Password password = passwordRepository.save(new Password(passwordEncoder.encode(resetPasswordDTO.getNewPassword())));
			user.getPasswords().add(password);
			userRepository.save(user);

		} catch (Exception e) {
			System.out.println("GRESKAAAAAAAAAAAAAAAAA");
			throw new BadRequestException("Code is expired or not correct!");
		}
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return this.userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User with email '%s' is not found!", email)));
	}


	private void saveResetPassword(User user, String code) {
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
}

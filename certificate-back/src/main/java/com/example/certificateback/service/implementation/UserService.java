package com.example.certificateback.service.implementation;

import com.example.certificateback.controller.UserController;
import com.example.certificateback.domain.*;
import com.example.certificateback.dto.ErrorDTO;
import com.example.certificateback.dto.LoginDTO;
import com.example.certificateback.dto.UserDTO;
import com.example.certificateback.exception.BadRequestException;
import com.example.certificateback.exception.NotFoundException;
import com.example.certificateback.repository.*;
import com.example.certificateback.service.interfaces.IUserService;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sendgrid.*;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.example.certificateback.dto.ResetPasswordDTO;
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.io.IOException;

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
	ILoginVerificationRepository loginVerificationRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Override
	public User findByEmail(String email) throws UsernameNotFoundException {
		return userRepository.findByEmail(email).orElseThrow(()
				-> new UsernameNotFoundException(String.format("User with email '%s' is not found!", email)));
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
			logger.error("User with that email already exists.");
			throw new BadRequestException("User with that email already exists!");
		}
		if (this.userRepository.existsByPhone(registrationDTO.getPhone())) {
			logger.error("User with that phone number already exists.");
			throw new BadRequestException("User with that phone number already exists!");
		}
		User user = new User(registrationDTO);
		user.setEnabled(false);

		Password password = passwordRepository.save(new Password(passwordEncoder.encode(registrationDTO.getPassword())));
		user.getPasswords().add(password);

		List<Role> roles = new ArrayList<>();
		roles.add(roleRepository.findById(1L).get());
		user.setRoles(roles);
		logger.info("Registration request submitted successfully.");
		user = userRepository.save(user);

		UserActivation activation = userActivationRepository.save(new UserActivation(user));

		if(registrationDTO.getVerification().equals("email")){
			try {
				sendActivationEmail(activation);
			} catch (MessagingException | UnsupportedEncodingException | javax.mail.MessagingException e) {
				logger.error("Error occurred while sending activation email.");
				throw new RuntimeException(e);
			}
		} else{
			logger.info("Trying to send activation SMS.");
			sendActivationSMS(activation);
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
		try {
			UserActivation activation = userActivationRepository.findById(activationId).orElseThrow(
					() -> new NotFoundException("Activation with entered id does not exist!"));
			User p = findUserById(activation.getUser().getId());
			if (new Date().before(new Date(activation.getDate().getTime() + activation.getLife() * 1000L))) {
				p.setEnabled(true);
				userRepository.save(p);
				userActivationRepository.delete(activation);
				logger.info("Successful account activation.");
				return new ErrorDTO("Successful account activation!");
			} else {
				userActivationRepository.delete(activation);
				passwordRepository.delete(p.getPasswords().get(0));
				userRepository.delete(p);
				throw new BadRequestException("Activation expired. Register again!");
			}
		}
		catch (NotFoundException | BadRequestException e) {
			if (e.getClass().getName().equals("NotFoundException")) logger.error("Activation with entered id does not exist.");
			else logger.error("Activation expired.");
			throw e;
		}
	}

	@Override
	public void sendResetEmail(String email) {
		try {
			userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User does not exist!"));

			Twilio.init(System.getenv("TWILIO_ACCOUNT_SID"), System.getenv("TWILIO_AUTH_TOKEN"));

			Verification.creator("VA7bc0fdf60508827d48fd33d1cf64a6e2", // this is your verification sid
							"kvucic6@gmail.com", // recipient email address
							"email") // this is your channel type
					.create();
		} catch (NotFoundException e) {
			logger.error("User does not exist.");
			throw e;
		}

		logger.info("Reset email sent successfully.");
	}

	@Override
	public void resetEmail(String email, ResetPasswordDTO resetPasswordDTO) {
		// TODO : test this
		User user;
		try {
			user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User does not exist!"));
		}
		catch (NotFoundException e) {
			logger.error("User does not exist.");
			throw e;
		}
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

		this.isPreviousPassword(user, resetPasswordDTO.getNewPassword());

		try {
			VerificationCheck.creator("VA7bc0fdf60508827d48fd33d1cf64a6e2") // pass verification SID here
					.setTo("kvucic6@gmail.com")
					.setCode(resetPasswordDTO.getCode()) // pass generated OTP here
					.create();

			Password password = passwordRepository.save(new Password(passwordEncoder.encode(resetPasswordDTO.getNewPassword())));
			user.getPasswords().add(password);
			userRepository.save(user);

		} catch (Exception e) {
			logger.error("Verification via email failed.");
			throw new BadRequestException("Verification failed.");
		}
		logger.info("Reset password email sent successfully.");
	}

	@Override
	public void sendSMS(String phone) {
		try {
			userRepository.findByPhone(phone).orElseThrow(() -> new NotFoundException("User does not exist!"));

			Twilio.init(System.getenv("TWILIO_ACCOUNT_SID"), System.getenv("TWILIO_AUTH_TOKEN"));

			Verification.creator("VAe0c3ba1e13e3da10bd89949823f7715a", // this is your verification sid
							"+381621444147", // recipient phone number
							"sms") // this is your channel type
					.create();
		}
		catch (NotFoundException e) {
			logger.error("User does not exist.");
			throw e;
		}
	}

	@Override
	public void checkSMS(String phone, ResetPasswordDTO resetPasswordDTO) {
		User user = userRepository.findByPhone(phone).orElseThrow(() -> new NotFoundException("User does not exist!"));

		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

		this.isPreviousPassword(user, resetPasswordDTO.getNewPassword());

		try {
			VerificationCheck.creator("VAe0c3ba1e13e3da10bd89949823f7715a") // pass verification SID here
					.setTo("+381621164208")
					.setCode(resetPasswordDTO.getCode()) // pass generated OTP here
					.create();

			Password password = passwordRepository.save(new Password(passwordEncoder.encode(resetPasswordDTO.getNewPassword())));
			user.getPasswords().add(password);
			userRepository.save(user);

		} catch (Exception e) {
			logger.error("SMS verification failed.");
			throw new BadRequestException("Verification failed.");
		}
		logger.info("Verification successful.");
	}

	@Override
	public void checkLogin(LoginDTO loginDTO) {
		//brisanje svih dosadasnjih za korisnika (zato da bi uvijek postojao jedan ili nijedan u bazi)
		List<LoginVerification>  all = loginVerificationRepository.findAll();
		for (LoginVerification v : all){
			if (v.getUser().getEmail().equals(loginDTO.getEmail()))
				loginVerificationRepository.delete(v);
		}

		User user = userRepository.findByEmail(loginDTO.getEmail()).get();
		LoginVerification loginVerification = loginVerificationRepository.save(new LoginVerification(user));
		if(loginDTO.getVerification().equals("email")){
			logger.info("Sending login email...");
			sendLoginEmail(loginVerification);
		} else {
			logger.info("Sending login SMS...");
			sendLoginSMS(loginVerification);
		}
	}

	@Override
	public void confirmLogin(LoginDTO loginDTO) {
		LoginVerification verification = loginVerificationRepository.findByUserEmail(loginDTO.getEmail()).
				orElseThrow(() -> new NotFoundException(String.format("User with email '%s' is not found!", loginDTO.getEmail())));

		if (!new Date().before(new Date(verification.getDate().getTime() + verification.getLife()*1000L))) {
			loginVerificationRepository.delete(verification);
			logger.error("Verification time expired.");
			throw new BadRequestException("Verification time expired. Login again!");
		}
		else if (!verification.getCode().equals(loginDTO.getVerification())){
			loginVerificationRepository.delete(verification);
			logger.error("The code for login is not correct.");
			throw new BadRequestException("Code not correct. Login again!");
		}else{
			logger.info("Login successful.");
			loginVerificationRepository.delete(verification);
		}
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return this.userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User with email '%s' is not found!", email)));
	}

	private void sendLoginSMS(LoginVerification verification) {
		Twilio.init(System.getenv("TWILIO_ACCOUNT_SID_2"), System.getenv("TWILIO_AUTH_TOKEN_2"));

		String text = "Hello [[name]], \n"
				+ "Your login verification code is:\n "
				+ "[[code]]\n"
				+ "Thank you, \n"
				+ "Your Certificate Manager Team.";

		text = text.replace("[[name]]", verification.getUser().getName());
		text = text.replace("[[code]]", verification.getCode());

		Message.creator(new PhoneNumber("+381612325345"),
				new PhoneNumber("+12708131194"), text).create();
	}

	private void sendLoginEmail(LoginVerification verification) {
		Email from = new Email("savic.sv7.2020@uns.ac.rs");
		Email to = new Email("kvucic6@gmail.com");
		Mail mail = new Mail();
		// we create an object of our static class feel free to change the class on its own file
		DynamicTemplatePersonalization personalization = new DynamicTemplatePersonalization();
		personalization.addTo(to);
		mail.setFrom(from);
		mail.setSubject("Verify login");
		personalization.addDynamicTemplateData("user", verification.getUser().getName() + " " + verification.getUser().getSurname());
		personalization.addDynamicTemplateData("code", verification.getCode());
		mail.addPersonalization(personalization);
		mail.setTemplateId("d-e1877410ec904fe3ae55af39bb917368");
		// this is the api key
		SendGrid sg = new SendGrid("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");
		Request request = new Request();

		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			sg.api(request);
		} catch (IOException ex) {
			logger.error("Error occurred, while sending login email.");
			System.out.println(ex);
		}
	}

	private void sendActivationEmail(UserActivation activation) throws MessagingException, UnsupportedEncodingException, javax.mail.MessagingException {
		User user = userRepository.findByEmail(activation.getUser().getEmail()).orElseThrow(()
				-> new NotFoundException("User does not exist!"));

		Email from = new Email("savic.sv7.2020@uns.ac.rs");
		Email to = new Email("kvucic6@gmail.com");
		Mail mail = new Mail();
		// we create an object of our static class feel free to change the class on its own file
		DynamicTemplatePersonalization personalization = new DynamicTemplatePersonalization();
		personalization.addTo(to);
		mail.setFrom(from);
		mail.setSubject("Activate Your CM Account");
		personalization.addDynamicTemplateData("user", user.getName() + " " + user.getSurname());
		personalization.addDynamicTemplateData("link", "http://localhost:4200/activation/"+activation.getId().toString());
		mail.addPersonalization(personalization);
		mail.setTemplateId("d-1753ed2fbd874302b80910e8ad3b9186");
		// this is the api key
		SendGrid sg = new SendGrid("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");
		Request request = new Request();

		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			sg.api(request);
		} catch (IOException ex) {
			System.out.println(ex);
		}
	}

	private void sendActivationSMS(UserActivation activation) {
		User user = userRepository.findByPhone(activation.getUser().getPhone())
				.orElseThrow(() -> new NotFoundException("User does not exist!"));

		Twilio.init(System.getenv("TWILIO_ACCOUNT_SID_2"), System.getenv("TWILIO_AUTH_TOKEN_2"));

		String text = "Hello [[name]], thank you for joining us!\n"
				+ "To activate your account please follow this link: "
				+ "http://localhost:4200/activation/[[id]]'\n"
				+ "The Certificate Manager team.";

		text = text.replace("[[name]]", user.getName());
		text = text.replace("[[id]]", activation.getId().toString());

		Message.creator(new PhoneNumber("+381612325345"),
				new PhoneNumber("+12708131194"), text).create();
	}

	private void isPreviousPassword(User user, String password) {
		for (Password p: user.getPasswords()) {
			if(passwordEncoder.matches(password, p.getPassword())) {
				logger.error("New password is not unique.");
				throw new BadRequestException("Password must be unique!");
			}
		}
	}

	// This class handles the dynamic data for the template
	private static class DynamicTemplatePersonalization extends Personalization {

		@JsonProperty(value = "dynamic_template_data")
		private Map<String, String> dynamic_template_data;

		@JsonProperty("dynamic_template_data")
		public Map<String, String> getDynamicTemplateData() {
			if (dynamic_template_data == null) {
				return Collections.<String, String>emptyMap();
			}
			return dynamic_template_data;
		}

		public void addDynamicTemplateData(String key, String value) {
			if (dynamic_template_data == null) {
				dynamic_template_data = new HashMap<String, String>();
				dynamic_template_data.put(key, value);
			} else {
				dynamic_template_data.put(key, value);
			}
		}

	}
}

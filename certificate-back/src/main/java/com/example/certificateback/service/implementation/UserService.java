package com.example.certificateback.service.implementation;

import com.example.certificateback.domain.Role;
import com.example.certificateback.domain.User;
import com.example.certificateback.domain.UserActivation;
import com.example.certificateback.dto.RegistrationDTO;
import com.example.certificateback.repository.IRoleRepository;
import com.example.certificateback.repository.IUserActivationRepository;
import com.example.certificateback.repository.IUserRepository;
import com.example.certificateback.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
	private IRoleRepository roleRepository;

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
	public List<User> findAll() throws AccessDeniedException {
		return userRepository.findAll();
	}

	@Override
	public User register(RegistrationDTO registrationDTO) {
//		if (this.userRepository.findByEmail(registrationDTO.getEmail())) {
//			throw new BadRequestException("User with that email already exists!");
//		}

		User user = new User(registrationDTO);
		
		user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
		user.setEnabled(false);

		List<Role> roles = new ArrayList<>();
		roles.add(roleRepository.findById(1L).get());
		user.setRoles(roles);

		user = userRepository.save(user);
		UserActivation activation = userActivationRepository.save(new UserActivation(passenger, new Date(), 180));
		//sendActivationEmail(activation);
		return user;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return this.userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User with email '%s' is not found!", email)));
	}

}

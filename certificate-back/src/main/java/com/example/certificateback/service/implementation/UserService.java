package com.example.certificateback.service.implementation;

import com.example.certificateback.domain.Role;
import com.example.certificateback.domain.User;
import com.example.certificateback.dto.RegistrationDTO;
import com.example.certificateback.repository.IUserRepository;
import com.example.certificateback.service.RoleService;
import com.example.certificateback.service.interfaces.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService, UserDetailsService {

	@Autowired
	private IUserRepository userRepository;

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
	public User save(RegistrationDTO registrationDTO) {
		User u = new User();
		u.setEmail(registrationDTO.getEmail());
		
		// pre nego sto postavimo lozinku u atribut hesiramo je kako bi se u bazi nalazila hesirana lozinka
		// treba voditi racuna da se koristi isi password encoder bean koji je postavljen u AUthenticationManager-u kako bi koristili isti algoritam
		u.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));

		u.setEnabled(true);
		u.setEmail(registrationDTO.getEmail());

		List<Role> roles = roleService.findByName("ROLE_USER");
		u.setRoles(roles);
		
		return this.userRepository.save(u);
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return this.userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException(String.format("User with email '%s' is not found!", email)));
	}

}

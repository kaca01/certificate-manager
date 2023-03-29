package com.example.certificateback.service.interfaces;

import com.example.certificateback.domain.Role;

import java.util.List;

public interface IRoleService {
	Role findById(Long id);
	List<Role> findByName(String name);
}

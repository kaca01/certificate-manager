package com.example.certificateback.service;

import com.example.certificateback.domain.Role;

import java.util.List;

public interface IRoleService {
	Role findById(Long id);
	List<Role> findByName(String name);
}

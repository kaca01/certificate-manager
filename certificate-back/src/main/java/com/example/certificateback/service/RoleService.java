package com.example.certificateback.service;

import com.example.certificateback.domain.Role;
import com.example.certificateback.repository.IRoleRepository;
import com.example.certificateback.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService implements IRoleService {

  @Autowired
  private IRoleRepository roleRepository;

  @Override
  public Role findById(Long id) {
    return this.roleRepository.findById(id).orElse(null);
  }

  @Override
  public List<Role> findByName(String name) {
    return this.roleRepository.findByName(name);
  }
}

package com.inventory.system.role.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.inventory.system.role.entity.Role;
import com.inventory.system.role.repository.RoleRepository;
import com.inventory.system.exception.ResourceNotFoundException;

@Service
public class RoleService {

	private final RoleRepository roleRepository;

	public RoleService(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	public List<Role> getAllRoles() {
		return roleRepository.findAll();
	}

	public Role createRole(Role role) {
		return roleRepository.save(role);
	}

	public Role updateRole(Long id, Role role) {

		Role existingRole = roleRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Role not found"));

		existingRole.setName(role.getName());

		return roleRepository.save(existingRole);
	}

	public void deleteRole(Long id) {

		Role role = roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role not found"));

		roleRepository.delete(role);
	}
}
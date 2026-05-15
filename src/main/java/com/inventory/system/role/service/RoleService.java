package com.inventory.system.role.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.system.exception.ResourceNotFoundException;
import com.inventory.system.role.entity.Role;
import com.inventory.system.role.repository.RoleRepository;

@Service
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {

        return roleRepository.findAll();
    }

    public Role createRole(Role role) {

        roleRepository.findByName(role.getName())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Role already exists");
                });

        return roleRepository.save(role);
    }

    public Role updateRole(Long id, Role role) {

        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role not found"));

        existingRole.setName(role.getName());

        return roleRepository.save(existingRole);
    }

    public void deleteRole(Long id) {

        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role not found");
        }

        roleRepository.deleteById(id);
    }
}
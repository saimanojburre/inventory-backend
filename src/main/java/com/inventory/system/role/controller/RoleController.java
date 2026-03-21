package com.inventory.system.role.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.inventory.system.role.entity.Role;
import com.inventory.system.role.service.RoleService;

@RestController
@RequestMapping("/roles")
public class RoleController {

	private final RoleService roleService;

	public RoleController(RoleService roleService) {
		this.roleService = roleService;
	}

	@GetMapping
	public List<Role> getRoles() {
		return roleService.getAllRoles();
	}

	@PostMapping
	public Role createRole(@RequestBody Role role) {
		return roleService.createRole(role);
	}

	@PutMapping("/{id}")
	public Role updateRole(@PathVariable Long id, @RequestBody Role role) {

		return roleService.updateRole(id, role);
	}

	@DeleteMapping("/{id}")
	public String deleteRole(@PathVariable Long id) {

		roleService.deleteRole(id);

		return "Role deleted";
	}
}
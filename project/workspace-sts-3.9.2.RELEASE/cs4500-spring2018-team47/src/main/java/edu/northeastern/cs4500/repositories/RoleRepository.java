package edu.northeastern.cs4500.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.northeastern.cs4500.model.user.Role;

/**
 * Repository for Roles.
 *
 */
public interface RoleRepository extends JpaRepository<Role, Integer>{
	Role findByRole(String role);

}
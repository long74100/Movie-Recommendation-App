package edu.northeastern.cs4500.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.northeastern.cs4500.model.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
	 User findByEmail(String email);
	 User findByUsername(String username);
	 boolean existsByEmail(String email);
}
package edu.northeastern.cs4500.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.northeastern.cs4500.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	 User findByEmail(String email);
}
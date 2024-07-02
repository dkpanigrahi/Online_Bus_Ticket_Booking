package com.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	public User findByEmail(String email);
	
	public boolean existsByEmail(String email);
}

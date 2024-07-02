package com.demo.service;

import com.demo.entity.User;

public interface UserService {

	public User saveuser(User user);
	
	public boolean existEmailCheck(String email);
	
	public void deleteSessionMessage();
}

package com.recon.dao;

import java.util.List;

import com.recon.entity.UserInfo;

public interface UserDao {
	public String insertUser(UserInfo user);

	public UserInfo update(UserInfo user);

	public List<UserInfo> getAllUsers();

	public UserInfo findByUserName(String userName);

	public int deleteUser(String userName);

	public UserInfo findByUserEmail(String email);

	public boolean isUsernameTaken(String username);

	public boolean isEmailAreadyRegistered(String email);

	public List<String> searchUser(String serachCriteria);

	public List<String> getAllUsernames();

}

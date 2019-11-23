package com.ischoolbar.programmer.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ischoolbar.programmer.dao.UserDao;
import com.ischoolbar.programmer.entity.User;
import com.ischoolbar.programmer.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserDao userdao;
	
	@Override
	public User findByUserName(String username) {
		
		return userdao.findByUserName(username);
	}
	@Override
	public int add(User user) {
		return userdao.add(user);
	}

	@Override
	public List<User> findList(Map<String,Object> queryMap) {
		
		return userdao.findList(queryMap);
	}

	@Override
	public int getTotal(Map<String, Object> queryMap) {
		return userdao.getTotal(queryMap);
	}

	@Override
	public int edit(User user) {
		return userdao.edit(user);
	}
	@Override
	public int delete(String ids) {
		return userdao.delete(ids);
	}

	

	

}

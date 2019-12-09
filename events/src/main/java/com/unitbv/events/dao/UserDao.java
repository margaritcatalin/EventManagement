package com.unitbv.events.dao;

import java.util.List;

import com.unitbv.events.model.User;

public interface UserDao extends GenericDAO<User> {
	public void close();

	public User createOrUpdate(User entity);

	public User findById(int id);

	public User findByEmail(String email);
	
	public User update(User entity);

	public void delete(User entity);

	public void deleteAll();

	public List<User> readAll();
	
}

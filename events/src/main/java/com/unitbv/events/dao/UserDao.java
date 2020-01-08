package com.unitbv.events.dao;

import java.util.List;

import com.unitbv.events.model.User;

public interface UserDao extends GenericDAO<User> {
	 void close();

	 User createOrUpdate(User entity);

	 User findById(int id);

	 User findByEmail(String email);
	
	 User update(User entity);

	 void delete(User entity);

	 void deleteAll();

	 List<User> readAll();
	
}

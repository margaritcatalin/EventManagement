package com.unitbv.events.dao;

import java.util.List;

import com.unitbv.events.model.Role;

public interface RoleDao extends GenericDAO<Role> {
	 void close();

	 Role createOrUpdate(Role entity);

	 Role findById(int id);

	 Role update(Role entity);

	 void delete(Role entity);

	 void deleteAll();

	 List<Role> readAll();
	
	 Role findByRoleName(String roleName);
}

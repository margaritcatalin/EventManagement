package com.unitbv.events.dao;

import java.util.List;

import com.unitbv.events.model.Role;

public interface RoleDao extends GenericDAO<Role> {
	public void close();

	public Role createOrUpdate(Role entity);

	public Role findById(int id);

	public Role update(Role entity);

	public void delete(Role entity);

	public void deleteAll();

	public List<Role> readAll();
	
	public Role findByRoleName(String roleName);
}

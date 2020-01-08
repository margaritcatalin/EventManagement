package com.unitbv.events.dao;

import java.util.List;

public interface GenericDAO<T> {
	 void close();

	 T createOrUpdate(T entity);

	 T findById(int id);

	 T update(T entity);

	 void delete(T entity);

	 void deleteAll();

	 List<T> readAll();

}
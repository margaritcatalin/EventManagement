package com.unitbv.events.dao.impl;

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.unitbv.events.dao.UserDao;
import com.unitbv.events.model.User;
import com.unitbv.events.util.ConnectionManager;

public class DefaultUserDao implements UserDao {
	ConnectionManager conManager;
	EntityManager em;

	public DefaultUserDao(String persistenceUnitName) {
		conManager = new ConnectionManager(persistenceUnitName);
	}

	public void close() {
		conManager.close();
	}

	public User createOrUpdate(User entity) {
		try {
			em = conManager.getEMFactory().createEntityManager();
			try {
				em.getTransaction().begin();
				em.merge(entity);
				em.getTransaction().commit();
			} catch (Exception ex) {
				em.getTransaction().rollback();
				em.getTransaction().begin();
				entity = em.merge(entity);
				em.getTransaction().commit();
			}
			return entity;
		} catch (Exception ex) {
			ex.printStackTrace();
			em.getTransaction().rollback();
			return null;
		} finally {
			em.close();
		}
	}

	public User findById(int id) {
		try {
			em = conManager.getEMFactory().createEntityManager();
			return em.find(User.class, id);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			em.close();
		}
	}

	public User findByEmail(String email) {
		try {
			em = conManager.getEMFactory().createEntityManager();
			Query query = em.createQuery("Select u FROM User u WHERE u.email = :email");
			query.setParameter("email", email);
			if(Objects.nonNull(query.getSingleResult()))
			{
				return (User) query.getSingleResult();
			}
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			em.close();
		}
	}

	public User update(User entity) {
		try {
			em = conManager.getEMFactory().createEntityManager();
			em.getTransaction().begin();
			em.merge(entity);
			em.getTransaction().commit();
			return entity;
		} catch (Exception ex) {
			ex.printStackTrace();
			em.getTransaction().rollback();
			return null;
		} finally {
			em.close();
		}
	}

	public void delete(User entity) {
		try {
			em = conManager.getEMFactory().createEntityManager();
			em.getTransaction().begin();

			entity = em.find(User.class, entity.getUserId());
			em.remove(entity);

			em.getTransaction().commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			em.getTransaction().rollback();
		} finally {
			em.close();
		}
	}

	public void deleteAll() {
		try {
			for (User entity : readAll()) {
				delete(entity);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public List<User> readAll() {
		try {
			em = conManager.getEMFactory().createEntityManager();

			return em.createQuery("FROM User u", User.class).getResultList();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			em.close();
		}
	}

}

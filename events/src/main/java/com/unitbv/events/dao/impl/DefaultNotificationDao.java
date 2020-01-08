package com.unitbv.events.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;

import com.unitbv.events.dao.NotificationDao;
import com.unitbv.events.model.Notification;
import com.unitbv.events.util.ConnectionManager;

public class DefaultNotificationDao implements NotificationDao {
	ConnectionManager conManager;
	EntityManager em;

	public DefaultNotificationDao(String persistenceUnitName) {
		conManager = new ConnectionManager(persistenceUnitName);
	}

	@Override
	public void close() {
		conManager.close();
	}

	@Override
	public Notification createOrUpdate(Notification entity) {
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

	@Override
	public Notification findById(int id) {
		try {
			em = conManager.getEMFactory().createEntityManager();
			return em.find(Notification.class, id);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			em.close();
		}
	}

	@Override
	public Notification update(Notification entity) {
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

	@Override
	public void delete(Notification entity) {
		try {
			em = conManager.getEMFactory().createEntityManager();
			em.getTransaction().begin();

			entity = em.find(Notification.class, entity.getNotificationId());
			em.remove(entity);

			em.getTransaction().commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			em.getTransaction().rollback();
		} finally {
			em.close();
		}
	}

	@Override
	public void deleteAll() {
		try {
			for (Notification entity : readAll()) {
				delete(entity);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public List<Notification> readAll() {
		try {
			em = conManager.getEMFactory().createEntityManager();

			return em.createQuery("from Notification n", Notification.class).getResultList();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			em.close();
		}
	}

}

package com.unitbv.events.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.unitbv.events.dao.InvitationDao;
import com.unitbv.events.model.Event;
import com.unitbv.events.model.Invitation;
import com.unitbv.events.model.Notification;
import com.unitbv.events.model.User;
import com.unitbv.events.util.ConnectionManager;

public class DefaultInvitationDao implements InvitationDao {
	ConnectionManager conManager;
	EntityManager em;

	public DefaultInvitationDao(String persistenceUnitName) {
		conManager = new ConnectionManager(persistenceUnitName);
	}

	@Override
	public void close() {
		conManager.close();
	}

	@Override
	public Invitation createOrUpdate(Invitation entity) {
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
	public Invitation findById(int id) {
		try {
			em = conManager.getEMFactory().createEntityManager();
			return em.find(Invitation.class, id);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			em.close();
		}
	}

	@Override
	public Invitation update(Invitation entity) {
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
	public List<Invitation> findByEventAndUser(User user, Event event) {
		try {
			em = conManager.getEMFactory().createEntityManager();
			Query query = em.createQuery("Select i FROM Invitation i WHERE i.user = :user AND i.event = :event",
					Invitation.class);
			query.setParameter("user", user);
			query.setParameter("event", event);
			return query.getResultList();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			em.close();
		}
	}

	@Override
	public void delete(Invitation entity) {
		try {
			em = conManager.getEMFactory().createEntityManager();
			em.getTransaction().begin();

			entity = em.find(Invitation.class, entity.getInvitationId());
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
			for (Invitation entity : readAll()) {
				delete(entity);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public List<Invitation> readAll() {
		try {
			em = conManager.getEMFactory().createEntityManager();

			return em.createQuery("from Invitation i", Invitation.class).getResultList();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			em.close();
		}
	}

	@Override
	public List<Invitation> findByUser(User user) {
		try {
			em = conManager.getEMFactory().createEntityManager();
			Query query = em.createQuery("Select i FROM Invitation i WHERE i.user = :user",
					Notification.class);
			query.setParameter("user", user);
			return query.getResultList();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			em.close();
		}
	}

}

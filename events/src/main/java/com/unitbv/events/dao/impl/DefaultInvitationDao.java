package com.unitbv.events.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;

import com.unitbv.events.dao.InvitationDao;
import com.unitbv.events.model.Invitation;
import com.unitbv.events.util.ConnectionManager;

public class DefaultInvitationDao implements InvitationDao {
	ConnectionManager conManager;
	EntityManager em;

	public DefaultInvitationDao(String persistenceUnitName) {
		conManager = new ConnectionManager(persistenceUnitName);
	}

	public void close() {
		conManager.close();
	}

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

	public void deleteAll() {
		try {
			for (Invitation entity : readAll()) {
				delete(entity);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public List<Invitation> readAll() {
		try {
			em = conManager.getEMFactory().createEntityManager();

			return em.createQuery("from Invitation", Invitation.class).getResultList();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			em.close();
		}
	}

}

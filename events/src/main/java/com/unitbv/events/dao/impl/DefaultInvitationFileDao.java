package com.unitbv.events.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;

import com.unitbv.events.dao.InvitationFileDao;
import com.unitbv.events.model.InvitationFile;
import com.unitbv.events.util.ConnectionManager;

public class DefaultInvitationFileDao implements InvitationFileDao {
	ConnectionManager conManager;
	EntityManager em;

	public DefaultInvitationFileDao(String persistenceUnitName) {
		conManager = new ConnectionManager(persistenceUnitName);
	}

	public void close() {
		conManager.close();
	}

	public InvitationFile createOrUpdate(InvitationFile entity) {
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

	public InvitationFile findById(int id) {
		try {
			em = conManager.getEMFactory().createEntityManager();
			return em.find(InvitationFile.class, id);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			em.close();
		}
	}

	public InvitationFile update(InvitationFile entity) {
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

	public void delete(InvitationFile entity) {
		try {
			em = conManager.getEMFactory().createEntityManager();
			em.getTransaction().begin();

			entity = em.find(InvitationFile.class, entity.getInvitationFileId());
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
			for (InvitationFile entity : readAll()) {
				delete(entity);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public List<InvitationFile> readAll() {
		try {
			em = conManager.getEMFactory().createEntityManager();

			return em.createQuery("from InvitationFile i", InvitationFile.class).getResultList();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			em.close();
		}
	}

}

package com.unitbv.events.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;

import com.unitbv.events.dao.EventDao;
import com.unitbv.events.model.Event;
import com.unitbv.events.util.ConnectionManager;

public class DefaultEventDao implements EventDao {
	ConnectionManager conManager;
	EntityManager em;

	public DefaultEventDao(String persistenceUnitName) {
		conManager = new ConnectionManager(persistenceUnitName);
	}

	@Override
	public void close() {
		conManager.close();
	}

	@Override
	public Event createOrUpdate(Event entity) {
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
	public Event findById(int id) {
		try {
			em = conManager.getEMFactory().createEntityManager();
			return em.find(Event.class, id);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			em.close();
		}
	}

	@Override
	public Event update(Event entity) {
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
	public void delete(Event entity) {
		try {
			em = conManager.getEMFactory().createEntityManager();
			em.getTransaction().begin();

			entity = em.find(Event.class, entity.getEventId());
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
			for (Event entity : readAll()) {
				delete(entity);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public List<Event> readAll() {
		try {
			em = conManager.getEMFactory().createEntityManager();

			return em.createQuery("SELECT e FROM Event e", Event.class).getResultList();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			em.close();
		}
	}

}

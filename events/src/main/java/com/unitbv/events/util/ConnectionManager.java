package com.unitbv.events.util;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class ConnectionManager {
	private static EntityManagerFactory emf = null;
	private String persistenceUnitName;

	public ConnectionManager(String persistenceUnitName) {
		this.persistenceUnitName = persistenceUnitName;
	}

	public EntityManagerFactory getEMFactory() {
		if (emf == null) {
			emf = Persistence.createEntityManagerFactory(persistenceUnitName);
		}
		return emf;
	}

	public void close() {
		if (emf != null && !emf.isOpen()) {
			emf.close();
		}
	}
}
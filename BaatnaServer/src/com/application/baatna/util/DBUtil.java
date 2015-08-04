package com.application.baatna.util;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class DBUtil {

	private static SessionFactory sessionFactory;
//	private static ServiceRegistry serviceRegistry;
	
	private static SessionFactory configureSessionFactory()
			throws HibernateException {
//		 Configuration configuration = new Configuration();
//		 configuration.configure();
//		 serviceRegistry = new ServiceRegistryBuilder().applySettings(
//		 configuration.getProperties()).buildServiceRegistry();
//		 sessionFactory = configuration.buildSessionFactory(serviceRegistry);
//		 return sessionFactory;

		if (sessionFactory == null) {
			Configuration configuration = new Configuration();
			configuration.configure();
			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(
					configuration.getProperties()).buildServiceRegistry();
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);

		}
		return sessionFactory;
	}

	public static SessionFactory getSessionFactory() {
		return configureSessionFactory();

	}

}

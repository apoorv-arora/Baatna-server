package com.application.baatna.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.application.baatna.dao.UserDAO;

public class Bapp implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		if (executorService != null) {
			executorService.shutdown();
		}
		if (executorService2 != null) {
			executorService2.shutdown();
		}
	}

	ScheduledExecutorService executorService;
	ScheduledExecutorService executorService2;

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		executorService = Executors.newScheduledThreadPool(1);
		executorService2 = Executors.newScheduledThreadPool(1);

		executorService.scheduleAtFixedRate(new Runnable() {
			public void run() {
				Bapp zappObject = new Bapp();
				zappObject.archiveOperation();
			}
		}, 0, 15, TimeUnit.MINUTES);
		
		executorService2.scheduleAtFixedRate(new Runnable() {
			public void run() {
				Bapp zappObject = new Bapp();
				zappObject.updateRating();
			}
		}, 0, 24, TimeUnit.HOURS);

	}

	private void archiveOperation() {
		 UserDAO storeItemDao = new UserDAO();
		 storeItemDao.nullifyPushId(-1);
	}

	/**
	 * Computes the average value of the user rating from the rating table and update the user rating
	 * */
	private void updateRating() {
		UserDAO dao = new UserDAO();
		dao.setUserDayRating();
	}

}

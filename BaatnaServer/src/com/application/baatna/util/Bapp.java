
package com.application.baatna.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.application.baatna.dao.UserDAO;

public class Bapp implements ServletContextListener {

	public static final int THREAD_RUN_TIME = 5 * 60 * 1000;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		if (executorService != null) {
			System.out.println("Asynchronous task");
			executorService.shutdown();
		}
	}

	ScheduledExecutorService executorService;

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		executorService = Executors.newScheduledThreadPool(1);

		executorService.scheduleAtFixedRate(new Runnable() {
			public void run() {
				Bapp zappObject = new Bapp();
				zappObject.archiveOperation();
			}
		}, 0, 15, TimeUnit.MINUTES);

	}

	private void archiveOperation() {
		UserDAO storeItemDao = new UserDAO();
		storeItemDao.nullifyPushId(-1);
	}

}

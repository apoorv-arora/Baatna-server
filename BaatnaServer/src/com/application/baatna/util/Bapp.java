
package com.application.baatna.util;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.application.baatna.dao.UserDAO;
import com.application.baatna.dao.WishDAO;

public class Bapp implements ServletContextListener {

	public static final int THREAD_RUN_TIME = 5 * 60 * 1000;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		if (executorService != null) {
			System.out.println("Asynchronous task");
			executorService.shutdown();
		}
		/*if (executorService2 != null) {
			System.out.println("Asynchronous task");
			executorService2.shutdown();
		}*/
	}

	ScheduledExecutorService executorService;
	//ScheduledExecutorService executorService2;

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		executorService = Executors.newScheduledThreadPool(1);
		//executorService=Executors.newScheduledThreadPool(2);

		executorService.scheduleAtFixedRate(new Runnable() {
			public void run() {
				Bapp zappObject = new Bapp();
				zappObject.archiveOperation();
			}
		}, 0, 15, TimeUnit.MINUTES);	
    /* executorService2.scheduleAtFixedRate(new Runnable() {
			public void run(){
				Bapp zappObject= new Bapp();
				zappObject.archiveOperationSecond();
			}
		},0,1,TimeUnit.DAYS);*/

	}

	private void archiveOperation() {
		//UserDAO storeItemDao = new UserDAO();
		//storeItemDao.nullifyPushId(-1);
		UserDAO dao= new UserDAO();
		ArrayList<com.application.baatna.bean.Session> allUsers=dao.getNearbyUsers();
		for(int i=0;i<allUsers.size();i++)
		{
			dao.setUserDayRating(allUsers.get(i).getUserId());
		}
	}
	/*private void archiveOperationSecond() {
		UserDAO dao= new UserDAO();
		ArrayList<com.application.baatna.bean.Session>allUsers=dao.getNearbyUsers();
		for(int i=0;i<allUsers.size();i++)
		{
			dao.setUserDayRating(allUsers.get(i).getUserId());
		}
		
	}
	
*/
}

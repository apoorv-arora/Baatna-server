package com.application.baatna.resource;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jivesoftware.smack.XMPPException;

import com.application.baatna.bean.AllMessages;
import com.application.baatna.dao.MessageDAO;
import com.application.baatna.dao.UserDAO;

@Path("/messaging")
public class Messaging {

	final String GOOGLE_SERVER_KEY = "google";

	@Path("/incoming_message")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes("application/x-www-form-urlencoded")
	public String sendMessage(@FormParam("access_token") String accessToken,
			@FormParam("wishId") int wishId,
			@FormParam("to_userId") int toUserId,
			@FormParam("message") String message) throws XMPPException {

		UserDAO userdao = new UserDAO();

		int fromUserId = userdao.userActive(accessToken);

		if (fromUserId > 0) {

			MessageDAO messagedao = new MessageDAO();
			String messageId = "";

			// getting toPushId of toUserId
			
			String toPushId = userdao.getSessionDetails(toUserId, accessToken).getPushId();

			// sending message touserId and toPushId
//			PushDAO pushdao = new PushDAO();

//			pushdao.connectToGCM();
//
//			messageId = pushdao.sendMessage(GOOGLE_SERVER_KEY, toPushId,
//					message);

			if (!messageId.equals("FAILURE")) {

				Date date = new Date();
				System.out.println(date.toString());

				if (messagedao.addMessage(message, true, date.toString(),
						fromUserId, toUserId, wishId,
						Integer.parseInt(messageId)))
					return "SUCCESS";

			}

			return "FAILURE";

		}

		else
			return "FAILURE";
	}

	@Path("/view_messages")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public AllMessages viewMessages(@FormParam("access_token") String accessToken,
			@FormParam("to_userId") int toUserId) {

		AllMessages allmessages = null;
		UserDAO userdao = new UserDAO();

		int fromUserId = userdao.userActive(accessToken);

		if (fromUserId > 0 && toUserId > 0) {

			MessageDAO messagedao = new MessageDAO();

			allmessages = messagedao.getAllMessages(fromUserId, toUserId);

		}

		return allmessages;
	}

}

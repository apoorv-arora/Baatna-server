package com.application.baatna.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.application.baatna.dao.UserDAO;
import com.application.baatna.util.CommonLib;

@Path("/appConfig")
public class AppConfig extends BaseResource {

	@Path("/version")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject appUpdate(@FormParam("client_id") String clientId, @FormParam("app_type") String appType,
			@FormParam("app_version") String version, @FormParam("access_token") String accessToken) {
		boolean clientTypeAndroid = false;
		// check for client_id
		if (!clientId.equals(CommonLib.ANDROID_CLIENT_ID)) {
			clientTypeAndroid = false;
		} else
			clientTypeAndroid = true;

		// check for app type
		if (!appType.equals(CommonLib.ANDROID_APP_TYPE)) {
			clientTypeAndroid = false;
		} else
			clientTypeAndroid = true;
		try {
			JSONObject appJsonObject = new JSONObject();

			if (clientTypeAndroid) {
				// remove force update checker.
				// if (CommonLib.ANDROID_APP_VERSION.equals(version)) {
				// appJsonObject.put("update", true);
				// } else
				// appJsonObject.put("update", false);
				appJsonObject.put("update", true);
				UserDAO dao = new UserDAO();
				int userId = dao.userActive(accessToken);

				if (userId > 0) {

					JSONArray jsonArr = dao.usersToBeRated(userId);
					appJsonObject.put("rating_list", jsonArr);

					return CommonLib.getResponseString(appJsonObject, "", CommonLib.RESPONSE_SUCCESS);
				} else
					return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_FAILURE);

			} else
				return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_FAILURE);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

}

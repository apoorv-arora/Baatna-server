package com.application.baatna.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jettison.json.JSONObject;

import com.application.baatna.util.CommonLib;

@Path("/appConfig")
public class AppConfig {

	@Path("/version")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject userSignup(@FormParam("client_id") String clientId, @FormParam("app_type") String appType) {
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

		if (clientTypeAndroid) {
			return CommonLib.getResponseString(CommonLib.ANDROID_APP_VERSION, "", CommonLib.RESPONSE_SUCCESS);
		} else
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_FAILURE);
	}

}

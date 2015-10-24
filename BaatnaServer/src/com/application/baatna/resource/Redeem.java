package com.application.baatna.resource;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.application.baatna.bean.Coupon;
import com.application.baatna.bean.Wish;
import com.application.baatna.dao.RedeemDao;
import com.application.baatna.dao.UserDAO;
import com.application.baatna.dao.WishDAO;
import com.application.baatna.util.CommonLib;
import com.application.baatna.util.JsonUtil;

@Path("/redeem")
public class Redeem {

	/**
	 * Create a new wish
	 */
	@Path("/add")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject addRedeem(@FormParam("client_id") String clientId, @FormParam("app_type") String appType,
			@FormParam("validity") String validity, @FormParam("name") String name, @FormParam("terms") String terms,
			@FormParam("count") int count, @FormParam("image") String image) {

		// null checks, invalid request
		if (clientId == null || appType == null)
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_PARAMS);

		// check for client_id
		if (!clientId.equals(CommonLib.ANDROID_CLIENT_ID))
			return CommonLib.getResponseString("Invalid client id", "", CommonLib.RESPONSE_INVALID_CLIENT_ID);

		// check for app type
		if (!appType.equals(CommonLib.ANDROID_APP_TYPE))
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_APP_TYPE);

		Coupon mCoupon = new Coupon();
		mCoupon.setCount(count);
		mCoupon.setImage(image);
		// mCoupon.setLimit(limit);
		mCoupon.setName(name);
		mCoupon.setTerms(terms);
		mCoupon.setValidity(validity);
		RedeemDao redeemDao = new RedeemDao();
		mCoupon = redeemDao.addCouponDetails(mCoupon);

		if (mCoupon != null) {
			try {
				return CommonLib.getResponseString(JsonUtil.getCouponDetails(mCoupon), "", CommonLib.RESPONSE_SUCCESS);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return CommonLib.getResponseString("", "Could not be added", CommonLib.RESPONSE_FAILURE);
		} else
			return CommonLib.getResponseString("", "Could not be added", CommonLib.RESPONSE_FAILURE);
	}

	@Path("/get")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject getCoupons(@FormParam("client_id") String clientId, @FormParam("app_type") String appType,
			@FormParam("access_token") String accessToken, @QueryParam("start") int start,
			@QueryParam("count") int count) {

		// null checks, invalid request
		if (clientId == null || appType == null)
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_PARAMS);

		// check for client_id
		if (!clientId.equals(CommonLib.ANDROID_CLIENT_ID))
			return CommonLib.getResponseString("Invalid client id", "", CommonLib.RESPONSE_INVALID_CLIENT_ID);

		// check for app type
		if (!appType.equals(CommonLib.ANDROID_APP_TYPE))
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_APP_TYPE);

		UserDAO userDao = new UserDAO();

		// access token validity
		int userId = userDao.userActive(accessToken);

		if (userId > 0) {

			RedeemDao redeemDao = new RedeemDao();
			Object[] couponList = redeemDao.getAllCoupons(userId, start, count);
			List<Coupon> coupons = (List<Coupon>) couponList[0];
			int quantity = (int) couponList[1];
			JSONObject returnObject = new JSONObject();
			try {
				JSONArray jsonArr = new JSONArray();
				for (Coupon coupon : coupons) {
					jsonArr.put(JsonUtil.getCouponDetails(coupon));
				}
				returnObject.put("coupons", jsonArr);
				returnObject.put("quantity", quantity);
			} catch (JSONException e) {

			}
			return CommonLib.getResponseString(returnObject, "success", CommonLib.RESPONSE_SUCCESS);
		} else
			return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);
	}

	@Path("/update")
	@POST
	@Produces("application/json")
	@Consumes("application/x-www-form-urlencoded")
	public JSONObject updateRedeem(@FormParam("client_id") String clientId, @FormParam("app_type") String appType,
			@FormParam("access_token") String accessToken, @FormParam("couponId") int couponId) {

		// null checks, invalid request
		if (clientId == null || appType == null)
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_PARAMS);

		// check for client_id
		if (!clientId.equals(CommonLib.ANDROID_CLIENT_ID))
			return CommonLib.getResponseString("Invalid client id", "", CommonLib.RESPONSE_INVALID_CLIENT_ID);

		// check for app type
		if (!appType.equals(CommonLib.ANDROID_APP_TYPE))
			return CommonLib.getResponseString("Invalid params", "", CommonLib.RESPONSE_INVALID_APP_TYPE);

		UserDAO userDao = new UserDAO();

		// access token validity
		int userId = userDao.userActive(accessToken);

		if (userId > 0) {

			RedeemDao redeemDao = new RedeemDao();
			boolean result = redeemDao.updateCouponOnRedeem(userId, couponId);
			if(result)
				return CommonLib.getResponseString("success", "success", CommonLib.RESPONSE_SUCCESS);
			else
				return CommonLib.getResponseString("failure", "failure", CommonLib.RESPONSE_FAILURE);
		} else
			return CommonLib.getResponseString("failure", "", CommonLib.RESPONSE_FAILURE);
	}
}

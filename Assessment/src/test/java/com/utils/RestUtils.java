package com.utils;

import org.json.JSONObject;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class RestUtils {

	public JSONObject getJsonReponseFromRestApi(String applicationUrl) {
		RestAssured.baseURI = applicationUrl;
		RequestSpecification httpRequest = RestAssured.given();
		Response response = httpRequest.get();
		int statusCode = response.getStatusCode();
		
		return new JSONObject(response.asString());
	}

}

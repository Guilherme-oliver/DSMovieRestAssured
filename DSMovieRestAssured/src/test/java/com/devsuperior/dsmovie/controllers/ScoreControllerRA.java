package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

import io.restassured.http.ContentType;
import org.json.JSONException;

import com.devsuperior.dsmovie.tests.TokenUtil;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class ScoreControllerRA {

	private String clientUsername, clientPassword, adminUsername, adminPassword;
	private String clientToken, adminToken, invalidToken;
	private Long existingMovieId, nonExistingMovieId;

	private Map<String, Object> postScoreInstance = new HashMap<>();

	@BeforeEach
	public void setUp() throws JSONException {
		baseURI = "http://localhost:8080";

		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		adminUsername = "maria@gmail.com";
		adminPassword = "123456";

		existingMovieId = 1L;
		nonExistingMovieId = 100L;

		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		invalidToken = adminToken + "xpto";

		postScoreInstance.put("movieId", 1L);
		postScoreInstance.put("score", 4);
	}

	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {
		postScoreInstance.put("movieId", nonExistingMovieId);
		postScoreInstance.put("score", 4);
		JSONObject newScore = new JSONObject(postScoreInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.body(newScore.toString())
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.put("/scores")
				.then()
				.statusCode(404);
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
		postScoreInstance.put("movieId", null);
		postScoreInstance.put("score", 4);
		JSONObject newScore = new JSONObject(postScoreInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.body(newScore.toString())
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.put("/scores")
				.then()
				.statusCode(422);
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {
		postScoreInstance.put("movieId", existingMovieId);
		postScoreInstance.put("score", -5);
		JSONObject newScore = new JSONObject(postScoreInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.body(newScore.toString())
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.put("/scores")
				.then()
				.statusCode(422);
	}
}
package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;

import io.restassured.http.ContentType;


import com.devsuperior.dsmovie.tests.TokenUtil;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class MovieControllerRA {

	private Long existingMovieId;
	private Long nonExistingMovieId;
	private String title;
	private String clientUsername;
	private String clientPassword;
	private String adminUsername;
	private String adminPassword;
	private String clientToken;
	private String adminToken;
	private String invalidToken;

	private Map<String, Object> postMovieInstance = new HashMap<>();

	@BeforeEach
	public void setUp() throws JSONException {
		baseURI = "http://localhost:8080";

		existingMovieId = 1L;
		nonExistingMovieId = 100L;
		title = "The Witcher";

		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		adminUsername = "maria@gmail.com";;
		adminPassword = "123456";

		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		invalidToken = adminToken + "xpto";

		postMovieInstance.put("title", "Test Movie");
		postMovieInstance.put("score", 4.5);
		postMovieInstance.put("count", 2);
		postMovieInstance.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
	}
	
	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
		given()
				.get("/movies")
				.then()
				.statusCode(200)
				.body("content.size()", is(greaterThan(0)));
	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {
		given()
				.param("title", title)
				.when()
				.get("/movies")
				.then()
				.statusCode(200)
				.body("content.size()", is(greaterThan(0)));
	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {
		given()
				.when()
				.get("/movies/{id}", existingMovieId)
				.then()
				.statusCode(200)
				.body("id", is(existingMovieId.intValue()))
				.body("title", equalTo(title));
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {
		given()
				.when()
				.get("/movies/{id}", nonExistingMovieId)
				.then()
				.statusCode(404);
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() {
		postMovieInstance.put("title", "");
		JSONObject newMovie = new JSONObject(postMovieInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.body(newMovie.toString())
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.post("/movies")
				.then()
				.statusCode(422)
				.body("errors.message[0]", equalTo("Campo requerido"));
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() {
		JSONObject newMovie = new JSONObject(postMovieInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(newMovie.toString())
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.post("/movies")
				.then()
				.statusCode(403);
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() {
		JSONObject newMovie = new JSONObject(postMovieInstance);
		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + invalidToken)
				.body(newMovie)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.post("/movie")
				.then()
				.statusCode(401);
	}
}
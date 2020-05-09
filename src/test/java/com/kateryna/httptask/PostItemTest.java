package com.kateryna.httptask;

import com.kateryna.httptask.commons.EndPoints;
import com.kateryna.httptask.dto.AuthForm;
import com.kateryna.httptask.dto.AuthResponse;
import com.kateryna.httptask.dto.ItemDto;
import com.kateryna.httptask.dto.PostResponse;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class PostItemTest {

    private static RequestSpecification requestSpec;
    private String token;

    @BeforeClass
    public static void createRequestSpecification() {

        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    @Before
    public void loginAndReceiveToken() {
        token = "Bearer " +
                given().contentType(ContentType.JSON)
                        .body(new AuthForm("test", "test"))
                        .when()
                        .post(EndPoints.login).body().as(AuthResponse.class).getAccess_token();
    }

    @Test
    public void when_AccessTokenIsNotProvided_Then_NewItemCannotBeAdded() {
        given().spec(requestSpec)
                .body(new ItemDto("Kateryna", "Lysenko"))
                .when()
                .post(EndPoints.items)
                .then().statusCode(401)
                .body("msg", equalTo("Missing Authorization Header"));
    }

    @Test
    public void when_PostItemWithCorrectData_Then_ItemIsAddedSuccessfully() {

        String itemName = "Kateryna";
        String itemValue = "Lysenko";

        String id = given().spec(requestSpec)
                .header("Authorization", token)
                .body(new ItemDto(itemName, itemValue))
                .when()
                .post(EndPoints.items)
                .then().statusCode(201)
                .extract().body()
                .as(PostResponse.class).getId();

        given().spec(requestSpec)
                .header("Authorization", token)
                .when()
                .get(EndPoints.items + "/" + id)
                .then().statusCode(200)
                .body("items.itemName", equalTo(itemName))
                .body("items.itemValue", equalTo(itemValue));
    }

}
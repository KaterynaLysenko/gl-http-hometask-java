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
import org.junit.Ignore;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class DeleteItemTest {

    private String token;
    private String id;

    private static RequestSpecification requestSpec;

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
                given().spec(requestSpec)
                        .body(new AuthForm("test", "test"))
                        .when()
                        .post(EndPoints.login).body().as(AuthResponse.class).getAccess_token();
    }

    @Before
    public void postItemToBeDeleted() {
        id = given().spec(requestSpec)
                .header("Authorization", token)
                .body(new ItemDto("Test", "Delete"))
                .when()
                .post(EndPoints.items)
                .then().statusCode(201)
                .extract().body().as(PostResponse.class).getId();
    }

    @Test
    public void when_AccessTokenIsNotProvided_Then_ItemCannotBeDeleted() {

        given().spec(requestSpec)
                .when()
                .delete(EndPoints.items + "/" + id)
                .then().statusCode(401)
                .body("msg", equalTo("Missing Authorization Header"));
    }

    @Test
    public void when_DeleteItemWithCorrectId_Then_ItemIsDeletedSuccessfully() {

        given().spec(requestSpec)
                .header("Authorization", token)
                .when()
                .delete(EndPoints.items + "/" + id)
                .then().statusCode(200);

        given().spec(requestSpec)
                .header("Authorization", token)
                .when()
                .get(EndPoints.items + "/" + id)
                .then().statusCode(404);

    }


    @Ignore("no information about correct behaviour in this case")
    @Test
    public void when_DeleteItemWithInCorrectId_Then_RequestIsUnsuccessful() {

        given().spec(requestSpec)
                .header("Authorization", token)
                .when()
                .delete(EndPoints.items + "/" + "7n8")
                .then().statusCode(400);
    }


}
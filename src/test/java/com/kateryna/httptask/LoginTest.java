package com.kateryna.httptask;

import com.kateryna.httptask.commons.EndPoints;
import com.kateryna.httptask.dto.AuthForm;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class LoginTest {

    private static RequestSpecification requestSpec;

    @BeforeClass
    public static void createRequestSpecification() {

        requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    @Test
    public void when_TheCredentialsAreValid_Then_LoginIsSuccessful() {

        given().spec(requestSpec)
                .body(new AuthForm("test", "test"))
                .when()
                .post(EndPoints.login)
                .then().statusCode(200);
    }

    @Test
    public void when_TheCredentialsAreInvalid_Then_LoginIsFailed() {
        given().spec(requestSpec)
                .body(new AuthForm("test00", "test00"))
                .when()
                .post(EndPoints.login)
                .then().statusCode(401)
                .and()
                .body("msg", equalTo("Bad username or password"));
    }

    @Test
    public void when_TheCredentialsAreNotProvided_Then_LoginIsFailed() {
        given().spec(requestSpec)
                .body(new AuthForm("", ""))
                .when()
                .post(EndPoints.login)
                .then().statusCode(401)
                .and()
                .body("msg", equalTo("Bad username or password"));
    }

    @Test
    public void when_UsernameIsNotProvided_Then_LoginIsFailed() {
        given().spec(requestSpec)
                .body(new AuthForm("", "test"))
                .when()
                .post(EndPoints.login)
                .then().statusCode(401)
                .and()
                .body("msg", equalTo("Bad username or password"));

    }

    @Test
    public void when_PasswordIsNotProvided_Then_LoginIsFailed() {
        given().spec(requestSpec)
                .body(new AuthForm("test", ""))
                .when()
                .post(EndPoints.login)
                .then().statusCode(401)
                .and()
                .body("msg", equalTo("Bad username or password"));
    }
}
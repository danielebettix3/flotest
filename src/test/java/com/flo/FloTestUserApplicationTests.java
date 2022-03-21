package com.flo;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.flo.controller.GlobalControllerExceptionHandler;
import com.flo.controller.UserController;
import com.flo.entity.User;
import io.restassured.RestAssured;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;


import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.port;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

@Slf4j
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class FloTestUserApplicationTests
{
    @Autowired
    com.flo.config.User login;
    @Autowired
    private WebApplicationContext webApplicationContext;

    User user = new User(null, "Daniele", "Betti", "password", "danielebetti@mailexmpl.com");

    String password = "secure";

    @LocalServerPort
    private int randomServerPort;

    @InjectMocks
    private UserController userController;
    @InjectMocks
    private GlobalControllerExceptionHandler globalControllerExceptionHandler;

    @Before
    public void initialiseRestAssuredMockMvcStandalone()
    {
        RestAssured.port = randomServerPort;

        RestAssuredMockMvc.standaloneSetup(userController, globalControllerExceptionHandler);
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);

        log.info("randomServerPort: "+randomServerPort);

        a_postUser();
    }

    @Test
    public void a_postUser()
    {
        ExtractableResponse<Response> res = RestAssured
                .given().port(randomServerPort)
                .auth().preemptive().basic(login.getUsername(),password)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(user)
                .log().all()
                .post("/users")
                .then()
                .log().ifValidationFails()
                .log().ifError()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                ;
        System.out.println( res );
        user.setId( Long.parseLong( res.path("id").toString() ) );
    }

    @Test
    public void b_putUser()
    {
        user.setPassword("passWord");
        user.setIndirizzo("daniele@mailexample.com");

        RestAssured
                .given().port(randomServerPort)
                .auth().preemptive().basic(login.getUsername(),password)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", user.getId())
                .body(user)
                .log().all()
                .put("/users/{id}")
                .then()
                .log().ifValidationFails()
                .log().ifError()
                .statusCode(HttpStatus.ACCEPTED.value())
        ;
    }

    @Test
    public void c_userById()
    {
        RestAssured
                .given().port(randomServerPort)
                .auth().preemptive().basic(login.getUsername(),password)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", user.getId())
                .log().all()
                .get("/users/{id}")
                .then()
                .log().ifValidationFails()
                .log().ifError()
                .statusCode(HttpStatus.ACCEPTED.value())
        ;
    }

    @Test
    public void d_usersTest()
    {
        RestAssured
                .given().port(randomServerPort)
                .auth().preemptive().basic(login.getUsername(),password)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .log().all()
                .get("/users")
                .then()
                .log().ifValidationFails()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .and()
                .time(lessThan(5L), TimeUnit.SECONDS)
                .and()
                .assertThat()
                .body("users.size()", greaterThan(0))
        ;
    }

    @Test
    public void e_usersBySurnameTest()
    {

        RestAssured
                .given().port(randomServerPort)
                .auth().preemptive().basic(login.getUsername(),password)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("cognome","Betti")
                .log().all()
                .get("/users")
                .then()
                .log().ifValidationFails()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .and()
                .time(lessThan(1L), TimeUnit.SECONDS)
                .and()
                .assertThat()
                .body("users.size()", greaterThan(0))
        ;
    }

    @Test
    public void f_usersByNameTest()
    {
        RestAssured
                .given().port(randomServerPort)
                .auth().preemptive().basic(login.getUsername(),password)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("nome","Daniele")
                .log().all()
                .get("/users" )
                .then()
                .log().ifValidationFails()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .and()
                .time(lessThan(1L), TimeUnit.SECONDS)
                .and()
                .assertThat()
                .body("users.size()", greaterThan(0))
        ;
    }

    @Test
    public void g_usersByFullNameTest()
    {
        RestAssured
                .given().port(randomServerPort)
                .auth().preemptive().basic(login.getUsername(),password)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("nome","Daniele")
                .queryParam("cognome","Betti")
                .log().all()
                .get("/users" )
                .then()
                .log().ifValidationFails()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())
                .and()
                .time(lessThan(1L), TimeUnit.SECONDS)
                .and()
                .assertThat()
                .body("users.size()", greaterThan(0))
        ;
    }

    @After
    public void h_deleteUser()
    {
        RestAssured
                .given().port(randomServerPort)
                .auth().preemptive().basic(login.getUsername(),password)
                .pathParam("id", user.getId())
                .body(new User(0L, "Daniele", "Betti", "passWord", "daniele@mailexample.com"))
                .log().all()
                .delete("/users/{id}")
                .then()
                .log().ifValidationFails()
                .log().ifError()
                .statusCode(HttpStatus.OK.value())

        ;
    }

}

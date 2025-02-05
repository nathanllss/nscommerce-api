package com.nathanlucas.nscommerce.utils;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;

@Component
public class TokenUtilRA {

    public static String obtainAccessToken(String username, String password) {
//        return authRequest(username, password)
//                .then().extract().path("access_token");
        Response response = authRequest(username, password);
        JsonPath jsonBody = response.jsonPath();
        return jsonBody.getString("access_token");
    }


    private static Response authRequest(String username, String password) {
        return given()
                .auth()
                .preemptive()
                .basic("myclientid", "myclientsecret")
            .contentType(ContentType.URLENC)
                .formParam("grant_type", "password")
                .formParam("username", username)
                .formParam("password", password)
            .when()
                .post("/oauth2/token");
    }

}

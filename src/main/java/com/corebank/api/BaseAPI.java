package com.corebank.api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class BaseAPI {

    // Khởi tạo thông tin cơ bản cho mọi request
    public static RequestSpecification getBaseRequest() {
        return new RequestSpecBuilder()
                .setBaseUri("https://parabank.parasoft.com/parabank/services/bank")
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON) // Yêu cầu server trả về dạng JSON
                .build();
    }

    // Khởi tạo các kiểm tra mặc định cho mọi response
    public static ResponseSpecification getBaseResponse() {
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .build();
    }
}
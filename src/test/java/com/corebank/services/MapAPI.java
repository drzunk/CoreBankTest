package com.corebank.services;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class MapAPI {
    private static final String MAP_BASE_URL = "https://nominatim.openstreetmap.org";

    // Hàm gọi Bản đồ để dịch tọa độ
    public static Response reverseGeocode(String lat, String lng) {
        return RestAssured
                .given()
                .baseUri(MAP_BASE_URL)
                .header("User-Agent", "CoreBankAutoTest/1.0")
                .queryParam("format", "json")
                .queryParam("lat", lat)
                .queryParam("lon", lng)
                .when()
                .get("/reverse");
    }
}
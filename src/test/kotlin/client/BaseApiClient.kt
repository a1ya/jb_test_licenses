package client

import config.TestConfig
import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import io.restassured.RestAssured.given

abstract class BaseApiClient(
    private val apiKey: String = TestConfig.apiKey,
    private val customerCode: String = TestConfig.customerCode
) {

    protected val requestSpec: RequestSpecification =
        RequestSpecBuilder()
            .setBaseUri(TestConfig.baseUrl)
            .addHeader("X-Api-Key", apiKey)
            .addHeader("X-Customer-Code", customerCode)
            .setContentType("application/json")
            .build()

    protected fun baseRequest(): RequestSpecification =
        given().spec(requestSpec)
}
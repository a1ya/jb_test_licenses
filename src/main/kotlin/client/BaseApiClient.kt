package client

import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import io.restassured.RestAssured.given
import config.TestConfig

abstract class BaseApiClient(
    apiKey: String = TestConfig.apiKey,
    customerCode: String = TestConfig.customerCode
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
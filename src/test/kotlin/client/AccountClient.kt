package client

import config.TestConfig
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.Response

class AccountClient(
    private val baseUrl: String,
    private val apiKey: String,
    private val customerCode: String
) {

    fun assignLicense(body: Any): Response =
        given()
            .baseUri(baseUrl)
            .header("X-Api-Key", apiKey)
            .header("X-Customer-Code", customerCode)
            .contentType(ContentType.JSON)
            .body(body)
            .`when`()
            .post("/customer/licenses/assign")
            .then()
            .extract()
            .response()

    fun changeLicenseTeam(body: Any): Response =
        given()
            .baseUri(baseUrl)
            .header("X-Api-Key", apiKey)
            .header("X-Customer-Code", customerCode)
            .contentType(ContentType.JSON)
            .body(body)
            .`when`()
            .post("/customer/changeLicensesTeam")
            .then()
            .extract()
            .response()

    fun getCustomerLicenses(
        productCode: String? = null,
        assigned: Boolean? = null,
        assigneeEmail: String? = null,
        page: Int = 1,
        perPage: Int = 100
    ): Response {

        val request = given()
            .baseUri(TestConfig.baseUrl)
            .header("X-Api-Key", TestConfig.apiKey)
            .header("X-Customer-Code", TestConfig.customerCode)
            .queryParam("page", page)
            .queryParam("perPage", perPage)

        productCode?.let { request.queryParam("productCode", it) }
        assigned?.let { request.queryParam("assigned", it) }
        assigneeEmail?.let { request.queryParam("assigneeEmail", it) }

        return request
            .`when`()
            .get("/customer/licenses")
            .then()
            .extract()
            .response()
    }

}



package client

import io.restassured.response.Response
import config.TestConfig
import model.AssignLicenseRequest
import model.ChangeTeamRequest
import model.LicenseResponse

class AccountClient(val apiKey: String = TestConfig.apiKey, customerCode: String = TestConfig.customerCode) : BaseApiClient(apiKey, customerCode) {

     fun assignLicense(requestBody: AssignLicenseRequest): Response =
         baseRequest()
             .body(requestBody)
             .post("/customer/licenses/assign")
             .then()
             .extract()
             .response()

    fun changeLicenseTeam(requestBody: ChangeTeamRequest): Response =
        baseRequest()
            .body(requestBody)
            .post("/customer/changeLicensesTeam")
            .then()
            .extract()
            .response()

    fun getCustomerLicenses(): Response =
        baseRequest()
            .get("/customer/licenses")
            .then()
            .extract()
            .response()

    fun getCustomerLicensesForTeam(teamId: Int): Response =
        baseRequest()
            .get("/customer/teams/$teamId/licenses")
            .then()
            .extract()
            .response()

    fun getCustomerLicense(licenseId: String): LicenseResponse =
        baseRequest()
            .get("/customer/licenses/$licenseId")
            .then()
            .statusCode(200)
            .extract()
            .`as`(LicenseResponse::class.java)

}



package tests

import client.AccountClient
import config.TestConfig
import data.TestData
import io.restassured.response.Response
import model.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LicenseAssignTests {

    private lateinit var client: AccountClient

    @BeforeAll
    fun setup() {
        client = AccountClient()
    }

    @Test
    fun `check available license can be assigned by license with product`() {
        val availableLicense = findAvailableLicense("CL")

        val request = AssignLicenseRequest(license =
            AssignFromTeamRequest(availableLicense.product.code, availableLicense.team.id))
        val response = client.assignLicense(request)

        checkLicenseIsAssigned(response, availableLicense.licenseId)
    }

    @Test
    fun `check available license can be assigned by licenseId`() {
        val availableLicense = findAvailableLicense()

        val request = AssignLicenseRequest(licenseId = availableLicense.licenseId)
        val response = client.assignLicense(request)

        checkLicenseIsAssigned(response, availableLicense.licenseId)
    }

    @Test
    fun `check available license can be assigned by team admin`() {
        val teamAdminClient = AccountClient(apiKey = TestConfig.apiKeyTeamBAdmin)
        val availableLicense = findAvailableLicenseInTeam(TestData.testTeamBId)

        val request = AssignLicenseRequest(licenseId = availableLicense.licenseId)
        val response = teamAdminClient.assignLicense(request)

        checkLicenseIsAssigned(response, availableLicense.licenseId)
    }

    @Test
    fun `check license can not be assigned by team admin from another team`() {
        val teamBAdminClient = AccountClient(apiKey = TestConfig.apiKeyTeamBAdmin)
        val availableLicense = findAvailableLicenseInTeam(TestData.testTeamAId)

        val request = AssignLicenseRequest(licenseId = availableLicense.licenseId)
        val response = teamBAdminClient.assignLicense(request)

        assertEquals(403, response.statusCode)
        val error = response.`as`(ErrorResponse::class.java)
        assertEquals(TEAM_MISMATCH, error.code)
    }

    @Test
    fun `check license can not be assigned by organization viewer`() {
        val orgViewerClient = AccountClient(apiKey = TestConfig.apiKeyOrgViewer)

        val availableLicense = findAvailableLicense()
        val request = AssignLicenseRequest(licenseId = availableLicense.licenseId)
        val response = orgViewerClient.assignLicense(request)

        assertEquals(403, response.statusCode)
        val error = response.`as`(ErrorResponse::class.java)
        assertEquals(INSUFFICIENT_PERMISSIONS, error.code)
    }

    @Test
    fun `check license can not be assigned by team viewer`() {
        val teamViewerClient = AccountClient(apiKey = TestConfig.apiKeyTeamBViewer)

        val availableLicense = findAvailableLicense()
        val request = AssignLicenseRequest(licenseId = availableLicense.licenseId)
        val response = teamViewerClient.assignLicense(request)

        assertEquals(403, response.statusCode)
        val error = response.`as`(ErrorResponse::class.java)
        assertEquals(INSUFFICIENT_PERMISSIONS, error.code)
    }

    @Test
    fun `check license can not be assigned twice`() {
        val availableLicense = findAvailableLicense()

        val request = AssignLicenseRequest(licenseId = availableLicense.licenseId)
        val response = client.assignLicense(request)
        checkLicenseIsAssigned(response, availableLicense.licenseId)

        val repeatedResponse = client.assignLicense(request)
        assertEquals(400, repeatedResponse.statusCode)
        val error = repeatedResponse.`as`(ErrorResponse::class.java)
        assertEquals(LICENSE_IS_NOT_AVAILABLE_TO_ASSIGN, error.code)
    }

    @Test
    fun `check license can not be assigned for disposable email`() {
        val availableLicense = findAvailableLicense()

        val request = AssignLicenseRequest(contact = TestData.testDisposableEmailContact, licenseId = availableLicense.licenseId)
        val response = client.assignLicense(request)

        assertEquals(400, response.statusCode)
        val error = response.`as`(ErrorResponse::class.java)
        assertEquals(INVALID_CONTACT_EMAIL, error.code)
    }

    @Test
    fun `check license can not be assigned for invalid license`() {
        val invalidLicenseId = "INVALID_LICENSE_ID"

        val request = AssignLicenseRequest(licenseId = invalidLicenseId)
        val response = client.assignLicense(request)

        assertEquals(404, response.statusCode)
        val error = response.`as`(ErrorResponse::class.java)
        assertEquals(LICENSE_NOT_FOUND, error.code)
    }

    @Test
    fun `check license can not be assigned for invalid team`() {
        val availableLicense = findAvailableLicense()
        val invalidTeamId = 0

        val request = AssignLicenseRequest(license =
            AssignFromTeamRequest(availableLicense.product.code, invalidTeamId))
        val response = client.assignLicense(request)

        assertEquals(404, response.statusCode)
        val error = response.`as`(ErrorResponse::class.java)
        assertEquals(TEAM_NOT_FOUND, error.code)
    }

    @Test
    fun `check license can not be assigned for invalid product code`() {
        val availableLicense = findAvailableLicense()
        val invalidProductCode = "FAKE"

        val request = AssignLicenseRequest(license = AssignFromTeamRequest(invalidProductCode, availableLicense.team.id))
        val response = client.assignLicense(request)

        assertEquals(404, response.statusCode)
        val error = response.`as`(ErrorResponse::class.java)
        assertEquals(PRODUCT_NOT_FOUND, error.code)
    }


    fun findAvailableLicense(productCode: String? = null): LicenseResponse {
        val licenses = client.getCustomerLicenses(productCode = productCode).`as`(Array<LicenseResponse>::class.java)
        return licenses.firstOrNull { it.isAvailableToAssign }
            ?: error("No available licenses found")
    }

    fun findAvailableLicenseInTeam(teamId: Int): LicenseResponse {
        val licenses = client.getCustomerLicensesForTeam(teamId).`as`(Array<LicenseResponse>::class.java)
        return licenses.firstOrNull { it.isAvailableToAssign }
            ?: error("No available licenses found for team with teamId=$teamId")
    }

    fun checkLicenseIsAssigned(response: Response, licenseId: String) {
        assertEquals(200, response.statusCode)

        val assignedLicense = client.getCustomerLicense(licenseId)
        assertNotNull(assignedLicense.assignee)
        assertEquals(assignedLicense.assignee?.email, TestData.testAssigneeContact.email) //note: only 1 test user
        assertFalse(assignedLicense.isAvailableToAssign)
    }


}

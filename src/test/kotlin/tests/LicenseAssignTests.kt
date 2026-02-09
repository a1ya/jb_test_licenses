package tests

import config.TestConfig
import config.TestData
import client.AccountClient
import model.ApiErrorCode.INSUFFICIENT_PERMISSIONS
import model.ApiErrorCode.INVALID_CONTACT_EMAIL
import model.ApiErrorCode.LICENSE_IS_NOT_AVAILABLE_TO_ASSIGN
import model.ApiErrorCode.LICENSE_NOT_FOUND
import model.ApiErrorCode.PRODUCT_NOT_FOUND
import model.ApiErrorCode.TEAM_MISMATCH
import model.ApiErrorCode.TEAM_NOT_FOUND
import model.AssignFromTeamRequest
import model.AssignLicenseRequest
import model.ErrorResponse
import model.LicenseResponse
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LicenseAssignTests {

    private lateinit var orgAdminClient: AccountClient

    @BeforeAll
    fun setup() {
        orgAdminClient = AccountClient()
    }

    @Test
    fun `check org admin can assign available license by license with product code and team id`() {
        val availableLicense = findAvailableLicense()

        val request = AssignLicenseRequest(
            license = AssignFromTeamRequest(availableLicense.product.code, availableLicense.team.id))

        checkLicenseIsAssignedSuccessfully(orgAdminClient, request, availableLicense.licenseId)
    }

    @Test
    fun `check org admin can assign available license by licenseId`() {
        val availableLicense = findAvailableLicense()

        val request = AssignLicenseRequest(licenseId = availableLicense.licenseId)

        checkLicenseIsAssignedSuccessfully(orgAdminClient, request, availableLicense.licenseId)
    }

    @Test
    fun `check team admin can assign available license`() {
        val teamAdminClient = AccountClient(apiKey = TestConfig.apiKeyTeamBAdmin)
        val availableLicense = findAvailableLicenseInTeam(TestData.TEAM_B_ID)

        val request = AssignLicenseRequest(licenseId = availableLicense.licenseId)

        checkLicenseIsAssignedSuccessfully(teamAdminClient, request, availableLicense.licenseId)
    }

    @Test
    fun `check license can not be assigned by team admin from another team`() {
        val teamBAdminClient = AccountClient(apiKey = TestConfig.apiKeyTeamBAdmin)
        val availableLicense = findAvailableLicenseInTeam(TestData.TEAM_A_ID)

        val request = AssignLicenseRequest(licenseId = availableLicense.licenseId)

        checkAssignLicenseRequestFailsWithError(teamBAdminClient, request,
            403, TEAM_MISMATCH)
        checkLicenseNotAssigned(availableLicense.licenseId)
    }

    @Test
    fun `check license can not be assigned by organization viewer`() {
        val orgViewerClient = AccountClient(apiKey = TestConfig.apiKeyOrgViewer)

        val availableLicense = findAvailableLicense()
        val request = AssignLicenseRequest(licenseId = availableLicense.licenseId)

        checkAssignLicenseRequestFailsWithError(orgViewerClient, request,
            403, INSUFFICIENT_PERMISSIONS)
        checkLicenseNotAssigned(availableLicense.licenseId)
    }

    @Test
    fun `check license can not be assigned by team viewer`() {
        val teamViewerClient = AccountClient(apiKey = TestConfig.apiKeyTeamBViewer)

        val availableLicense = findAvailableLicenseInTeam(TestData.TEAM_B_ID)
        val request = AssignLicenseRequest(licenseId = availableLicense.licenseId)

        checkAssignLicenseRequestFailsWithError(teamViewerClient, request,
            403, INSUFFICIENT_PERMISSIONS)
        checkLicenseNotAssigned(availableLicense.licenseId)
    }

    @Test
    fun `check license can not be assigned twice`() {
        val availableLicense = findAvailableLicense()

        val request = AssignLicenseRequest(licenseId = availableLicense.licenseId)

        checkLicenseIsAssignedSuccessfully(orgAdminClient, request, availableLicense.licenseId) // first time
        checkAssignLicenseRequestFailsWithError(orgAdminClient, request,
            400, LICENSE_IS_NOT_AVAILABLE_TO_ASSIGN) // second time
    }

    @Test
    fun `check license can not be assigned for disposable email assignee contact`() {
        val availableLicense = findAvailableLicense()

        val request = AssignLicenseRequest(
            contact = TestData.testDisposableEmailAssigneeContact, licenseId = availableLicense.licenseId)

        checkAssignLicenseRequestFailsWithError(orgAdminClient, request,
            400, INVALID_CONTACT_EMAIL)
        checkLicenseNotAssigned(availableLicense.licenseId)
    }

    @Test
    fun `check license can not be assigned for invalid license`() {
        val invalidLicenseId = TestData.INVALID_LICENSE_ID

        val request = AssignLicenseRequest(licenseId = invalidLicenseId)

        checkAssignLicenseRequestFailsWithError(orgAdminClient, request,
            404, LICENSE_NOT_FOUND)
    }

    @Test
    fun `check license can not be assigned for invalid team`() {
        val availableLicense = findAvailableLicense()
        val invalidTeamId = 0

        val request = AssignLicenseRequest(
            license = AssignFromTeamRequest(availableLicense.product.code, invalidTeamId))

        checkAssignLicenseRequestFailsWithError(orgAdminClient, request,
            404, TEAM_NOT_FOUND)
        checkLicenseNotAssigned(availableLicense.licenseId)
    }

    @Test
    fun `check license can not be assigned for invalid product code`() {
        val availableLicense = findAvailableLicense()
        val invalidProductCode = TestData.INVALID_PRODUCT_CODE

        val request = AssignLicenseRequest(license = AssignFromTeamRequest(invalidProductCode, availableLicense.team.id))

        checkAssignLicenseRequestFailsWithError(orgAdminClient, request,
            404, PRODUCT_NOT_FOUND)
        checkLicenseNotAssigned(availableLicense.licenseId)
    }

    private fun findAvailableLicense(): LicenseResponse {
        val licenses = orgAdminClient.getCustomerLicenses().`as`(Array<LicenseResponse>::class.java)
        return licenses.firstOrNull { it.isAvailableToAssign }
            ?: error("No available licenses found")
    }

    private fun findAvailableLicenseInTeam(teamId: Int): LicenseResponse {
        val licenses = orgAdminClient.getCustomerLicensesForTeam(teamId).`as`(Array<LicenseResponse>::class.java)
        return licenses.firstOrNull { it.isAvailableToAssign }
            ?: error("No available licenses found for team with teamId=$teamId")
    }

    private fun checkLicenseIsAssignedSuccessfully(assignClient: AccountClient, request: AssignLicenseRequest, licenseId: String) {
        val response = assignClient.assignLicense(request)
        assertEquals(200, response.statusCode)

        val license = orgAdminClient.getCustomerLicense(licenseId)
        assertNotNull(license.assignee, "Assignee expected to be notNull for licenseId =$licenseId")

        val licenseEmail = license.assignee?.email
        val expectedEmail = TestData.testAssignContact.email // since there is only 1 test user that can be successfully assigned
        assertEquals(expectedEmail, licenseEmail,
            "Assignee email for licenseId=$licenseId expected to be $expectedEmail, but was $licenseEmail")

        assertFalse(license.isAvailableToAssign, "isAvailableToAssign expected to be false after assigning license")
    }

    private fun checkAssignLicenseRequestFailsWithError(client: AccountClient, request: AssignLicenseRequest,
                                                expectedStatusCode: Int, expectedErrorCode: String) {

        val response = client.assignLicense(request)
        assertEquals(expectedStatusCode, response.statusCode,
            "Assign license request=$request expected to fail with status code $expectedStatusCode, but was ${response.statusCode}")

        val error = response.`as`(ErrorResponse::class.java)
        assertEquals(expectedErrorCode, error.code,
            "Assign license request=$request expected to fail with error code $expectedErrorCode, but was ${error.code}")
    }

    private fun checkLicenseNotAssigned(licenseId: String) {
        val license = orgAdminClient.getCustomerLicense(licenseId)
        assertNull(license.assignee, "Assignee expected to be null for not assigned license")
        assertTrue(license.isAvailableToAssign, "isAvailableToAssign expected to be true for not assigned license")
    }

    //todo would be nice to have some test revoke method to clean up (since existing public one works only for licenses assigned more than 30 days ago)

}

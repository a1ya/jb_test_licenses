package tests

import client.AccountClient
import config.TestConfig
import config.TestData
import model.ApiErrorCode.INSUFFICIENT_PERMISSIONS
import model.ApiErrorCode.TEAM_NOT_FOUND
import model.ApiErrorCode.TOKEN_TYPE_MISMATCH
import model.ChangeTeamRequest
import model.ErrorResponse
import model.LicenseResponse
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LicenseChangeTeamTests {

    private lateinit var orgAdminClient: AccountClient
    private val sourceTeamId = TestData.TEAM_B_ID //team with licenses
    private val targetTeamId = TestData.TEAM_C_ID // team without licenses

    @BeforeAll
    fun setup() {
        orgAdminClient = AccountClient()
    }

    @Test
    fun `check org admin can change license team to another`() {
        val licenseId = findAvailableLicenseInTeam(sourceTeamId)

        val request = ChangeTeamRequest(listOf(licenseId), targetTeamId)

        checkChangeLicenseTeamRequestSuccessful(request)
        checkLicenseTeam(licenseId, targetTeamId)
    }

    @Test
    fun `check org admin can transfer several licenses from one team to another`() {
        val (licenseId1, licenseId2) = findAvailableLicenseIdsInTeam(sourceTeamId, count = 2)

        val request = ChangeTeamRequest(listOf(licenseId1, licenseId2), targetTeamId)

        checkChangeLicenseTeamRequestSuccessful(request)
        checkLicenseTeam(licenseId1, targetTeamId)
        checkLicenseTeam(licenseId2, targetTeamId)
    }

    @Test
    fun `check org admin can transfer license from one team to another if one of licenses in request is already in target team`() {
        val licenseId1 = findAvailableLicenseInTeam(sourceTeamId)
        val preparationRequest = ChangeTeamRequest(listOf(licenseId1), targetTeamId)
        checkChangeLicenseTeamRequestSuccessful(preparationRequest)
        checkLicenseTeam(licenseId1, targetTeamId)

        val licenseId2 = findAvailableLicenseInTeam(sourceTeamId)

        val request = ChangeTeamRequest(listOf(licenseId1, licenseId2), targetTeamId)

        checkChangeLicenseTeamRequestSuccessful(request)
        checkLicenseTeam(licenseId1, targetTeamId)
        checkLicenseTeam(licenseId2, targetTeamId)
    }

    @Test
    fun `check org admin can transfer license from one team to another if one of licenseIds in request is invalid`() {
        val licenseId = findAvailableLicenseInTeam(sourceTeamId)
        val invalidLicenseId = TestData.INVALID_LICENSE_ID

        val request = ChangeTeamRequest(listOf(licenseId, invalidLicenseId), targetTeamId)

        checkChangeLicenseTeamRequestSuccessful(request)
        checkLicenseTeam(licenseId, targetTeamId)
    }

    @Test
    fun `check change licenses team should not fail if licenseIds are empty`() { // todo discuss if 200 is ok
        val request = ChangeTeamRequest(listOf(), targetTeamId)

        checkChangeLicenseTeamRequestSuccessful(request)
    }

    @Test
    fun `check change licenses team should not fail if licenseIds has only invalid license`() {  // todo discuss if 200 is ok
        val request = ChangeTeamRequest(listOf(TestData.INVALID_LICENSE_ID), targetTeamId)

        checkChangeLicenseTeamRequestSuccessful(request)
    }

    @Test
    fun `check license can not be transferred by organization viewer`() {
        val orgViewerClient = AccountClient(apiKey = TestConfig.apiKeyOrgViewer)
        val licenseId = findAvailableLicenseInTeam(sourceTeamId)

        val request = ChangeTeamRequest(listOf(licenseId), targetTeamId)

        checkChangeLicenseTeamRequestFailsWithError(orgViewerClient, request,
            403, INSUFFICIENT_PERMISSIONS)
        checkLicenseTeam(licenseId, sourceTeamId)
    }

    @Test
    fun `check license can not be transferred by only source team admin`() {
        val teamViewerClient = AccountClient(apiKey = TestConfig.apiKeyTeamBViewer)
        val licenseId = findAvailableLicenseInTeam(sourceTeamId)

        val request = ChangeTeamRequest(listOf(licenseId), targetTeamId)

        checkChangeLicenseTeamRequestFailsWithError(teamViewerClient, request,
            403, TOKEN_TYPE_MISMATCH)
        checkLicenseTeam(licenseId, sourceTeamId)
    }

    @Test
    fun `check license can not be transferred by two teams admin`() { // todo check expected behavior, since possible via UI
        val twoTeamsAdminClient = AccountClient(apiKey = TestConfig.apiKeyTeamABAdmin)
        val licenseId = findAvailableLicenseInTeam(sourceTeamId)

        val request = ChangeTeamRequest(listOf(licenseId), targetTeamId)
        checkChangeLicenseTeamRequestFailsWithError(twoTeamsAdminClient, request,
            403, TOKEN_TYPE_MISMATCH)
        checkLicenseTeam(licenseId, sourceTeamId)
    }

    @Test
    fun `check license can not be transferred by source team viewer`() {
        val teamViewerClient = AccountClient(apiKey = TestConfig.apiKeyTeamBViewer)
        val licenseId = findAvailableLicenseInTeam(sourceTeamId)

        val request = ChangeTeamRequest(listOf(licenseId), targetTeamId)
        checkChangeLicenseTeamRequestFailsWithError(teamViewerClient, request,
            403, TOKEN_TYPE_MISMATCH)
        checkLicenseTeam(licenseId, sourceTeamId)
    }

    @Test
    fun `check change licenses team fails if targetTeamId is invalid`() {
        val licenseId = findAvailableLicenseInTeam(sourceTeamId)

        val request = ChangeTeamRequest(listOf(licenseId), 0)

        checkChangeLicenseTeamRequestFailsWithError(orgAdminClient, request,
            404, TEAM_NOT_FOUND)
        checkLicenseTeam(licenseId, sourceTeamId)
    }

    @Test
    fun `check change licenses team fails if targetTeamId is deleted`() {
        val licenseId = findAvailableLicenseInTeam(sourceTeamId)

        val request = ChangeTeamRequest(listOf(licenseId), TestData.TEAM_DELETED_ID)

        checkChangeLicenseTeamRequestFailsWithError(orgAdminClient, request,
            404, TEAM_NOT_FOUND)
    }

    @Disabled("would be nice to also check that license with isTransferableBetweenTeams = false, " +
            "but no such existing licenses found")
    @Test
    fun `check change team fails for license with isTransferableBetweenTeams = false`() {}
    //

    private fun findAvailableLicenseInTeam(teamId: Int): String {
        return findAvailableLicenseIdsInTeam(teamId).first()
    }

    private fun findAvailableLicenseIdsInTeam(teamId: Int, count: Int = 1): List<String> {
        val licenses = orgAdminClient.getCustomerLicensesForTeam(teamId)
            .`as`(Array<LicenseResponse>::class.java)
            .filter { it.isTransferableBetweenTeams }

        if (licenses.size < count) {
            throw AssertionError(
                "Expected $count transferable licenses for teamId=$teamId, but found ${licenses.size}")
        }

        return licenses.take(count).map { it.licenseId }
    }

    private fun checkLicenseTeam(licenseId: String, expectedTeamId: Int) {
        val license = orgAdminClient.getCustomerLicense(licenseId)
        assertEquals(license.team.id, expectedTeamId,
            "TeamId for licenseId=$licenseId expected to be $expectedTeamId, but was ${license.team.id}")
    }

    private fun checkChangeLicenseTeamRequestSuccessful(request: ChangeTeamRequest) {
        val response = orgAdminClient.changeLicenseTeam(request)
        assertEquals(200, response.statusCode,
            "Change licenses team request=$request expected to be successful")
    }

    private fun checkChangeLicenseTeamRequestFailsWithError(changeTeamClient: AccountClient, request: ChangeTeamRequest,
                                                    expectedStatusCode: Int, expectedErrorCode: String) {

        val response = changeTeamClient.changeLicenseTeam(request)
        assertEquals(expectedStatusCode, response.statusCode,
            "Change licenses team request=$request expected to fail with status code $expectedStatusCode, but was ${response.statusCode}")

        val error = response.`as`(ErrorResponse::class.java)
        assertEquals(expectedErrorCode, error.code,
            "Change licenses team request=$request expected to fail with error code $expectedErrorCode, but was ${error.code}")
    }

    @AfterEach
    fun cleanupMoveTargetTeamLicensesBackToSourceTeam() {
        val targetLicenses = orgAdminClient.getCustomerLicensesForTeam(targetTeamId).`as`(Array<LicenseResponse>::class.java)

        if (targetLicenses.isNotEmpty()) {
            val licenseIds = targetLicenses.map { it.licenseId }
            val request = ChangeTeamRequest(licenseIds = licenseIds, targetTeamId = sourceTeamId)
            val response = orgAdminClient.changeLicenseTeam(request)
            assertEquals(200, response.statusCode)
        }

        val remaining = orgAdminClient.getCustomerLicensesForTeam(targetTeamId).`as`(Array<LicenseResponse>::class.java)

        assertTrue(remaining.isEmpty(),
            "Target team with $targetTeamId still contains ${remaining.size} licenses after cleanup")
    }

}
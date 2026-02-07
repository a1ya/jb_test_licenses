package tests

import client.AccountClient
import config.TestConfig
import model.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LicenseManagementTests {

    private lateinit var client: AccountClient

    private val baseUrl = TestConfig.baseUrl
    private val apiKey = TestConfig.apiKey
    private val customerCode = TestConfig.customerCode

    @BeforeAll
    fun setup() {
        client = AccountClient(baseUrl, apiKey, customerCode)
    }

    @Test
    fun `should return list of licenses`() {

        val response = client.getCustomerLicenses()

        assertEquals(200, response.statusCode)

        val licenses = response.jsonPath().getList(".", LicenseResponse::class.java)

        assertNotNull(licenses)
        assertTrue(licenses.isNotEmpty(), "License list should not be empty")
    }

}

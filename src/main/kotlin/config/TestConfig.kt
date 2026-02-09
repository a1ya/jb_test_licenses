package config

import model.AssignContactRequest

object TestConfig {

    val apiKey: String = System.getenv("JB_API_KEY")
        ?: error("JB_API_KEY not set")

    val apiKeyOrgViewer: String = System.getenv("JB_API_KEY_ORG_VIEWER")
        ?: error("JB_API_KEY_ORG_VIEWER not set")

    val apiKeyTeamBAdmin: String = System.getenv("JB_API_KEY_TEAM_B_ADMIN")
        ?: error("JB_API_KEY_TEAM_B_ADMIN not set")

    val apiKeyTeamBViewer: String = System.getenv("JB_API_KEY_TEAM_B_VIEWER")
        ?: error("JB_API_KEY_TEAM_B_VIEWER not set")

    val apiKeyTeamABAdmin: String = System.getenv("JB_API_KEY_TEAM_AB_ADMIN")
        ?: error("JB_API_KEY_TEAM_AB_ADMIN not set")

    val customerCode: String = System.getenv("JB_CUSTOMER_CODE")
        ?: error("JB_CUSTOMER_CODE not set")

    const val baseUrl = "https://account.jetbrains.com/api/v1"

}

object TestData {

    val testAssignContact = AssignContactRequest(
        email = "alevtina.gatilova@gmail.com",
        firstName = "test",
        lastName = "test"
    )

    val testDisposableEmailAssigneeContact = AssignContactRequest(
        email = "carvelc51@sunsabla.com",
        firstName = "test",
        lastName = "test"
    )

    const val INVALID_LICENSE_ID = "INVALID_LICENSE_ID"
    const val INVALID_PRODUCT_CODE = "FAKE"
    const val TEAM_A_ID = 2795957
    const val TEAM_B_ID = 2794989
    const val TEAM_C_ID = 2796686
    const val TEAM_DELETED_ID = 2796685
}

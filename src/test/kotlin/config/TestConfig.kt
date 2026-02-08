package config

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
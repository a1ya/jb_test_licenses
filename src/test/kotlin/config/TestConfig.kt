package config

object TestConfig {

    val apiKey: String = System.getenv("JB_API_KEY")
        ?: error("JB_API_KEY not set")

    val customerCode: String = System.getenv("JB_CUSTOMER_CODE")
        ?: error("JB_CUSTOMER_CODE not set")

    const val baseUrl = "https://account.jetbrains.com/api/v1"
}
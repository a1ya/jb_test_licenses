package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class AssigneeContactRequest(
    val email: String,
    val firstName: String,
    val lastName: String
)

data class AssignFromTeamRequest(
    val productCode: String,
    val team: Int
)

data class AssignLicenseRequest(
    val contact: AssigneeContactRequest,
    val includeOfflineActivationCode: Boolean,
    val license: AssignFromTeamRequest? = null,
    val licenseId: String? = null,
    val sendEmail: Boolean
)

data class ChangeTeamRequest(
    val licenseIds: List<String>,
    val targetTeamId: Int
)

data class ErrorResponse(
    val code: String?,
    val description: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LicenseResponse(
    val licenseId: String,
    val isAvailableToAssign: Boolean?,
    val isSuspended: Boolean?,
    val isTransferableBetweenTeams: Boolean?,
    val isTrial: Boolean?,
    val product: ProductResponse?,
    val subscription: SubscriptionResponse?,
    val team: TeamResponse,
    )

data class ProductResponse(
    val code: String?,
    val name: String?
)

data class SubscriptionResponse(
    val isAutomaticallyRenewed: Boolean,
    val isOutdated: Boolean,
    val subscriptionPackRef: String?,
    val validUntilDate: String?,
)

data class TeamResponse(
    val id: Int,
    val name: String?
)


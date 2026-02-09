package model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import config.TestData

data class AssignContactRequest(
    val email: String,
    val firstName: String,
    val lastName: String
)

data class AssignFromTeamRequest(
    val productCode: String?,
    val team: Int
)

data class AssignLicenseRequest(
    val contact: AssignContactRequest = TestData.testAssignContact,
    val includeOfflineActivationCode: Boolean = true,
    val license: AssignFromTeamRequest? = null,
    val licenseId: String? = null,
    val sendEmail: Boolean = false
)

data class ChangeTeamRequest(
    val licenseIds: List<String>,
    val targetTeamId: Int
)

data class ErrorResponse(
    val code: String,
    val description: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LicenseResponse(
    val assignee: AssigneeLicenseResponse?,
    val licenseId: String,
    val isAvailableToAssign: Boolean,
    val isTransferableBetweenTeams: Boolean,
    val product: ProductResponse,
    val team: TeamResponse,
    )

data class ProductResponse(
    val code: String,
    val name: String
)

data class TeamResponse(
    val id: Int,
    val name: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AssigneeLicenseResponse(
    val email: String?,
    val name: String?
)


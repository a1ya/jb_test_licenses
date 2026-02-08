package data

import model.AssignContactRequest

object TestData {

    // Test user
    val testAssignContact = AssignContactRequest(
        email = "alevtina.gatilova@gmail.com",
        firstName = "test",
        lastName = "test"
    )

    // Test user
    val testDisposableEmailAssigneeContact = AssignContactRequest(
        email = "carvelc51@sunsabla.com",
        firstName = "test",
        lastName = "test"
    )

    // reusable data:
    const val invalidLicenseId = "INVALID_LICENSE_ID"
    const val teamAId = 2795957
    const val teamBId = 2794989
    const val teamDeletedId = 2796685
}
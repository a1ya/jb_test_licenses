package data

import model.AssigneeContactRequest

object TestData {

    // Test user
    val testAssigneeContact = AssigneeContactRequest(
        email = "alevtina.gatilova@gmail.com",
        firstName = "test",
        lastName = "test"
    )

    // Test user
    val testDisposableEmailAssigneeContact = AssigneeContactRequest(
        email = "carvelc51@sunsabla.com",
        firstName = "Alya TeamBviewer",
        lastName = "Test"
    )

    // reusable data:
    const val invalidLicenseId = "INVALID_LICENSE_ID"
    const val teamAId = 2795957
    const val teamBId = 2794989
    const val teamDeletedId = 2796685
}
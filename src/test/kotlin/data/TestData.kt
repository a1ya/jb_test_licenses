package data

import model.AssigneeContactRequest

object TestData {

    // Test user
    val testAssigneeContact = AssigneeContactRequest(
        email = "alevtina.gatilova@gmail.com",
        firstName = "Alevtina",
        lastName = "Shalygina"
    )

    // Test user
    val testDisposableEmailContact = AssigneeContactRequest(
        email = "carvelc51@sunsabla.com",
        firstName = "Alya TeamBviewer",
        lastName = "Test"
    )


    // reusable data:
    const val testLicenseId = "07WB7RP9UB"
    const val recentlyAssignedLicenseId = "RECENT1234"
    const val invalidLicenseId = "INVALID1234"
    const val testProductCode = "II" // IntelliJ IDEA Ultimate, for example
    const val testTeamAId = 2795957
    const val testTeamBId = 2794989
}
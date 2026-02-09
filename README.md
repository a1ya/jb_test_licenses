License Management API Tests

The project verifies license assignment, transfer between teams, and negative scenarios such as permission restrictions and invalid data handling.

Automated API tests for License Management functionality using:
- Kotlin (JVM 17)
- JUnit 5
- REST Assured
- Jackson
- Allure Reporting
- Gradle

Required Environment Variables (provided separately)
- JB_API_KEY
- JB_API_KEY_ORG_VIEWER
- JB_API_KEY_TEAM_AB_ADMIN
- JB_API_KEY_TEAM_B_ADMIN
- JB_API_KEY_TEAM_B_VIEWER
- JB_CUSTOMER_CODE
If they are not set, tests will fail.

How to Run Tests
- Run all tests ./gradlew test

Allure Report
- Generate report ./gradlew allureReport
- Generate and open report in browser ./gradlew allureServe
- Run everything in one command ./gradlew test allureReport allureServe

Test Cleanup

- LicenseChangeTeamTests. Source and Target teams are used. Source team has licenses, target team has no licenses.
  Cleanup logic ensures that licenses are returned to the Source team. Target team does not retain transferred licenses after test execution.

- LicenseAssignTests. Unfortunately no cleanup logic in tests (public api method can not be used to revoke licenses assigned less then 30 days ago). 
  For cleanup licenses assigned to test user should be revoked manually via UI.

Notes

- Tests depend on real test environment data.




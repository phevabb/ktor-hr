package com.hr.managerprofile.routes

import com.hr.config.DatabaseFactory.dbQuery
import com.hr.managerprofile.dtos.ManagerProfileOperationResult
import com.hr.managerprofile.dtos.ManagerProfileRequest
import com.hr.managerprofile.repositories.ManagerProfileRepository
import com.hr.managerprofile.services.ManagerProfileService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put

fun Route.managerProfileRoutes() {

    /*
     * Get all manager profiles
     *
     * GET /api/manager-profiles
     */
    get {
        val managerProfiles =
            dbQuery {
                ManagerProfileRepository.getAll()
            }

        call.respond(
            HttpStatusCode.OK,
            managerProfiles
        )
    }

    /*
     * Get manager profile by account ID
     *
     * GET /api/manager-profiles/account/{accountId}
     */
    get("/account/{accountId}") {
        val accountId =
            call.parameters["accountId"]
                ?.toIntOrNull()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid account ID"
                )

        if (accountId <= 0) {
            return@get call.respond(
                HttpStatusCode.BadRequest,
                "Account ID must be greater than zero"
            )
        }

        val managerProfile =
            dbQuery {
                ManagerProfileRepository
                    .getByAccountId(
                        accountId
                    )
            }
                ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    "Manager profile not found for this account"
                )

        call.respond(
            HttpStatusCode.OK,
            managerProfile
        )
    }

    /*
     * Get manager profiles by region ID
     *
     * GET /api/manager-profiles/region/{regionId}
     */
    get("/region/{regionId}") {
        val regionId =
            call.parameters["regionId"]
                ?.toIntOrNull()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid region ID"
                )

        if (regionId <= 0) {
            return@get call.respond(
                HttpStatusCode.BadRequest,
                "Region ID must be greater than zero"
            )
        }

        val regionExists =
            dbQuery {
                ManagerProfileRepository
                    .regionExists(
                        regionId
                    )
            }

        if (!regionExists) {
            return@get call.respond(
                HttpStatusCode.NotFound,
                "Region not found"
            )
        }

        val managerProfiles =
            dbQuery {
                ManagerProfileRepository
                    .getByRegionId(
                        regionId
                    )
            }

        call.respond(
            HttpStatusCode.OK,
            managerProfiles
        )
    }

    /*
     * Get one manager profile
     *
     * GET /api/manager-profiles/{id}
     */
    get("/{id}") {
        val id =
            call.parameters["id"]
                ?.toIntOrNull()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid manager profile ID"
                )

        if (id <= 0) {
            return@get call.respond(
                HttpStatusCode.BadRequest,
                "Manager profile ID must be greater than zero"
            )
        }

        val managerProfile =
            dbQuery {
                ManagerProfileRepository
                    .getById(id)
            }
                ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    "Manager profile not found"
                )

        call.respond(
            HttpStatusCode.OK,
            managerProfile
        )
    }

    /*
     * Create manager profile
     *
     * POST /api/manager-profiles
     */
    post {
        val request =
            call.receive<
                    ManagerProfileRequest
                    >()

        if (request.accountId <= 0) {
            return@post call.respond(
                HttpStatusCode.BadRequest,
                "Account ID must be greater than zero"
            )
        }

        if (request.regionId <= 0) {
            return@post call.respond(
                HttpStatusCode.BadRequest,
                "Region ID must be greater than zero"
            )
        }

        val result =
            dbQuery {
                ManagerProfileService
                    .create(request)
            }

        call.respondToManagerProfileResult(
            result = result,
            successStatus =
                HttpStatusCode.Created,
            successAction =
                "created"
        )
    }

    /*
     * Update manager profile
     *
     * PUT /api/manager-profiles/{id}
     */
    put("/{id}") {
        val id =
            call.parameters["id"]
                ?.toIntOrNull()
                ?: return@put call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid manager profile ID"
                )

        if (id <= 0) {
            return@put call.respond(
                HttpStatusCode.BadRequest,
                "Manager profile ID must be greater than zero"
            )
        }

        val request =
            call.receive<
                    ManagerProfileRequest
                    >()

        if (request.accountId <= 0) {
            return@put call.respond(
                HttpStatusCode.BadRequest,
                "Account ID must be greater than zero"
            )
        }

        if (request.regionId <= 0) {
            return@put call.respond(
                HttpStatusCode.BadRequest,
                "Region ID must be greater than zero"
            )
        }

        val result =
            dbQuery {
                ManagerProfileService.update(
                    id = id,
                    request = request
                )
            }

        call.respondToManagerProfileResult(
            result = result,
            successStatus =
                HttpStatusCode.OK,
            successAction =
                "updated"
        )
    }

    /*
     * Delete manager profile
     *
     * DELETE /api/manager-profiles/{id}
     */
    delete("/{id}") {
        val id =
            call.parameters["id"]
                ?.toIntOrNull()
                ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid manager profile ID"
                )

        if (id <= 0) {
            return@delete call.respond(
                HttpStatusCode.BadRequest,
                "Manager profile ID must be greater than zero"
            )
        }

        val deleted: Boolean =
            dbQuery {
                ManagerProfileRepository
                    .deleteManagerProfile(id)
            }

        if (!deleted) {
            return@delete call.respond(
                HttpStatusCode.NotFound,
                "Manager profile not found"
            )
        }

        println(
            "Manager profile deleted: $id"
        )

        call.respond(
            HttpStatusCode.OK,
            "Manager profile deleted successfully"
        )
    }
}

/*
 * This helper is outside managerProfileRoutes(),
 * but the delete route above must remain inside it.
 */
private suspend fun ApplicationCall
        .respondToManagerProfileResult(
    result: ManagerProfileOperationResult,
    successStatus: HttpStatusCode,
    successAction: String
) {
    when (result) {
        ManagerProfileOperationResult
            .AccountNotFound -> {
            respond(
                HttpStatusCode.NotFound,
                "Account not found"
            )
        }

        ManagerProfileOperationResult
            .RegionNotFound -> {
            respond(
                HttpStatusCode.NotFound,
                "Region not found"
            )
        }

        ManagerProfileOperationResult
            .ManagerProfileNotFound -> {
            respond(
                HttpStatusCode.NotFound,
                "Manager profile not found"
            )
        }

        ManagerProfileOperationResult
            .AccountAlreadyAssigned -> {
            respond(
                HttpStatusCode.Conflict,
                "This account already has a manager profile"
            )
        }

        ManagerProfileOperationResult
            .Failed -> {
            respond(
                HttpStatusCode.InternalServerError,
                "Manager profile operation failed"
            )
        }

        is ManagerProfileOperationResult
        .Success -> {
            val managerProfile =
                result.managerProfile

            println(
                "Manager profile $successAction: " +
                        "profileId=${managerProfile.id}, " +
                        "accountId=${managerProfile.accountId}, " +
                        "displayName=${managerProfile.displayName}, " +
                        "regionId=${managerProfile.regionId}, " +
                        "regionName=${managerProfile.regionName}"
            )

            respond(
                successStatus,
                managerProfile
            )
        }
    }
}

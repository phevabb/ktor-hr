package com.hr.staff.routes

import com.hr.auth.dtos.AuthMessageResponse
import com.hr.auth.models.AuthPrincipal
import com.hr.config.DatabaseFactory.dbQuery
import com.hr.staff.services.StaffResult
import com.hr.staff.services.StaffService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.staffRoutes() {

    authenticate("auth-jwt") {

        /*
         * Get staff directory
         *
         * Admin:
         * Returns all staff.
         *
         * Manager:
         * Returns staff from the manager's region.
         *
         * Staff:
         * Returns only the authenticated account.
         *
         * GET /api/staff
         */
        get {
            val principal =
                call.principal<AuthPrincipal>()
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        AuthMessageResponse(
                            detail =
                                "Authentication is required."
                        )
                    )

            val staff =
                try {
                    dbQuery {
                        StaffService.getAll(
                            principal
                        )
                    }
                } catch (exception: Exception) {
                    println(
                        "Unable to retrieve staff directory: " +
                                exception.message
                    )

                    return@get call.respond(
                        HttpStatusCode.InternalServerError,
                        AuthMessageResponse(
                            detail =
                                "The staff directory could not be retrieved."
                        )
                    )
                }

            call.respond(
                HttpStatusCode.OK,
                staff
            )
        }

        /*
         * Get one staff account
         *
         * GET /api/staff/{id}
         */
        get("/{id}") {
            val principal =
                call.principal<AuthPrincipal>()
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        AuthMessageResponse(
                            detail =
                                "Authentication is required."
                        )
                    )

            val staffId =
                call.parameters["id"]
                    ?.toIntOrNull()
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        AuthMessageResponse(
                            detail =
                                "Invalid staff ID."
                        )
                    )

            if (staffId <= 0) {
                return@get call.respond(
                    HttpStatusCode.BadRequest,
                    AuthMessageResponse(
                        detail =
                            "Staff ID must be greater than zero."
                    )
                )
            }

            val result =
                try {
                    dbQuery {
                        StaffService.getById(
                            principal = principal,
                            staffId = staffId
                        )
                    }
                } catch (exception: Exception) {
                    println(
                        "Unable to retrieve staff account $staffId: " +
                                exception.message
                    )

                    return@get call.respond(
                        HttpStatusCode.InternalServerError,
                        AuthMessageResponse(
                            detail =
                                "The staff account could not be retrieved."
                        )
                    )
                }

            when (result) {
                is StaffResult.Success -> {
                    call.respond(
                        HttpStatusCode.OK,
                        result.staff
                    )
                }

                StaffResult.NotFound -> {
                    call.respond(
                        HttpStatusCode.NotFound,
                        AuthMessageResponse(
                            detail =
                                "Staff account not found."
                        )
                    )
                }

                StaffResult.AccessDenied -> {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        AuthMessageResponse(
                            detail =
                                "You do not have permission to view this staff account."
                        )
                    )
                }

                StaffResult.ManagerRegionMissing -> {
                    call.respond(
                        HttpStatusCode.Forbidden,
                        AuthMessageResponse(
                            detail =
                                "Your Manager account does not have an assigned region."
                        )
                    )
                }
            }
        }
    }
}
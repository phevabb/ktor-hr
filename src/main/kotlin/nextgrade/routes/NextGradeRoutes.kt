package com.hr.nextgrade.routes


import com.hr.nextgrade.dtos.NextGradeMessageResponse
import com.hr.nextgrade.dtos.NextGradeRequest
import com.hr.nextgrade.repos.NextGradeRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.nextGradeRoutes() {

    route("/next-grades") {

        get {
            try {
                val nextGrades =
                    NextGradeRepository.getAll()

                call.respond(
                    status = HttpStatusCode.OK,
                    message = nextGrades
                )
            } catch (error: Exception) {
                println(
                    "Unable to retrieve next grades: " +
                            error.message
                )

                call.respond(
                    status =
                        HttpStatusCode.InternalServerError,
                    message = NextGradeMessageResponse(
                        message =
                            "Unable to retrieve next grades"
                    )
                )
            }
        }

        get("/{id}") {
            val id = call.parameters["id"]
                ?.toIntOrNull()

            if (id == null || id <= 0) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = NextGradeMessageResponse(
                        message =
                            "A valid next grade ID is required"
                    )
                )

                return@get
            }

            try {
                val nextGrade =
                    NextGradeRepository.getById(id)

                if (nextGrade == null) {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = NextGradeMessageResponse(
                            message =
                                "Next grade not found"
                        )
                    )

                    return@get
                }

                call.respond(
                    status = HttpStatusCode.OK,
                    message = nextGrade
                )
            } catch (error: Exception) {
                println(
                    "Unable to retrieve next grade: " +
                            error.message
                )

                call.respond(
                    status =
                        HttpStatusCode.InternalServerError,
                    message = NextGradeMessageResponse(
                        message =
                            "Unable to retrieve next grade"
                    )
                )
            }
        }

        post {
            val request = try {
                call.receive<NextGradeRequest>()
            } catch (error: Exception) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = NextGradeMessageResponse(
                        message =
                            "Invalid request body"
                    )
                )

                return@post
            }

            val normalizedNextGrade =
                request.nextGrade.trim()

            val validationMessage =
                validateNextGrade(
                    nextGrade = normalizedNextGrade
                )

            if (validationMessage != null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = NextGradeMessageResponse(
                        message = validationMessage
                    )
                )

                return@post
            }

            try {
                val alreadyExists =
                    NextGradeRepository.existsByName(
                        nextGrade = normalizedNextGrade
                    )

                if (alreadyExists) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = NextGradeMessageResponse(
                            message =
                                "Next grade already exists"
                        )
                    )

                    return@post
                }

                val createdId =
                    NextGradeRepository.create(
                        request = NextGradeRequest(
                            nextGrade =
                                normalizedNextGrade
                        )
                    )

                val createdNextGrade =
                    NextGradeRepository.getById(
                        id = createdId
                    )

                if (createdNextGrade == null) {
                    call.respond(
                        status =
                            HttpStatusCode.InternalServerError,
                        message = NextGradeMessageResponse(
                            message =
                                "Next grade was created but could not be retrieved"
                        )
                    )

                    return@post
                }

                call.respond(
                    status = HttpStatusCode.Created,
                    message = createdNextGrade
                )
            } catch (error: Exception) {
                println(
                    "Unable to create next grade: " +
                            error.message
                )

                val isDuplicate =
                    error.message
                        ?.contains(
                            other = "unique",
                            ignoreCase = true
                        ) == true ||
                            error.message
                                ?.contains(
                                    other = "duplicate",
                                    ignoreCase = true
                                ) == true

                if (isDuplicate) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = NextGradeMessageResponse(
                            message =
                                "Next grade already exists"
                        )
                    )

                    return@post
                }

                call.respond(
                    status =
                        HttpStatusCode.InternalServerError,
                    message = NextGradeMessageResponse(
                        message =
                            "Unable to create next grade"
                    )
                )
            }
        }

        put("/{id}") {
            val id = call.parameters["id"]
                ?.toIntOrNull()

            if (id == null || id <= 0) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = NextGradeMessageResponse(
                        message =
                            "A valid next grade ID is required"
                    )
                )

                return@put
            }

            val request = try {
                call.receive<NextGradeRequest>()
            } catch (error: Exception) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = NextGradeMessageResponse(
                        message =
                            "Invalid request body"
                    )
                )

                return@put
            }

            val normalizedNextGrade =
                request.nextGrade.trim()

            val validationMessage =
                validateNextGrade(
                    nextGrade = normalizedNextGrade
                )

            if (validationMessage != null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = NextGradeMessageResponse(
                        message = validationMessage
                    )
                )

                return@put
            }

            try {
                val existingNextGrade =
                    NextGradeRepository.getById(id)

                if (existingNextGrade == null) {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = NextGradeMessageResponse(
                            message =
                                "Next grade not found"
                        )
                    )

                    return@put
                }

                val alreadyExists =
                    NextGradeRepository
                        .existsByNameExcludingId(
                            nextGrade =
                                normalizedNextGrade,
                            excludedId = id
                        )

                if (alreadyExists) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = NextGradeMessageResponse(
                            message =
                                "Next grade already exists"
                        )
                    )

                    return@put
                }

                val updated =
                    NextGradeRepository.update(
                        id = id,
                        request = NextGradeRequest(
                            nextGrade =
                                normalizedNextGrade
                        )
                    )

                if (!updated) {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = NextGradeMessageResponse(
                            message =
                                "Next grade not found"
                        )
                    )

                    return@put
                }

                val updatedNextGrade =
                    NextGradeRepository.getById(id)

                if (updatedNextGrade == null) {
                    call.respond(
                        status =
                            HttpStatusCode.InternalServerError,
                        message = NextGradeMessageResponse(
                            message =
                                "Next grade was updated but could not be retrieved"
                        )
                    )

                    return@put
                }

                call.respond(
                    status = HttpStatusCode.OK,
                    message = updatedNextGrade
                )
            } catch (error: Exception) {
                println(
                    "Unable to update next grade: " +
                            error.message
                )

                val isDuplicate =
                    error.message
                        ?.contains(
                            other = "unique",
                            ignoreCase = true
                        ) == true ||
                            error.message
                                ?.contains(
                                    other = "duplicate",
                                    ignoreCase = true
                                ) == true

                if (isDuplicate) {
                    call.respond(
                        status = HttpStatusCode.Conflict,
                        message = NextGradeMessageResponse(
                            message =
                                "Next grade already exists"
                        )
                    )

                    return@put
                }

                call.respond(
                    status =
                        HttpStatusCode.InternalServerError,
                    message = NextGradeMessageResponse(
                        message =
                            "Unable to update next grade"
                    )
                )
            }
        }

        delete("/{id}") {
            val id = call.parameters["id"]
                ?.toIntOrNull()

            if (id == null || id <= 0) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = NextGradeMessageResponse(
                        message =
                            "A valid next grade ID is required"
                    )
                )

                return@delete
            }

            try {
                val existingNextGrade =
                    NextGradeRepository.getById(id)

                if (existingNextGrade == null) {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = NextGradeMessageResponse(
                            message =
                                "Next grade not found"
                        )
                    )

                    return@delete
                }

                val deleted =
                    NextGradeRepository.delete(id)

                if (!deleted) {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = NextGradeMessageResponse(
                            message =
                                "Next grade not found"
                        )
                    )

                    return@delete
                }

                call.respond(
                    status = HttpStatusCode.OK,
                    message = NextGradeMessageResponse(
                        message =
                            "Next grade deleted successfully"
                    )
                )
            } catch (error: Exception) {
                println(
                    "Unable to delete next grade: " +
                            error.message
                )

                call.respond(
                    status =
                        HttpStatusCode.InternalServerError,
                    message = NextGradeMessageResponse(
                        message =
                            "Unable to delete next grade"
                    )
                )
            }
        }
    }
}

private fun validateNextGrade(
    nextGrade: String
): String? {
    if (nextGrade.isBlank()) {
        return "Next grade is required"
    }

    if (nextGrade.length < 2) {
        return "Next grade must contain at least 2 characters"
    }

    if (nextGrade.length > 100) {
        return "Next grade cannot exceed 100 characters"
    }

    return null
}
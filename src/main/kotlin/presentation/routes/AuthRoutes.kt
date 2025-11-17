package org.example.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.domain.service.UserService
import org.example.model.UserDTO
import org.koin.ktor.ext.inject

fun Route.authRoutes() {

    val service by inject<UserService>()

    post("/register") {
        val body = call.receive<UserDTO>()
        if (!service.registerUser(body)){
            call.respond(HttpStatusCode.Conflict,"Username exists")
        }else{
            call.respond(HttpStatusCode.Created, "Registered.")
        }
    }

    post("/login") {
        val body = call.receive<UserDTO>()
        if (service.login(body)){
            call.respond(HttpStatusCode.OK,"Login success")
        }else{
            call.respond(HttpStatusCode.Unauthorized,"Invalid")
        }
    }

    post("/update") {
        val body = call.receive<UserDTO>()
        if (service.updatePassword(body)){
            call.respond(HttpStatusCode.OK,"Updated")
        }else{
            call.respond(HttpStatusCode.NotFound,"User not found")
        }
    }

    delete("/delete/{username}") {
        val username = call.parameters["username"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
        if (service.deleteUser(username))
            call.respond(HttpStatusCode.OK, "Deleted.")
        else
            call.respond(HttpStatusCode.NotFound, "Not found.")
    }
}


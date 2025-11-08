package org.example.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import at.favre.lib.crypto.bcrypt.BCrypt
import org.example.model.UserDTO
import org.example.model.UsersTable

fun Route.authRoutes() {

    post("/register") {
        val newUser = call.receive<UserDTO>()

        val exists = transaction {
            UsersTable.selectAll()
                .any { it[UsersTable.username] == newUser.username }
        }

        if (exists) {
            call.respond(HttpStatusCode.Conflict, "Username already exists.")
            return@post
        }

        val hashedPassword = BCrypt.withDefaults().hashToString(12, newUser.password.toCharArray())

        transaction {
            UsersTable.insert {
                it[username] = newUser.username
                it[password] = hashedPassword
            }
        }

        call.respond(HttpStatusCode.Created, "User registered successfully.")
    }

    post("/login") {
        val user = call.receive<UserDTO>()

        val storedHash = transaction {
            UsersTable.selectAll()
                .firstOrNull { it[UsersTable.username] == user.username }
                ?.get(UsersTable.password)
        }

        if (storedHash == null) {
            call.respond(HttpStatusCode.Unauthorized, "Invalid credentials.")
            return@post
        }

        val result = BCrypt.verifyer().verify(user.password.toCharArray(), storedHash)
        if (!result.verified) {
            call.respond(HttpStatusCode.Unauthorized, "Invalid credentials.")
            return@post
        }

        call.respond(HttpStatusCode.OK, "Login successful.")
    }
}


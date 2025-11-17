package org.example.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import at.favre.lib.crypto.bcrypt.BCrypt
import org.example.data.model.UsersTable
import org.example.model.UserDTO
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

fun Route.authRoutes() {

    get("/users") {
        val users = transaction {
            UsersTable.selectAll().map { row->
                UserDTO(
                    username = row[UsersTable.username],
                    password = row[UsersTable.password]
                )
            }
        }

        call.respond(HttpStatusCode.OK, users)
    }

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

    post("/update"){
        val user = call.receive<UserDTO>()

        val updated = transaction {
            val storedUser = UsersTable.selectAll().firstOrNull(){it[UsersTable.username] == user.username}?.get(
                UsersTable.password)
            if (storedUser != null){
                val hashedPassword = BCrypt.withDefaults().hashToString(12,user.password.toCharArray())
                UsersTable.update({ UsersTable.username eq user.username}){
                    it[password] = hashedPassword
                }
                true
            }else{
                false
            }
        }

        if (updated){
            call.respond(HttpStatusCode.OK, "User updated successfully.")
        } else {
            call.respond(HttpStatusCode.NotFound, "User not found.")
        }
    }

    delete("/delete/{username"){
        val username = call.parameters["username"]

        if (username == null){
            call.respond(HttpStatusCode.BadRequest, "Username required.")
            return@delete
        }

        val deleted = transaction {
            UsersTable.deleteWhere{ UsersTable.username eq username }
        }

        if (deleted > 0){
            call.respond(HttpStatusCode.OK,"User deleted successfully")
        } else {
            call.respond(HttpStatusCode.NotFound, "User not found.")
        }
    }
}


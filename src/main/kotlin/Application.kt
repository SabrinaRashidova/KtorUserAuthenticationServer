package org.example

import io.github.cdimascio.dotenv.dotenv
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import org.example.data.model.UsersTable
import org.example.routes.authRoutes
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction


fun main(){
    connectDatabase()

    embeddedServer(Netty, port = 8080){
        install(ContentNegotiation){
            json()
        }

        routing {
            authRoutes()
        }
    }.start(wait = true)

}

fun connectDatabase(){
    val dotenv = dotenv()

    val host = dotenv["DB_HOST"] ?: error("DB_HOST missing")
    val port = dotenv["DB_PORT"] ?: "5432"
    val dbName = dotenv["DB_NAME"] ?: error("DB_NAME missing")
    val dbUser = dotenv["DB_USER"] ?: error("DB_USER missing")
    val dbPass = dotenv["DB_PASSWORD"] ?: error("DB_PASSWORD missing")

    val jdbcUrl = "jdbc:postgresql://$host:$port/$dbName"

    Database.connect(
        url = jdbcUrl,
        driver = "org.postgresql.Driver",
        user = dbUser,
        password = dbPass
    )

    transaction {
        SchemaUtils.create(UsersTable)
    }
}



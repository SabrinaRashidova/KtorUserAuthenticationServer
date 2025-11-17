package org.example.data.model

import org.jetbrains.exposed.sql.Table

object UsersTable : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val password = varchar("password", 64)
    override val primaryKey = PrimaryKey(id)
}

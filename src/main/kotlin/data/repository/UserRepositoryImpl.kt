package org.example.data.repository

import org.example.data.model.UsersTable
import org.example.domain.service.UserRepository
import org.example.model.User
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class UserRepositoryImpl : UserRepository {
    override fun findByUsername(username: String): User? = transaction{
        UsersTable.selectAll().where { UsersTable.username eq username }
            .map { User(it[UsersTable.username], it[UsersTable.password]) }
            .firstOrNull()
    }

    override fun create(user: User): Unit = transaction{
        UsersTable.insert {
            it[username] = user.username
            it[password] = user.password
        }
    }

    override fun updatePassword(username: String, newPassword: String): Boolean = transaction {
        UsersTable.update( { UsersTable.username eq username}){
            it[password] = newPassword
        }>0
    }

    override fun delete(username: String): Boolean = transaction{
        UsersTable.deleteWhere{ UsersTable.username eq username } > 0
    }


}
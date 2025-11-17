package org.example.domain.service

import org.example.model.User

interface UserRepository {
    fun findByUsername(username: String) : User?
    fun create(user: User)
    fun updatePassword(username: String, newPassword: String) : Boolean
    fun delete(username: String) :  Boolean
}
package org.example.domain.service

import at.favre.lib.crypto.bcrypt.BCrypt
import org.example.model.User
import org.example.model.UserDTO

class UserService(
    private val repository: UserRepository
) {
    fun registerUser(dto: UserDTO) : Boolean{
        if (repository.findByUsername(dto.username) != null) return false

        val hashed = BCrypt.withDefaults().hashToString(12,dto.password.toCharArray())
        repository.create(User(dto.username,hashed))
        return true
    }

    fun login(dto: UserDTO) : Boolean{
        val user = repository.findByUsername(dto.username) ?: return false
        return BCrypt.verifyer().verify(dto.password.toCharArray(),user.password).verified
    }

    fun updatePassword(dto: UserDTO) : Boolean{
        val hashed = BCrypt.withDefaults().hashToString(12,dto.password.toCharArray())
        return repository.updatePassword(dto.username,hashed)
    }

    fun deleteUser(username: String): Boolean {
        return repository.delete(username)
    }
}
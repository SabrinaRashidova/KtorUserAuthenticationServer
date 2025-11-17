package org.example.di

import org.example.data.repository.UserRepositoryImpl
import org.example.domain.service.UserRepository
import org.example.domain.service.UserService
import org.koin.dsl.module

val appModule = module {

    single<UserRepository> { UserRepositoryImpl() }

    single { UserService(get()) }
}
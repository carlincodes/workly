package com.example.workly.di

import com.example.workly.api.RetrofitClient
import com.example.workly.repository.ApiRepository
import com.example.workly.repository.ChatRepository
import com.example.workly.repository.ProfileRepository
import com.example.workly.repository.ServiceRepository
import com.example.workly.repository.UserRepository
import com.example.workly.service.ChatService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.dsl.module

val repositoryModule = module {

    // Firebase
    single { FirebaseFirestore.getInstance() }
    single { FirebaseAuth.getInstance() }

    // Retrofit
    single { RetrofitClient.apiService }

    // Services
    single { ChatService(get()) }

    // Repositories
    single { ServiceRepository(get(), get()) }
    single { ProfileRepository() }
    single { ApiRepository() }
    single { UserRepository() }
    single { ChatRepository() }
}
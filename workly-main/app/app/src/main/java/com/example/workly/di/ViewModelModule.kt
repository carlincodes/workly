package com.example.workly.di

import com.example.workly.viewmodel.AuthViewModel
import com.example.workly.viewmodel.HomeViewModel
import com.example.workly.viewmodel.ProfileViewModel
import com.example.workly.viewmodel.ServiceViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        HomeViewModel(get())
    }

    viewModel {
        ProfileViewModel(get())
    }

    viewModel {
        ServiceViewModel(get(), get())
    }

    viewModel {
        AuthViewModel()
    }
}
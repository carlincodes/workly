package com.example.workly.di

import org.koin.dsl.module

val appModule = listOf(

    databaseModule,

    repositoryModule,

    viewModelModule

)
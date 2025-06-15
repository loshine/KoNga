package io.github.loshine.konga.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@ComponentScan("io.github.loshine.**")
@Module(includes = [DataModule::class])
class AppModule

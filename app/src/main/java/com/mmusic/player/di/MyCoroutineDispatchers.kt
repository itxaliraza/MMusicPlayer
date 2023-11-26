package com.mmusic.player.di

import javax.inject.Qualifier

@Qualifier
annotation class MyDispatcher(val myCoroutineDispatcher:MyCoroutineDispatchers)

enum class MyCoroutineDispatchers {
    MAIN,
    IO
}
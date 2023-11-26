package com.mmusic.player.di

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.mmusic.player.data.MediaRepositoryImpl
import com.mmusic.player.data.PrefHelper
import com.mmusic.player.domain.MediaRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMediaRepository(
        @ApplicationContext context: Context,
        @MyDispatcher(MyCoroutineDispatchers.IO) ioDispatcher: CoroutineDispatcher
    ): MediaRepository {
        return MediaRepositoryImpl(context, ioDispatcher)
    }
    @Provides
    @Singleton
    fun providePrefHelper(
        @ApplicationContext context: Context,
     ): PrefHelper {
        return PrefHelper(context)
    }

    @Provides
    @MyDispatcher(MyCoroutineDispatchers.MAIN)
    @Singleton
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @MyDispatcher(MyCoroutineDispatchers.IO)
    @Singleton
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context):NotificationManager? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getSystemService(NotificationManager::class.java)
        } else {
            null
        }
}
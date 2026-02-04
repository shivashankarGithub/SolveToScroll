package com.solvetoscroll.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.solvetoscroll.blocker.AccessGrantManager
import com.solvetoscroll.blocker.UsageMonitor
import com.solvetoscroll.challenge.ChallengeSelector
import com.solvetoscroll.challenge.DifficultyManager
import com.solvetoscroll.challenge.WaitTimerManager
import com.solvetoscroll.challenge.breathing.BreathingChallengeGenerator
import com.solvetoscroll.challenge.math.MathChallengeGenerator
import com.solvetoscroll.challenge.memory.MemoryChallengeGenerator
import com.solvetoscroll.challenge.reflection.ReflectionChallengeGenerator
import com.solvetoscroll.challenge.reflection.ReflectionValidator
import com.solvetoscroll.challenge.typing.TypingChallengeGenerator
import com.solvetoscroll.challenge.word.WordChallengeGenerator
import com.solvetoscroll.data.AppDatabase
import com.solvetoscroll.data.dao.AttemptDao
import com.solvetoscroll.data.dao.BlockedAppDao
import com.solvetoscroll.data.dao.ScheduleDao
import com.solvetoscroll.scheduler.ScheduleChecker
import com.solvetoscroll.scheduler.ScheduleManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "solvetoscroll_db"
        ).build()
    }
    
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
    
    @Provides
    fun provideBlockedAppDao(database: AppDatabase): BlockedAppDao {
        return database.blockedAppDao()
    }
    
    @Provides
    fun provideScheduleDao(database: AppDatabase): ScheduleDao {
        return database.scheduleDao()
    }
    
    @Provides
    fun provideAttemptDao(database: AppDatabase): AttemptDao {
        return database.attemptDao()
    }
    
    @Provides
    @Singleton
    fun provideUsageMonitor(@ApplicationContext context: Context): UsageMonitor {
        return UsageMonitor(context)
    }
    
    @Provides
    @Singleton
    fun provideAccessGrantManager(): AccessGrantManager {
        return AccessGrantManager()
    }
    
    @Provides
    @Singleton
    fun provideScheduleChecker(
        blockedAppDao: BlockedAppDao,
        scheduleDao: ScheduleDao
    ): ScheduleChecker {
        return ScheduleChecker(blockedAppDao, scheduleDao)
    }
    
    @Provides
    @Singleton
    fun provideScheduleManager(
        blockedAppDao: BlockedAppDao,
        scheduleDao: ScheduleDao
    ): ScheduleManager {
        return ScheduleManager(blockedAppDao, scheduleDao)
    }
    
    @Provides
    @Singleton
    fun provideMathChallengeGenerator(): MathChallengeGenerator {
        return MathChallengeGenerator()
    }
    
    @Provides
    @Singleton
    fun provideTypingChallengeGenerator(): TypingChallengeGenerator {
        return TypingChallengeGenerator()
    }
    
    @Provides
    @Singleton
    fun provideReflectionChallengeGenerator(): ReflectionChallengeGenerator {
        return ReflectionChallengeGenerator()
    }
    
    @Provides
    @Singleton
    fun provideReflectionValidator(): ReflectionValidator {
        return ReflectionValidator()
    }
    
    @Provides
    @Singleton
    fun provideMemoryChallengeGenerator(): MemoryChallengeGenerator {
        return MemoryChallengeGenerator()
    }
    
    @Provides
    @Singleton
    fun provideWordChallengeGenerator(): WordChallengeGenerator {
        return WordChallengeGenerator()
    }
    
    @Provides
    @Singleton
    fun provideBreathingChallengeGenerator(): BreathingChallengeGenerator {
        return BreathingChallengeGenerator()
    }
    
    @Provides
    @Singleton
    fun provideChallengeSelector(
        mathGenerator: MathChallengeGenerator,
        typingGenerator: TypingChallengeGenerator,
        reflectionGenerator: ReflectionChallengeGenerator,
        memoryGenerator: MemoryChallengeGenerator,
        wordGenerator: WordChallengeGenerator,
        breathingGenerator: BreathingChallengeGenerator
    ): ChallengeSelector {
        return ChallengeSelector(
            mathGenerator,
            typingGenerator,
            reflectionGenerator,
            memoryGenerator,
            wordGenerator,
            breathingGenerator
        )
    }
    
    @Provides
    @Singleton
    fun provideDifficultyManager(attemptDao: AttemptDao): DifficultyManager {
        return DifficultyManager(attemptDao)
    }
    
    @Provides
    @Singleton
    fun provideWaitTimerManager(): WaitTimerManager {
        return WaitTimerManager()
    }
}

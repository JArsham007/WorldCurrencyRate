package com.example.worldcurrencyrate.di

import com.example.worldcurrencyrate.domain.repository.CurrencyRepository
import com.example.worldcurrencyrate.domain.usecases.*
import com.example.worldcurrencyrate.presentation.screens.track_widget_screen.TrackWidget
import com.example.worldcurrencyrate.presentation.utils.AxisValueOverrider
import com.example.worldcurrencyrate.presentation.utils.ChartXAxisValueFormatter
import com.example.worldcurrencyrate.presentation.utils.ChartYAxisValueFormatter
import com.example.worldcurrencyrate.utils.Symbols
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideUseCases(
        repository: CurrencyRepository
    ): UseCases {
        return UseCases(
            convertCurrency = ConvertCurrency(repository),
            fluctuationCurrency = FluctuationCurrency(repository),
            timeSeriesCurrency = TimeSeriesCurrency(repository),
            latestCurrency = LatestCurrency(repository)
        )
    }

    @Provides
    @Singleton
    fun provideAxisValueOverrider(): AxisValueOverrider {
        return AxisValueOverrider()
    }

    @Provides
    @Singleton
    fun provideChartXAxisValueFormatter(): ChartXAxisValueFormatter {
        return ChartXAxisValueFormatter()
    }

    @Provides
    @Singleton
    fun provideChartYAxisValueFormatter(): ChartYAxisValueFormatter {
        return ChartYAxisValueFormatter()
    }

    @Provides
    @Singleton
    fun provideSymbols(): Symbols {
        return Symbols()
    }

    @Provides
    @Singleton
    fun provideTrackWidget(): TrackWidget {
        return TrackWidget()
    }

}
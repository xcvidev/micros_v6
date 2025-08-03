package com.xcvi.micros

import android.app.Application
import androidx.room.Room
import com.xcvi.micros.data.repository.FoodRepositoryImplementation
import com.xcvi.micros.data.repository.MessageRepositoryImplementation
import com.xcvi.micros.data.repository.PortionRepositoryImplementation
import com.xcvi.micros.data.repository.WeightRepositoryImplementation
import com.xcvi.micros.data.source.local.MicrosDatabase
import com.xcvi.micros.data.source.remote.AiApi
import com.xcvi.micros.data.source.remote.ProductApi
import com.xcvi.micros.domain.respostory.FoodRepository
import com.xcvi.micros.domain.respostory.MessageRepository
import com.xcvi.micros.domain.respostory.PortionRepository
import com.xcvi.micros.domain.respostory.WeightRepository
import com.xcvi.micros.domain.usecases.DashboardUseCases
import com.xcvi.micros.domain.usecases.DetailsUseCases
import com.xcvi.micros.domain.usecases.GoalsUseCases
import com.xcvi.micros.domain.usecases.MealUseCases
import com.xcvi.micros.domain.usecases.MessageUseCases
import com.xcvi.micros.domain.usecases.ScanUseCases
import com.xcvi.micros.domain.usecases.SearchUseCases
import com.xcvi.micros.domain.usecases.StatsUseCases
import com.xcvi.micros.domain.usecases.WeightUseCases
import com.xcvi.micros.preferences.UserPreferences
import com.xcvi.micros.ui.screens.dashboard.DashboardViewModel
import com.xcvi.micros.ui.screens.details.DetailsViewModel
import com.xcvi.micros.ui.screens.goals.GoalsViewModel
import com.xcvi.micros.ui.screens.meal.MealViewModel
import com.xcvi.micros.ui.screens.message.MessageViewModel
import com.xcvi.micros.ui.screens.scan.ScanViewModel
import com.xcvi.micros.ui.screens.search.SearchViewModel
import com.xcvi.micros.ui.screens.stats.StatsViewModel
import com.xcvi.micros.ui.screens.weight.WeightViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.HttpTimeout
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

class Micros: Application() {

    override fun onCreate() {
        super.onCreate()
        UserPreferences.init(this)
        startKoin {
            androidContext(this@Micros)
        }
        loadKoinModules(
            listOf(
                dbModule,
                viewModelModule,
                apiModule,
                repositoryModule,
                useCasesModule
            )
        )
    }


    private val viewModelModule = module {
        viewModel { DashboardViewModel(get(),get()) }
        viewModel { StatsViewModel(get()) }
        viewModel { GoalsViewModel(get()) }
        viewModel { MealViewModel(get()) }
        viewModel { SearchViewModel(get()) }
        viewModel { ScanViewModel(get()) }
        viewModel { DetailsViewModel(get(), get()) }
        viewModel { MessageViewModel(get()) }
        viewModel { WeightViewModel(get()) }
    }
    private val useCasesModule = module {
        factory { MessageUseCases(get()) }
        factory { WeightUseCases(get()) }
        factory { DashboardUseCases(get()) }
        factory { DetailsUseCases(get(), get(), get()) }
        factory { GoalsUseCases(get()) }
        factory { MealUseCases(get()) }
        factory { ScanUseCases(get()) }
        factory { SearchUseCases(get(), get()) }
        factory { StatsUseCases(get(), get()) }
    }
    private val repositoryModule = module {
        single<MessageRepository>{ MessageRepositoryImplementation(get(),get(),get()) }
        single<FoodRepository>{ FoodRepositoryImplementation(get(),get(),get()) }
        single<WeightRepository>{ WeightRepositoryImplementation(get()) }
        single<PortionRepository>{ PortionRepositoryImplementation(get()) }
    }
    private val dbModule = module {
        single {
            Room.databaseBuilder(
                androidContext(),
                MicrosDatabase::class.java,
                "micros_db"
            ).build()
        }
        single { get<MicrosDatabase>().foodDao() }
        single { get<MicrosDatabase>().portionDao() }
        single { get<MicrosDatabase>().weightDao() }
        single { get<MicrosDatabase>().messageDao() }
    }

    private val apiModule = module {
        val key = BuildConfig.OPENAI_API_KEY
        val serializerJson = Json {
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
        }
        val aiClient = HttpClient(Android) {
            install(HttpTimeout) {
                requestTimeoutMillis = 10000
                socketTimeoutMillis = 8000
                connectTimeoutMillis = 3000
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer(serializerJson)
            }
            defaultRequest {
                header("Authorization", "Bearer $key")
                contentType(ContentType.Application.Json)
            }
        }
        val productClient = HttpClient(Android) {
            install(HttpTimeout) {
                requestTimeoutMillis = 5000
                socketTimeoutMillis = 3000
                connectTimeoutMillis = 3000
            }
            install(feature = JsonFeature) {
                serializer = KotlinxSerializer(serializerJson)
            }
        }
        single { ProductApi(client = productClient) }
        single { AiApi(client = aiClient,jsonParser = serializerJson) }
    }
}
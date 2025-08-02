package com.xcvi.micros

import android.app.Application
import androidx.room.Room
import com.xcvi.micros.data.source.local.MicrosDatabase
import com.xcvi.micros.data.source.remote.AiApi
import com.xcvi.micros.data.source.remote.ProductApi
import com.xcvi.micros.preferences.UserPreferences
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

    }
    private val useCasesModule = module {

    }
    private val repositoryModule = module {

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
        single { get<MicrosDatabase>().aiDao() }
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
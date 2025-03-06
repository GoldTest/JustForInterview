package org.example.project

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.example.project.SharedInstance.json
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


interface ApiClient {
    fun fetchCoins(): Flow<ApiResponse>
    fun fetchRates(): Flow<ApiResponse>
    fun fetchBalance(): Flow<ApiResponse>
}

class ApiClientImpl(val context: Context) : ApiClient {
    override fun fetchCoins(): Flow<ApiResponse> {
        return flow {
            val responseBody = FileUtil(context).readAssetsFile("currencies.json")
            if (responseBody.isNullOrEmpty()) {
                return@flow
            }
            val response: Response = json.decodeFromString<Response>(responseBody)
            if (!response.ok || response.currencies.isEmpty()) {
                emit(ApiError(400, "response empty"))
                return@flow
            }
            //maybe eventStream or slice
            emit(response)
        }
    }

    override fun fetchRates(): Flow<ApiResponse> {
        //maybe using database is better
        return flow {
            val responseBody = FileUtil(context).readAssetsFile("live-rates.json")
            if (responseBody.isNullOrEmpty()) {
                emit(ApiError(400, "response null or parse error"))
                return@flow
            }
            val response = json.decodeFromString<LiveRates>(responseBody)
            if (!response.ok || response.tiers.isEmpty()) {
                emit(ApiError(400, "response empty"))
                return@flow
            }
            //maybe eventStream or slice
            emit(response)
        }
    }

    override fun fetchBalance(): Flow<ApiResponse> {
        return flow {
            val responseBody = FileUtil(context).readAssetsFile("wallet-balance.json")
            println(responseBody.toString()+"gggggas")
            if (responseBody.isNullOrEmpty()) {
                emit(ApiError(400, "response null or parse error"))
                return@flow
            }
            val response = json.decodeFromString<WalletResponse>(responseBody)
            if (!response.ok || response.wallet.isEmpty()) {
                emit(ApiError(400, "response empty"))
                return@flow
            }
            //maybe eventStream or slice
            emit(response)
        }
    }
}

@Serializable
data class BalanceItem(val currency: String, val amount: Double)

@Serializable
data class WalletResponse(
    val ok: Boolean,
    val warning: String,
    val wallet: List<BalanceItem>
) : ApiResponse()

@Serializable
data class Rate(val amount: String, val rate: String)

@Serializable
data class Tier(
    val from_currency: String,
    val to_currency: String,
    val rates: List<Rate>,
    val time_stamp: Long
)

@Serializable
data class LiveRates(
    val ok: Boolean,
    val warning: String,
    val tiers: List<Tier>
) : ApiResponse()


// 在工具类中：
class FileUtil(private val context: Context) {
    fun readAssetsFile(fileName: String): String? {
        return try {
            // 通过传入的 Context 获取 AssetManager
            val inputStream = context.assets.open(fileName)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            bufferedReader.use { it.readText() }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}


@Serializable
enum class LoadingState {
    IDLE,
    LOADING,
    OUTPUTTING,
    ERROR
}

@Serializable
data class Coin(
    val coin_id: String,
    val name: String,
    val symbol: String,
    val token_decimal: Int,
    val contract_address: String,
    val withdrawal_eta: List<String>,
    val colorful_image_url: String,
    val gray_image_url: String,
    val has_deposit_address_tag: Boolean,
    val min_balance: Int,
    val blockchain_symbol: String,
    val trading_symbol: String,
    val code: String,
    val explorer: String,
    val is_erc20: Boolean,
    val gas_limit: Int,
    val token_decimal_value: String,
    val display_decimal: Int,
    val supports_legacy_address: Boolean,
    val deposit_address_tag_name: String,
    val deposit_address_tag_type: String,
    val num_confirmation_required: Int
)

@Serializable
sealed class ApiResponse

@Serializable
data class ApiError(
    val code: Int,
    val message: String,
) : ApiResponse()

@Serializable
data class Response(
    val currencies: List<Coin>,
    val total: Int,
    val ok: Boolean
) : ApiResponse()


val myModule = module {
    single { androidApplication() }
    factory { ApiClientImpl(get()) }
    viewModel { MyViewModel(get<ApiClientImpl>()) }
}

object SharedInstance {
    val json = Json {
        prettyPrint = true
        encodeDefaults = true
        explicitNulls = false
        ignoreUnknownKeys = true
//        serializersModule = SerializersModule {
//            contextual(FieldValue::class, FieldValueSerializer)
//        }
//        classDiscriminator = "classDisWithType"
    }
}
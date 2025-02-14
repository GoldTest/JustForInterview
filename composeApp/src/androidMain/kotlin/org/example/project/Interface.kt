package org.example.project

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


interface ApiClient {
    fun fetchModels(): Flow<List<String>>
}

class ApiClientImpl : ApiClient {
    override fun fetchModels(): Flow<List<String>> {
        return flow {
            emit(listOf("model1", "model2", "model3", "model4", "model5"))
        }
    }
}


val myModule = module {
    factory { ApiClientImpl() }
    viewModel { MyViewModel(get<ApiClientImpl>()) }
}


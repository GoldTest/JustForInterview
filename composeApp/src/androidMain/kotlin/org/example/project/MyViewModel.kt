package org.example.project

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class MyViewModel(private val apiClient: ApiClient) : ViewModel() {


    //extract models
    private val _models = MutableStateFlow(emptyList<String>())
    val models: StateFlow<List<String>> = _models

    private val _sorted = MutableStateFlow(Array<String>(0) { "" })
    val sortedFileNames: StateFlow<Array<String>> = _sorted

    //this maybe suspend, not consider result seal for now
    fun fetchModels() {
        viewModelScope.launch {
            apiClient
                .fetchModels()
                .flowOn(Dispatchers.IO)
                .catch {
                    Log.e("MainViewModel", "Failed to get list of car models.")
                }
                .collect { _models.value = it }
        }
    }


    // 提取字符串中的数字部分
    fun String.extractNumber(): Int {
        val numberRegex = Regex("\\d+")
        val matchResult = numberRegex.find(this)
        return matchResult?.value?.toInt() ?: 0
    }

    fun sort(fileNames: Array<String>) {
        _sorted.value = fileNames.sortedWith(compareBy(
            { it.extractNumber() }, //先按数字顺序排列
            { it } //数字相同的，整体排列
        )).toTypedArray()
    }
}
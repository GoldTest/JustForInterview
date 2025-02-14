package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : ComponentActivity() {

    //using viewModel
    private lateinit var viewModel: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //init viewModel
        viewModel = getViewModel<MyViewModel>()
        setContent {
            mainPage()
        }

        //using intent to fetch data，single flow
        viewModel.fetchModels()
    }

    //rename
    @Preview
    @Composable
    fun mainPage() {
        //with lifecycle
        val models = viewModel.models.collectAsStateWithLifecycle(initialValue = emptyList()).value

        //demo theme
        MaterialTheme() {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) { Text("Code by ZHX") }
                    modelsList(models = models)
                    Spacer(Modifier.height(12.dp))
                    sort()
                }
            }
        }

    }

    //rename
    @Preview
    @Composable
    fun modelsList(modifier: Modifier = Modifier, models: List<String>) {
        //size change
        LazyColumn(modifier.fillMaxWidth()) {
            items(models) { modelItem(it) }
        }
    }

    //rename
    @Preview
    @Composable
    fun modelItem(item: String, modifier: Modifier = Modifier) {
        Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Spacer(Modifier.height(12.dp))
            Text(text = item)
        }
    }


    @Composable
    fun sort() {
        val fileNames by remember {
            mutableStateOf(
                arrayOf(
                    "file2.gif",
                    "file01.gif",
                    "1file.jpg",
                    "1file.gif",
                    "file10.gif",
                    "file1.gif",
                    "file1a.gif",
                )
            )
        }
        val sortedFileNames = viewModel.sortedFileNames.collectAsState(initial = emptyArray()).value

        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row (horizontalArrangement = Arrangement.Center){
                Button(onClick = { viewModel.sort(fileNames) }) { Text("sort") }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Column {
                    fileNames.forEach {
                        Spacer(Modifier.height(12.dp))
                        Text(it)
                    }
                }
                Spacer(Modifier.width(40.dp))
                Column {
                    sortedFileNames.forEach {
                        Spacer(Modifier.height(12.dp))
                        Text(it)
                    }
                }
            }
        }

    }
}

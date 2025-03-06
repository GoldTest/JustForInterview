package org.example.project

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.androidx.viewmodel.ext.android.getViewModel
import java.math.BigDecimal

class MainActivity : ComponentActivity() {

    //using viewModel
    private lateinit var viewModel: MyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //init viewModel with koin
        viewModel = getViewModel<MyViewModel>()
        setContent {
            mainPage()
        }

        //using intent to fetch data
        viewModel.fetchModelIntent()
        viewModel.fetchRatesIntent()
        viewModel.fetchWalletIntent()
    }


    @Preview
    @Composable
    fun mainPage() {
        //control loading state,should use a channel or eventbus to manage
        LaunchedEffect(viewModel.loading.value) {
            when (viewModel.loading.value) {
                LoadingState.IDLE -> {}
                LoadingState.LOADING -> {

                }

                LoadingState.OUTPUTTING -> {

                }

                LoadingState.ERROR -> {

                }
            }
        }

        //with lifecycle
        val coins = viewModel.models.collectAsStateWithLifecycle(initialValue = emptyList()).value
        val allCash = viewModel.allUsdCash.collectAsStateWithLifecycle(initialValue = "0.0").value

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
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(text = "$", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(text = allCash, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(text = "USD", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    }

                    coinList(models = coins)
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }


    @Preview
    @Composable
    fun coinList(modifier: Modifier = Modifier, models: List<Coin>) {
        //size change
        LazyColumn(modifier.fillMaxWidth().padding(12.dp)) {
            items(models) {
                modelItem(it)
                Spacer(Modifier.height(8.dp))
            }
        }
    }

    @SuppressLint("DefaultLocale")
    @Preview
    @Composable
    fun modelItem(item: Coin, modifier: Modifier = Modifier) {

        val balances = viewModel.balances.collectAsStateWithLifecycle(initialValue = emptyList()).value
        val rates = viewModel.rates.collectAsStateWithLifecycle(initialValue = emptyList()).value

        //range maybe exceed and cause error
        val amount by remember(balances, item.code) {
            derivedStateOf {
                balances.firstOrNull { it.currency == item.code }?.amount ?: 0.0
            }
        }
        val rate: Rate? by remember(rates, item.code) {
            derivedStateOf {
                rates.firstOrNull { it.from_currency == item.code }?.rates?.firstOrNull()
            }
        }
        val totalPrice by remember(amount, rate) {
            derivedStateOf {
                amount * (if (rate == null) 0.0 else rate?.rate?.toDouble() ?: 0.0)
            }
        }

        Card(
            elevation = 6.dp,
            modifier = Modifier.fillMaxWidth().height(56.dp).width(56.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(48.dp),
                    contentScale = ContentScale.Crop
                )
                Text(text = item.name)
                Spacer(Modifier.weight(1f))
                Column(
                ) {
                    Row {
                        Text(amount.toString())
                        Spacer(Modifier.width(4.dp))
                        Text(item.code)
                    }
                    Text("$ ${String.format("%.2f", totalPrice)}")
                }
            }
        }
    }
}

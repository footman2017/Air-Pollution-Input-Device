package com.example.airpollutioninputdevice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.airpollutioninputdevice.ui.theme.AirPollutionInputDeviceTheme
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.json.JSONObject

import kotlinx.coroutines.*

import kotlinx.serialization.Serializable

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

import io.ktor.client.request.*

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.ui.tooling.preview.Preview

@Serializable
data class AirData(
    val pm10: String,
    val pm25: String,
    val so2: String,
    val co: String,
    val o3: String,
    val no2: String,
)

@Serializable
data class AirQualityResponse(
    val rows: List<AirData>,
)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AirPollutionInputDeviceTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    AirPollutionInputLayout()
                }
            }
        }
    }
}

suspend fun getAirQuality(id_stasiun: String): AirQualityResponse {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    val url = "https://ispu.menlhk.go.id/apimobile/v1/getDetail/stasiun/${id_stasiun}"

//    val customer: AirQualityResponse = client.get(url).body()
//    println(AirQualityResponse)
//    return customer

    return try {
        val customer: AirQualityResponse = client.get(url).body()
        println(AirQualityResponse)
        customer
    } catch (e: Exception) {
        e.printStackTrace()
        val airData = AirData(
            pm10 = "",
            pm25 = "",
            so2 = "",
            co = "",
            o3 = "",
            no2 = ""
        )

        val airQualityResponse = AirQualityResponse(
            rows = listOf(airData)
        )
        val customer: AirQualityResponse = airQualityResponse
        customer
    }
}

data class Station(val nama: String, val id_stasiun: String)

val options = listOf(
    Station("Bandung Cihapit", "BANDUNG"),
    Station("DKI Bundaran HI", "DKI1"),
    Station("DKI Kelapa Gading", "DKI2")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirPollutionInputLayout() {
    var pm10 by remember { mutableStateOf("") }
    var pm25 by remember { mutableStateOf("") }
    var so2 by remember { mutableStateOf("") }
    var co by remember { mutableStateOf("") }
    var o3 by remember { mutableStateOf("") }
    var no2 by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isLoading2 by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("") }
    var selectedOptionId by remember { mutableStateOf("BANDUNG") }

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            InputField(
                string1 = "PM",
                string2 = "10",
                value = pm10,
                onValueChange = {
                    pm10 = it.filter { it.isDigit() }
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                ),
            )

            InputField(
                string1 = "PM",
                string2 = "25",
                value = pm25,
                onValueChange = { pm25 = it.filter { it.isDigit() } },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                ),
            )
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            InputField(
                string1 = "SO",
                string2 = "2",
                value = so2,
                onValueChange = { so2 = it.filter { it.isDigit() } },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                ),
            )

            InputField(
                string1 = "CO",
                string2 = "",
                value = co,
                onValueChange = { co = it.filter { it.isDigit() } },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                ),
            )
        }

        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            InputField(
                string1 = "O",
                string2 = "3",
                value = o3,
                onValueChange = { o3 = it.filter { it.isDigit() } },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
                ),
            )

            InputField(
                string1 = "NO",
                string2 = "2",
                value = no2,
                onValueChange = { no2 = it.filter { it.isDigit() } },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
                ),
            )
        }


        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                TextField(
                    // The `menuAnchor` modifier must be passed to the text field for correctness.
                    modifier = Modifier
                        .menuAnchor()
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth(),
                    readOnly = true,
                    value = selectedOptionText,
                    onValueChange = {},
                    label = { Text("Lokasi") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    options.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption.nama) },
                            onClick = {
                                selectedOptionText = selectionOption.nama
                                selectedOptionId = selectionOption.id_stasiun
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }

        if (isLoading2) {
            CircularProgressIndicator(modifier = Modifier)
        } else {
            Button(
                onClick = {
                    isLoading2 = true
                    if (selectedOptionText == "") {
                        selectedOptionText = options[0].nama;
                        selectedOptionId = options[0].id_stasiun;
                    }

                    coroutineScope.launch {
                        val response = getAirQuality(selectedOptionId)
                        pm10 = response.rows.first().pm10
                        pm25 = response.rows.first().pm25
                        so2 = response.rows.first().so2
                        co = response.rows.first().co
                        o3 = response.rows.first().o3
                        no2 = response.rows.first().no2
                        isLoading2 = false
                    }
                },
                enabled = true,
                modifier = Modifier
            ) {
                Text(text = "get from menlhk.go.id")
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier)
        } else {
            Button(
                onClick = {
                    isLoading = true
                    coroutineScope.launch {
                        submitData(
                            pm10.toInt(),
                            pm25.toInt(),
                            so2.toInt(),
                            co.toInt(),
                            o3.toInt(),
                            no2.toInt(),
                            selectedOptionId
                        )
                        isLoading = false
                    }
                },
                enabled = selectedOptionText.isNotEmpty() && pm10.isNotEmpty() && pm25.isNotEmpty() && so2.isNotEmpty() && co.isNotEmpty() && o3.isNotEmpty() && no2.isNotEmpty(),
                modifier = Modifier
            ) {
                Text(text = "SUBMIT")
            }
        }
    }

}

@Composable
fun InputField(
    string1: String,
    string2: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier
        .width(175.dp)
        .padding(20.dp)
) {
    TextField(
        label = {
            Text(buildAnnotatedString {
                append(string1)
                withStyle(
                    style = SpanStyle(
                        fontSize = 10.sp, baselineShift = BaselineShift.Subscript
                    )
                ) {
                    append(string2)
                }
            })
        },
        singleLine = true,
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
//        modifier = Modifier
//            .padding(bottom = 32.dp)
//            .fillMaxWidth(),
        modifier = modifier
    )
}


suspend fun submitData(
    pm10: Int, pm25: Int, so2: Int, co: Int, o3: Int, no2: Int, location: String
) {
    val data = mapOf(
        "pm10" to pm10, "pm25" to pm25, "so2" to so2, "co" to co, "o3" to o3, "no2" to no2, "location" to location
    )

    val json = JSONObject(data).toString()
    withContext(Dispatchers.IO) {
        try {
            val mqttClient = MqttClient(
                "tcp://test.mosquitto.org:1883", MqttClient.generateClientId(), null
            )
            mqttClient.connect()
            val message = MqttMessage()
            message.payload = json.toByteArray()
            mqttClient.publish("air_parameter", message)
            mqttClient.disconnect()
            println("Message successfully published!")
        } catch (e: MqttException) {
            println("Error when connecting to MQTT broker or publishing message: ${e.message}")
            println("Reason code: ${e.reasonCode}")
            println("Cause: ${e.cause}")
            println("Exception stack trace: ")
            e.printStackTrace()
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun GreetingPreview() {
//    AirPollutionInputDeviceTheme {
//        AirPollutionInputLayout()
//    }
//}

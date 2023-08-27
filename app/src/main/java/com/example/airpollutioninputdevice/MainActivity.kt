package com.example.airpollutioninputdevice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

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

    Column(
        modifier = Modifier
            .padding(40.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        inputField(
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

        inputField(
            string1 = "PM",
            string2 = "25",
            value = pm25,
            onValueChange = { pm25 = it.filter { it.isDigit() } },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ),
        )

        inputField(
            string1 = "SO",
            string2 = "2",
            value = so2,
            onValueChange = { so2 = it.filter { it.isDigit() } },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ),
        )

        inputField(
            string1 = "CO",
            string2 = "",
            value = co,
            onValueChange = { co = it.filter { it.isDigit() } },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ),
        )

        inputField(
            string1 = "O",
            string2 = "3",
            value = o3,
            onValueChange = { o3 = it.filter { it.isDigit() } },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ),
        )

        inputField(
            string1 = "NO",
            string2 = "2",
            value = no2,
            onValueChange = { no2 = it.filter { it.isDigit() } },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
            ),
        )

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        delay(1000)  // Optional: ensure loading indicator is shown for at least 300ms
                        if (pm10.isNotEmpty() && pm25.isNotEmpty() && so2.isNotEmpty() && co.isNotEmpty() && o3.isNotEmpty() && no2.isNotEmpty()) {
                            submitData(
                                pm10.toInt(),
                                pm25.toInt(),
                                so2.toInt(),
                                co.toInt(),
                                o3.toInt(),
                                no2.toInt()
                            )
                        }
                        isLoading = false
                    }
                },
                enabled = pm10.isNotEmpty() && pm25.isNotEmpty() && so2.isNotEmpty() && co.isNotEmpty() && o3.isNotEmpty() && no2.isNotEmpty()
            ) {
                Text(text = "SUBMIT")
            }
        }

        if (isLoading2) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading2 = true
                        delay(1000)  // Optional: ensure loading indicator is shown for at least 300ms
                        if (pm10.isNotEmpty() && pm25.isNotEmpty() && so2.isNotEmpty() && co.isNotEmpty() && o3.isNotEmpty() && no2.isNotEmpty()) {
                            submitData(
                                pm10.toInt(),
                                pm25.toInt(),
                                so2.toInt(),
                                co.toInt(),
                                o3.toInt(),
                                no2.toInt()
                            )
                        }
                        isLoading2 = false
                    }
                },
                enabled = pm10.isNotEmpty() && pm25.isNotEmpty() && so2.isNotEmpty() && co.isNotEmpty() && o3.isNotEmpty() && no2.isNotEmpty()
            ) {
                Text(text = "get from menlhk.go.id")
            }
        }
    }

}

@Composable
fun inputField(
    string1: String,
    string2: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions
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
        modifier = Modifier
            .padding(bottom = 32.dp)
            .fillMaxWidth(),
    )
}


private suspend fun submitData(
    pm10: Int, pm25: Int, so2: Int, co: Int, o3: Int, no2: Int
) {
    val data = mapOf(
        "pm10" to pm10, "pm25" to pm25, "so2" to so2, "co" to co, "o3" to o3, "no2" to no2
    )

    val json = JSONObject(data).toString()

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

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun GreetingPreview() {
//    AirPollutionInputDeviceTheme {
//        AirPollutionInputLayout()
//    }
//}

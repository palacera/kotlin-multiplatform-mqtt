import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import mqtt.adapter.MqttClient
import mqtt.api.MqttClientConfig
import mqtt.api.MqttConnectionState
import mqtt.api.MqttPublishState
import mqtt.api.MqttQos
import mqtt.api.MqttSubscription
import mqtt.api.MqttSubscriptionOptions
import mqtt.api.MqttVersion
import mqtt.api.asFilter
import mqtt.api.mqttTopic
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        val scope = rememberCoroutineScope()
        val topic = remember { mqttTopic("hasdfkjahsldf", "ashfkjashlfkjllask", appendSlash = true) }

        val adapter = remember {
            MqttClient(
                scope,
                MqttClientConfig(
                    mqttVersion = MqttVersion.V5,
                    address = "test.mosquitto.org",
                    port = 1883,
                )
            )
        }

        LaunchedEffect(Unit) {
            adapter.subscribe(
                MqttSubscription(
                    topicFilter = topic.asFilter(),
                    options = MqttSubscriptionOptions(qos = MqttQos.ExactlyOnce)
                )
            )
            adapter.connect()
        }

        val mqttResponse by adapter.publishStateFlow.collectAsState(MqttPublishState.Idle)
        val connectionState by adapter.connectionStateFlow.collectAsState(MqttConnectionState.Idle)

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            when (connectionState) {
                is MqttConnectionState.Connecting -> {
                    Text(connectionState.toString())
                }

                is MqttConnectionState.Connected -> {
                    Text(connectionState.toString())
                    Button(
                        enabled = connectionState is MqttConnectionState.Connected,
                        onClick = {
                            scope.launch {
                                adapter.disconnect()
                            }
                        }) {
                        Text("Disconnect")
                    }
                }

                is MqttConnectionState.Disconnecting -> {
                    Text(connectionState.toString())
                }

                is MqttConnectionState.Disconnected -> {
                    Text(connectionState.toString())
                    Button(
                        enabled = connectionState is MqttConnectionState.Disconnected,
                        onClick = {
                            scope.launch {
                                adapter.connect()
                            }
                        }) {
                        Text("Connect")
                    }
                }

                is MqttConnectionState.Error -> {
                    Text(connectionState.toString())
                }

                else -> Unit
            }
            when (mqttResponse) {
                is MqttPublishState.Publishing -> {
                    Text(mqttResponse.toString())
                }

                is MqttPublishState.Published -> {
                    Text(mqttResponse.toString())
                }

                is MqttPublishState.Error -> {
                    Text(mqttResponse.toString())
                }

                else -> Unit
            }
            Button(
                enabled = connectionState is MqttConnectionState.Connected,
                onClick = {
                    scope.launch {
                        adapter.publish(
                            topic = topic,
                            qos = MqttQos.AtLeastOnce,
                            retain = false,
                            payload = null
                        )
                    }
                }) {
                Text("Click me!")
            }
        }
    }
}

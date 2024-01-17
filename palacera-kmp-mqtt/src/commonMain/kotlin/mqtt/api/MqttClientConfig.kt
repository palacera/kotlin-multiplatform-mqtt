package mqtt.api

data class MqttClientConfig(
    val mqttVersion: MqttVersion,
    val address: String,
    val port: Int,
    //val tls: TLSClientSettings?,
    val keepAlive: Int = 60,
    val useWebSocket: Boolean = false,
    val cleanStart: Boolean = true,
    //var clientId: String? = null,
    //val userName: String? = null,
    //val password: UByteArray? = null,
    //val properties: MQTT5Properties = MQTT5Properties(),
    val will: MqttWillConfig? = null,
)

@OptIn(ExperimentalUnsignedTypes::class)
data class MqttWillConfig(
    val topic: String,
    val payload: UByteArray? = null,
    val retain: Boolean = false,
    val qos: MqttQos = MqttQos.Default,
    //val properties: MQTT5Properties = MQTT5Properties(),
)

package mqtt.api

data class MqttMessage<out T>(
    val payload: T
)

package mqtt.api

sealed class MqttSubscribeState<T> {
    data object Idle : MqttSubscribeState<Nothing>()
    data class Subscribing<T>(val message: T) : MqttSubscribeState<T>()
    data class Subscribed<T>(val message: T) : MqttSubscribeState<T>()
    data class Error<T>(val message: T) : MqttSubscribeState<T>()
}

package mqtt.api

sealed class MqttPublishState<out T> {
    data object Idle : MqttPublishState<Nothing>()
    data class Publishing<T>(val message: T) : MqttPublishState<T>()
    data class Published<T>(val message: T) : MqttPublishState<T>()
    data class Error<T>(val message: T, val throwable: Throwable) : MqttPublishState<T>()
}

package mqtt.api

sealed class MqttConnectionState {
    data object Idle : MqttConnectionState()
    data object Connecting : MqttConnectionState()
    data object Connected: MqttConnectionState()
    data object Disconnecting : MqttConnectionState()
    data object Disconnected: MqttConnectionState()
    data class Error(val throwable: Throwable): MqttConnectionState()
}

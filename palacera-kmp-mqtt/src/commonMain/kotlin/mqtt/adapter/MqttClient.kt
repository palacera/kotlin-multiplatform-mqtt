package mqtt.adapter

import MQTTClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import mqtt.Subscription
import mqtt.api.MqttClientConfig
import mqtt.api.MqttConnectionState
import mqtt.api.MqttMessage
import mqtt.api.MqttPublishState
import mqtt.api.MqttQos
import mqtt.api.MqttSubscribeState
import mqtt.api.MqttSubscription
import mqtt.api.MqttTopic
import mqtt.packets.mqttv5.ReasonCode

class MqttClient(
    private val scope: CoroutineScope,
    private val config: MqttClientConfig,
) {
    private val _connectionStateFlow: MutableStateFlow<MqttConnectionState> = MutableStateFlow(MqttConnectionState.Idle)
    val connectionStateFlow: StateFlow<MqttConnectionState> = _connectionStateFlow.asStateFlow()

    private suspend fun MqttConnectionState.emit() = _connectionStateFlow.emit(this@emit)

    //private val _subscribeStateFlow: MutableStateFlow<MqttSubscribeState<String>> = MutableStateFlow(MqttSubscribeState.Idle)
    //val subscribeStateFlow: StateFlow<MqttSubscribeState<String>> = _subscribeStateFlow.asStateFlow()

    private val _publishStateFlow: MutableStateFlow<MqttPublishState<MqttMessage<String>>> = MutableStateFlow(MqttPublishState.Idle)
    val publishStateFlow: StateFlow<MqttPublishState<MqttMessage<String>>> = _publishStateFlow.asStateFlow()

    private suspend fun MqttPublishState<MqttMessage<String>>.emit() = _publishStateFlow.emit(this@emit)


    private var mqttClient: MQTTClient = getClient()

    private var subscriptions: List<MqttSubscription> = emptyList()

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun getClient(): MQTTClient {
        return config.resolve() {
            scope.launch(PlatformDispatcher.io) {
                MqttPublishState.Published(MqttMessage(it.payload.toString())).emit()
            }
            println(it.payload?.toByteArray()?.decodeToString())
        }
    }

    suspend fun subscribe(subscription: MqttSubscription) = subscribe(listOf(subscription))

    suspend fun subscribe(subscriptions: List<MqttSubscription>) = withContext(PlatformDispatcher.io) {
        //_subscribeStateFlow.emit(MqttSubscribeState.Subscribing("hello"))

        // setting the subscriptions here so able to resubscribe on reconnect, see checkForReconnect()
        this@MqttClient.subscriptions = subscriptions

        mqttClient.subscribe(subscriptions.resolve())
    }

    suspend fun unsubscribe(topics: MqttTopic) = unsubscribe(listOf(topics))

    suspend fun unsubscribe(topics: List<MqttTopic>) = withContext(PlatformDispatcher.io) {
        mqttClient.unsubscribe(topics.resolve())
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    suspend fun <T> publish(
        topic: MqttTopic,
        qos: MqttQos,
        retain: Boolean,
        payload: T? = null,
    ) = withContext(PlatformDispatcher.io) {

        val uByteArrayOrNull: UByteArray? = when (payload) {
            is String -> {
                payload.encodeToByteArray().toUByteArray()
            }
            is ByteArray -> {
                payload.toUByteArray()
            }
            is UByteArray -> {
                payload
            }
            else -> {
                null
            }
        }

        MqttPublishState.Publishing(MqttMessage(uByteArrayOrNull.toString())).emit()
        mqttClient.publish(retain, qos.resolve(), topic.name, uByteArrayOrNull)
    }

    suspend fun connect() = withContext(PlatformDispatcher.io) {
        launch { checkConnection() }

        // have to check for reconnect currently because kmqtt requires a new client instance after disconnect
        checkForReconnect()

        mqttClient.run()
    }

    suspend fun disconnect() = withContext(PlatformDispatcher.io) {
        launch { checkDisconnection() }

        val reasonCode = if (config.will == null) ReasonCode.SUCCESS else ReasonCode.DISCONNECT_WITH_WILL_MESSAGE
        mqttClient.disconnect(reasonCode)
    }

    // only exists because kmqtt currently requires a new client instance after disconnect
    private suspend fun checkForReconnect() = withContext(PlatformDispatcher.io) {
        if (!mqttClient.running) {
            mqttClient = getClient()
            mqttClient.subscribe(subscriptions.resolve())
        }
    }

    private suspend fun checkConnection() = withContext(PlatformDispatcher.io) {
        MqttConnectionState.Connecting.emit()

        try {
            withTimeout(5000) {

                while (isActive && !mqttClient.connackReceived) {
                    delay(100)
                }

                MqttConnectionState.Connected.emit()
                cancel()
            }
        } catch (e: TimeoutCancellationException) {
            MqttConnectionState.Error(e).emit()
        }
    }

    private suspend fun checkDisconnection() = withContext(PlatformDispatcher.io) {
        MqttConnectionState.Disconnecting.emit()

        try {
            withTimeout(5000) {

                while (isActive && mqttClient.connackReceived) {
                    delay(100)
                }

                MqttConnectionState.Disconnected.emit()
                cancel()
            }
        } catch (e: TimeoutCancellationException) {
            MqttConnectionState.Error(e).emit()
        }
    }
}

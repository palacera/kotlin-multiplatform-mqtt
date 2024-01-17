package mqtt.adapter

import MQTTClient
import mqtt.api.MqttClientConfig
import mqtt.packets.mqtt.MQTTPublish

@OptIn(ExperimentalUnsignedTypes::class)
internal fun MqttClientConfig.resolve(publishReceived: (publish: MQTTPublish) -> Unit) = MQTTClient(
    mqttVersion = mqttVersion.resolve(),
    address = address,
    port = port,
    tls = null,
    keepAlive = keepAlive,
    webSocket = useWebSocket,
    cleanStart = cleanStart,
    willProperties = null,
    willTopic = will?.topic,
    willPayload = will?.payload,
    willRetain = will?.retain ?: false,
    willQos = will?.qos.resolve(),
    publishReceived = publishReceived
)

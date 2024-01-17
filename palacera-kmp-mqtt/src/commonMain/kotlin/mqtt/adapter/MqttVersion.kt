package mqtt.adapter

import mqtt.MQTTVersion
import mqtt.api.MqttVersion

internal fun MqttVersion.resolve() = when (this) {
    MqttVersion.V3_1_1 -> MQTTVersion.MQTT3_1_1
    MqttVersion.V5 -> MQTTVersion.MQTT5
}

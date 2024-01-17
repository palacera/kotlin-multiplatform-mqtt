package mqtt.adapter

import mqtt.api.MqttQos
import mqtt.packets.Qos

internal fun MqttQos?.resolve() = when (this) {
    MqttQos.AtLeastOnce -> Qos.AT_LEAST_ONCE
    MqttQos.ExactlyOnce -> Qos.EXACTLY_ONCE
    else -> Qos.AT_MOST_ONCE
}

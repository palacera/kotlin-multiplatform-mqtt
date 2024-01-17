package mqtt.adapter

import mqtt.api.MqttTopic

internal fun List<MqttTopic>.resolve(): List<String> = map { it.resolve() }

internal fun MqttTopic.resolve(): String = name

package mqtt.api

import kotlin.jvm.JvmInline

const val MQTT_WILDCARD: String = "+"

@JvmInline
value class MqttTopicFilter(val name: String) {
    init {
        require(name.isNotEmpty()) { "Topic filter must not be empty" }
        require(name.length <= 65535) { "Topic filter must not be longer than 65535 characters" }
    }
}

fun mqttTopicFilter(vararg segments: String, appendMultiLevelWildcard: Boolean = false): MqttTopicFilter =
    MqttTopicFilter(
        segments.joinToString(separator = "/").let {
            if (appendMultiLevelWildcard) "$it/#" else it
        }
    )

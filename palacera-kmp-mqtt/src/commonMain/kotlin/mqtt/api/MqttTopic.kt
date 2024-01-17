package mqtt.api

import kotlin.jvm.JvmInline

@JvmInline
value class MqttTopic(val name: String) {
    init {
        require(name.isNotEmpty()) { "Topic name must not be empty" }
        require(name.length <= 65535) { "Topic name must not be longer than 65535 characters" }
        require(name.all { it != '#' && it != '+' }) { "Topic name must not contain wildcards" }
    }
}

fun mqttTopic(vararg segments: String, appendSlash: Boolean = false): MqttTopic =
    MqttTopic(
        segments.joinToString(separator = "/").let {
            if (appendSlash) "$it/" else it
        }
    )

fun MqttTopic.asFilter(): MqttTopicFilter = MqttTopicFilter(name)

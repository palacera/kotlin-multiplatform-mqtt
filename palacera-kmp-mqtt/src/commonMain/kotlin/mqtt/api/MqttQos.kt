package mqtt.api

enum class MqttQos(val value: Int) {
    AtMostOnce(0),
    AtLeastOnce(1),
    ExactlyOnce(2);

    companion object {
        val Default = AtMostOnce
    }
}

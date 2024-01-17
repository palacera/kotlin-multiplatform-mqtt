package mqtt.api

data class MqttSubscription(
    val topicFilter: MqttTopicFilter,
    val options: MqttSubscriptionOptions = MqttSubscriptionOptions(),
    //val subscriptionIdentifier: UInt? = null
)

data class MqttSubscriptionOptions(
    val qos: MqttQos = MqttQos.AtMostOnce,
    //val noLocal: Boolean = false,
    //val retainedAsPublished: Boolean = false,
    //val retainHandling: UInt = 0u
)

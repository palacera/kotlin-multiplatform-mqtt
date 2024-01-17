package mqtt.adapter

import mqtt.api.MqttSubscription
import mqtt.Subscription
import mqtt.packets.mqttv5.SubscriptionOptions

internal fun List<MqttSubscription>.resolve(): List<Subscription> = map { it.resolve() }

internal fun MqttSubscription.resolve(): Subscription {
    return Subscription(
        topicFilter = topicFilter.name,
        options = SubscriptionOptions(
            qos = options.qos.resolve(),
            //noLocal = options.noLocal,
            //retainedAsPublished = options.retainedAsPublished,
            //retainHandling = options.retainHandling
        ),
    )
}

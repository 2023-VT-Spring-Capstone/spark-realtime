from kafka import KafkaConsumer, KafkaProducer
import json
from sentiment_analysis import sentiment_analysis

bootstrap_servers = ['localhost:9092', 'localhost:9093', 'localhost:9094']
input_topic = 'DWD_TOP_LOG'
output_topic = 'DWD_ANALYZED_LOG'
consumer_group = 'my_consumer_group'

# Create Kafka consumer and producer
consumer = KafkaConsumer(
    input_topic,
    bootstrap_servers=bootstrap_servers,
    group_id=consumer_group,
    auto_offset_reset='earliest',  # Start reading from the beginning of the topic
    # enable_auto_commit=True,  # Enable automatic offset commit
    # Decode message values as UTF-8 strings
    value_deserializer=lambda m: m.decode('utf-8'),
)

consumer.subscribe([input_topic])

producer = KafkaProducer(bootstrap_servers=bootstrap_servers,
                         value_serializer=lambda m: json.dumps(m).encode('utf-8'))


def main():
    for message in consumer:
        message_value = message.value
        print(message_value)
        sentiment_analysis_result = sentiment_analysis(
            json.loads(message_value))
        print(sentiment_analysis_result)
        if sentiment_analysis_result:
            producer.send(output_topic, value=sentiment_analysis_result)


if __name__ == "__main__":
    main()

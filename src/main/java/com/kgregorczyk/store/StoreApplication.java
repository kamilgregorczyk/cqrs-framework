package com.kgregorczyk.store;

import com.kgregorczyk.store.cqrs.command.DomainCommand;
import com.kgregorczyk.store.cqrs.command.DomainCommandPublisher;
import com.kgregorczyk.store.cqrs.event.DomainEvent;
import com.kgregorczyk.store.cqrs.event.DomainEventPublisher;
import com.kgregorczyk.store.cqrs.event.DomainEventRepository;
import com.kgregorczyk.store.cqrs.kafka.*;
import com.kgregorczyk.store.product.aggregate.ProductAggregate;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootApplication
@Import(value = KafkaCQRSConfiguration.class)
public class StoreApplication {

  public static void main(String[] args) {
    SpringApplication.run(StoreApplication.class, args);
  }

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  /** Events */
  @Bean(ProductAggregate.EVENT_TOPIC)
  NewTopic productEventTopic() {
    return new NewTopic(ProductAggregate.EVENT_TOPIC, 5, (short) 1);
  }

  @Bean
  ProducerFactory<UUID, DomainEvent<ProductAggregate>> domainEventProducerFactory() {
    return new DefaultKafkaProducerFactory<>(producerConfig());
  }

  @Bean
  KafkaTemplate<UUID, DomainEvent<ProductAggregate>> domainEventKafkaTemplate() {
    return new KafkaTemplate<>(domainEventProducerFactory());
  }

  @Bean
  DomainEventPublisher<ProductAggregate> domainEventPublisher(
      KafkaTemplate<UUID, DomainEvent<ProductAggregate>> kafkaTemplate) {
    return new KafkaDomainEventPublisher<>(kafkaTemplate);
  }

  /** Commands */
  @Bean(ProductAggregate.COMMAND_TOPIC)
  NewTopic productCommandTopic() {
    return new NewTopic(ProductAggregate.COMMAND_TOPIC, 5, (short) 1);
  }

  @Bean
  ProducerFactory<UUID, DomainCommand<ProductAggregate>> domainCommandProducerFactory() {
    return new DefaultKafkaProducerFactory<>(producerConfig());
  }

  @Bean
  KafkaTemplate<UUID, DomainCommand<ProductAggregate>> domainCommandKafkaTemplate() {
    return new KafkaTemplate<>(domainCommandProducerFactory());
  }

  @Bean
  DomainCommandPublisher<ProductAggregate> domainCommandPublisher(
      KafkaTemplate<UUID, DomainCommand<ProductAggregate>> kafkaTemplate) {
    return new KafkaDomainCommandPublisher<>(kafkaTemplate);
  }

  @Bean
  public ConsumerFactory<UUID, DomainCommand<ProductAggregate>> domainCommandConsumerFactory() {
    return new DefaultKafkaConsumerFactory<>(
        consumerConfigs(), new UUIDDeserializer(), new TrustedJsonDeserializer<>());
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<UUID, DomainCommand<ProductAggregate>>
      kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<UUID, DomainCommand<ProductAggregate>> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(domainCommandConsumerFactory());
    return factory;
  }

  // Stream
  @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
  StreamsConfig productStreamsConfig() {
    Map<String, Object> config = new HashMap<>();
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "product-service");
    return new StreamsConfig(config);
  }

  @Bean
  KTable<UUID, ProductAggregate> productAggregateKTable(StreamsBuilder streamsBuilder) {
    var keySerde = Serdes.serdeFrom(new UUIDSerializer(), new UUIDDeserializer());
    var valueSerde =
        new JsonSerde<DomainEvent<ProductAggregate>>(
            new TrustedJsonSerializer<>(), new TrustedJsonDeserializer<>());
    var aggregateSerde =
        new JsonSerde<ProductAggregate>(
            new TrustedJsonSerializer<>(), new TrustedJsonDeserializer<>());
    return streamsBuilder.stream(ProductAggregate.EVENT_TOPIC, Consumed.with(keySerde, valueSerde))
        .groupBy((uuid, event) -> uuid)
        .aggregate(
            ProductAggregate::new,
            (uuid, event, aggregate) -> aggregate.applyEvent(event),
            Materialized.<UUID, ProductAggregate, KeyValueStore<Bytes, byte[]>>as(
                    ProductAggregate.EVENT_SNAPSHOT)
                .withKeySerde(keySerde)
                .withValueSerde(aggregateSerde));
  }

  @Bean
  ReadOnlyKeyValueStore<UUID, ProductAggregate> productAggregateReadOnlyKeyValueStore(QueryableStoreRegistry queryableStoreRegistry){

  }

  @Bean
  DomainEventRepository<ProductAggregate> productAggregateDomainEventRepository(
          DomainEventPublisher<ProductAggregate> eventPublisher,
          ReadOnlyKeyValueStore<UUID, ProductAggregate> keyValueStore) {
    return new KafkaDomainEventRepository<>(eventPublisher, keyValueStore);
  }

  private Map<String, Object> producerConfig() {
    Map<String, Object> config = new HashMap<>();
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, TrustedJsonSerializer.class);
    return config;
  }

  public Map<String, Object> consumerConfigs() {

    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TrustedJsonDeserializer.class);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "product-service");

    return props;
  }
}

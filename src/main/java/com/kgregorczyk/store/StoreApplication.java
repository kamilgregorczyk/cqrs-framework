package com.kgregorczyk.store;

import static java.util.UUID.randomUUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kgregorczyk.store.cqrs.command.DomainCommand;
import com.kgregorczyk.store.cqrs.command.DomainCommandPublisher;
import com.kgregorczyk.store.cqrs.event.DomainEvent;
import com.kgregorczyk.store.cqrs.event.DomainEventPublisher;
import com.kgregorczyk.store.cqrs.kafka.KafkaDomainCommandPublisher;
import com.kgregorczyk.store.cqrs.kafka.KafkaDomainEventPublisher;
import com.kgregorczyk.store.cqrs.kafka.TrustedJsonDeserializer;
import com.kgregorczyk.store.cqrs.kafka.TrustedJsonSerializer;
import com.kgregorczyk.store.cqrs.kafka.UUIDDeserializer;
import com.kgregorczyk.store.cqrs.kafka.UUIDSerializer;
import com.kgregorczyk.store.product.aggregate.ProductAggregate;
import com.kgregorczyk.store.product.event.ProductCreatedEvent;
import com.kgregorczyk.store.product.event.ProductNameUpdatedEvent;
import com.kgregorczyk.store.product.event.ProductPriceUpdatedEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@EnableKafka
@SpringBootApplication
public class StoreApplication {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  public static void main(String[] args) {
    SpringApplication.run(StoreApplication.class, args);
  }

  /** Events */
  @Bean(ProductAggregate.EVENT_TOPIC)
  NewTopic productEventTopic() {
    return new NewTopic(ProductAggregate.EVENT_TOPIC, 3, (short) 1);
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
    return new NewTopic(ProductAggregate.COMMAND_TOPIC, 3, (short) 1);
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
  public ConsumerFactory<UUID, DomainCommand<ProductAggregate>> kafkaListenerContainerFactory(
      TrustedJsonDeserializer<DomainCommand<ProductAggregate>> jsonDeserializer) {
    return new DefaultKafkaConsumerFactory<>(
        commandConsumerConfigs(), new UUIDDeserializer(), jsonDeserializer);
  }

  @Bean("domainCommandContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<UUID, DomainCommand<ProductAggregate>>
      domainCommandContainerFactory(
          ConsumerFactory<UUID, DomainCommand<ProductAggregate>> consumerFactory) {
    ConcurrentKafkaListenerContainerFactory<UUID, DomainCommand<ProductAggregate>> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConcurrency(5);
    factory.setConsumerFactory(consumerFactory);
    return factory;
  }

  @Bean
  @ConditionalOnMissingBean(name = "domainEventContainerFactory")
  public ConsumerFactory<UUID, DomainEvent<ProductAggregate>> domainEventConsumerFactory(
      TrustedJsonDeserializer<DomainEvent<ProductAggregate>> jsonDeserializer) {
    return new DefaultKafkaConsumerFactory<>(
        eventConsumerConfigs(), new UUIDDeserializer(), jsonDeserializer);
  }

  @Bean("domainEventContainerFactory")
  public ConcurrentKafkaListenerContainerFactory<UUID, DomainEvent<ProductAggregate>>
      domainEventContainerFactory(
          ConsumerFactory<UUID, DomainEvent<ProductAggregate>> consumerFactory) {
    ConcurrentKafkaListenerContainerFactory<UUID, DomainEvent<ProductAggregate>> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConcurrency(5);
    factory.setConsumerFactory(consumerFactory);
    return factory;
  }

  @Bean
  RedissonClient redissonClient(){
    return Redisson.create();
  }

  private Map<String, Object> producerConfig() {
    Map<String, Object> config = new HashMap<>();
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, UUIDSerializer.class);
    config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, TrustedJsonSerializer.class);
    return config;
  }

  private Map<String, Object> commandConsumerConfigs() {

    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TrustedJsonDeserializer.class);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "product-service");

    return props;
  }

  private Map<String, Object> eventConsumerConfigs() {

    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, UUIDDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TrustedJsonDeserializer.class);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "product-service");

    return props;
  }

  @Bean
  public ObjectMapper objectMapper() {
    var mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.registerModule(new Jdk8Module());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return mapper;
  }

  @Bean()
  public Map<String, Class> eventTypeToClass() {
    return Map.of(
        "PRODUCT_CREATED_EVENT",
        ProductCreatedEvent.class,
        "PRODUCT_PRICE_UPDATED_EVENT",
        ProductPriceUpdatedEvent.class,
        "PRODUCT_NAME_UPDATED_EVENT",
        ProductNameUpdatedEvent.class);
  }
}

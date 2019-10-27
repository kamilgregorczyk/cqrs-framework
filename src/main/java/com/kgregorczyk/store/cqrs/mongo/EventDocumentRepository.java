package com.kgregorczyk.store.cqrs.mongo;

import java.util.stream.Stream;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventDocumentRepository extends MongoRepository<EventDocument, String> {

  Stream<EventDocument> streamAllByAggregateIdOrderByCreatedAt(String id);

  Stream<EventDocument> streamAllByAggregateType(String eventType);
}

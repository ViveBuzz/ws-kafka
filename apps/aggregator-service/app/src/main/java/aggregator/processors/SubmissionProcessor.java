package aggregator.processors;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import aggregator.configs.KafkaProperties;
import aggregator.models.EnrichedSubmission;
import aggregator.models.Submission;
import aggregator.models.User;
import aggregator.serdes.SubmissionSerializer;
import aggregator.serdes.UserDeserializer;
import aggregator.serdes.UserSerializer;
import aggregator.serdes.EnrichedSubmissionDeserializer;
import aggregator.serdes.EnrichedSubmissionSerializer;
import aggregator.serdes.SubmissionDeserializer;

public class SubmissionProcessor {
    private final KafkaProperties kafkaProperties;
    private KafkaStreams streams;

    private Serde<User> userSerde = Serdes.serdeFrom(new UserSerializer(), new UserDeserializer());
    private Serde<Submission> submissionSerde = Serdes.serdeFrom(new SubmissionSerializer(),
            new SubmissionDeserializer());
    private Serde<EnrichedSubmission> enrichedSubmissionSerde = Serdes.serdeFrom(new EnrichedSubmissionSerializer(),
            new EnrichedSubmissionDeserializer());

    public SubmissionProcessor(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    public void buildSubmissionStream(StreamsBuilder builder) {
        // Create streams and tables
        KStream<String, Submission> submissionStream = builder.stream(
                kafkaProperties.getSubmissionTopic(),
                Consumed.with(Serdes.String(), submissionSerde)).selectKey((key, submission) -> submission.getUserId());
        KTable<String, User> userTable = builder.table(
                kafkaProperties.getUserTopic(),
                        Consumed.with(Serdes.String(), userSerde),
                Materialized.<String, User, KeyValueStore<Bytes, byte[]>>as("user-store")
                        .withKeySerde(Serdes.String())
                        .withValueSerde(userSerde));

        // Perform join
        KStream<String, EnrichedSubmission> enrichedSubmissionStream = submissionStream.leftJoin(
                userTable,
                (submission, user) -> {
                            var enrichedSubmission = new EnrichedSubmission();
                    enrichedSubmission.setSubmission(submission);
                    enrichedSubmission.setUser(user);
                    return enrichedSubmission;
                },
                Joined.with(Serdes.String(), submissionSerde, userSerde));
        // Create a state store to store enriched submissions
        enrichedSubmissionStream.toTable(
                Materialized.<String, EnrichedSubmission, KeyValueStore<Bytes, byte[]>>as("enriched-submission-store")
                        .withKeySerde(Serdes.String())
                        .withValueSerde(enrichedSubmissionSerde));

        // Debugging
        submissionStream
                .peek((key, value) -> System.out.println("Submission key: " + key + " value: " + value));
        enrichedSubmissionStream
                .peek((key, value) -> System.out.println("Enriched submission key: " + key + " value: " + value));

        // Write to output topic
        enrichedSubmissionStream.to(
                kafkaProperties.getOutputTopic(),
                Produced.with(Serdes.String(), enrichedSubmissionSerde));

        // PART 2: Process user updates
        KStream<String, User> userUpdatedStream = builder.stream(
                kafkaProperties.getUserUpdatedTopic(),
                Consumed.with(Serdes.String(), userSerde));

        userUpdatedStream.mapValues(value -> {
            // Ensure KafkaStreams instance is set
            if (streams == null) {
                throw new IllegalStateException("KafkaStreams instance is not set.");
            }

            // Access the enriched submission store
            ReadOnlyKeyValueStore<String, EnrichedSubmission> enrichedSubmissionStore = streams.store(
                    StoreQueryParameters.fromNameAndType("enriched-submission-store",
                            QueryableStoreTypes.keyValueStore()));

            // Fetch the existing enriched submission for the user
            EnrichedSubmission existingEnrichedSubmission = enrichedSubmissionStore.get(value.getId());

            if (existingEnrichedSubmission == null) {
                return null;
            }

            existingEnrichedSubmission.setUser(value);
            return existingEnrichedSubmission;
        })
                .filter((key, value) -> value != null)
                .peek((key, value) -> System.out.println("Updated Enriched Submission: " + value))
                .to(kafkaProperties.getOutputTopic(),
                        Produced.with(Serdes.String(), enrichedSubmissionSerde));
    }

    public void setStreams(KafkaStreams streams) {
        this.streams = streams;
    }
}

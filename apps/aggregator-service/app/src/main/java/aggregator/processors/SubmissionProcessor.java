package aggregator.processors;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;

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

    public SubmissionProcessor(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    public void buildSubmissionStream(StreamsBuilder builder) {
        // Create serdes
        Serde<Submission> submissionSerde = Serdes.serdeFrom(new SubmissionSerializer(), new SubmissionDeserializer());
        Serde<User> userSerde = Serdes.serdeFrom(new UserSerializer(), new UserDeserializer());
        Serde<EnrichedSubmission> enrichedSubmissionSerde = Serdes.serdeFrom(new EnrichedSubmissionSerializer(),
                new EnrichedSubmissionDeserializer());

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
                        (submission, user) -> new EnrichedSubmission(submission, user),
                Joined.with(Serdes.String(), submissionSerde, userSerde));

        // Debugging
        submissionStream
                .peek((key, value) -> System.out.println("Submission key: " + key + " value: " + value));
        enrichedSubmissionStream
                .peek((key, value) -> System.out.println("Enriched submission key: " + key + " value: " + value));

        // Write to output topic
        enrichedSubmissionStream.to(
                kafkaProperties.getOutputTopic(),
                Produced.with(Serdes.String(), enrichedSubmissionSerde));
    }
}

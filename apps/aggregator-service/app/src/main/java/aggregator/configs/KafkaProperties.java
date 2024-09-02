package aggregator.configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KafkaProperties {

    private Properties properties;

    public KafkaProperties() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("kafka.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find kafka.properties");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Accessor methods for specific Kafka configuration properties

    public String getBootstrapServers() {
        return properties.getProperty("kafka.bootstrap.servers", "localhost:29092");
    }

    public String getSchemaRegistryUrl() {
        return properties.getProperty("kafka.schema.registry.url", "http://localhost:8081");
    }

    public String getApplicationId() {
        return properties.getProperty("kafka.application.id", "default-app-id");
    }

    public String getUserTopic() {
        return properties.getProperty("kafka.topic.user", "user_topic");
    }

    public String getUserUpdatedTopic() {
        return properties.getProperty("kafka.topic.user.updated", "user_updated_topic");
    }

    public String getMembershipTopic() {
        return properties.getProperty("kafka.topic.membership", "membership_topic");
    }

    public String getSubmissionTopic() {
        return properties.getProperty("kafka.topic.submission", "submission_topic");
    }

    public String getOutputTopic() {
        return properties.getProperty("kafka.topic.output", "enriched_submission_topic");
    }

    public String getStateDir() {
        return properties.getProperty("kafka.state.dir", "/tmp/kafka-streams");
    }

    public String getSecurityProtocol() {
        return properties.getProperty("kafka.security.protocol", "PLAINTEXT");
    }

    public String getSaslMechanism() {
        return properties.getProperty("kafka.sasl.mechanism", "PLAIN");
    }

    // Add more accessor methods as needed for other properties

    public Properties getKafkaStreamsProperties() {
        Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", getBootstrapServers());
        kafkaProps.put("application.id", getApplicationId());
        kafkaProps.put("state.dir", getStateDir());
        kafkaProps.put("security.protocol", getSecurityProtocol());
        kafkaProps.put("sasl.mechanism", getSaslMechanism());
        // Include other necessary Kafka properties here
        return kafkaProps;
    }
}
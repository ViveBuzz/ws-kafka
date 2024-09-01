package aggregator.serdes;

import java.util.Map;
import org.apache.kafka.common.serialization.Deserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import aggregator.models.Submission;

public class SubmissionDeserializer implements Deserializer<Submission> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    // Configuration if needed
  }

  @Override
  public Submission deserialize(String topic, byte[] data) {
    try {
      if (data == null || data.length == 0) {
        return null;
      }
      return objectMapper.readValue(data, Submission.class);
    } catch (Exception e) {
      throw new RuntimeException("Error deserializing Submission object", e);
    }
  }

  @Override
  public void close() {
  }
}
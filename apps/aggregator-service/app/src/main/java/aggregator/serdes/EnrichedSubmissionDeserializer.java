package aggregator.serdes;

import java.util.Map;
import org.apache.kafka.common.serialization.Deserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import aggregator.models.EnrichedSubmission;

public class EnrichedSubmissionDeserializer implements Deserializer<EnrichedSubmission> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    // Configuration if needed
  }

  @Override
  public EnrichedSubmission deserialize(String topic, byte[] data) {
    try {
      if (data == null || data.length == 0) {
        return null;
      }
      return objectMapper.readValue(data, EnrichedSubmission.class);
    } catch (Exception e) {
      throw new RuntimeException("Error deserializing EnrichedSubmission object", e);
    }
  }

  @Override
  public void close() {
  }
}
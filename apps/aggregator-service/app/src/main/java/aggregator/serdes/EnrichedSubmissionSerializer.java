package aggregator.serdes;

import java.util.Map;
import org.apache.kafka.common.serialization.Serializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import aggregator.models.EnrichedSubmission;

public class EnrichedSubmissionSerializer implements Serializer<EnrichedSubmission> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    // Configuration if needed
  }

  @Override
  public byte[] serialize(String topic, EnrichedSubmission data) {
    try {
      if (data == null) {
        return null;
      }
      return objectMapper.writeValueAsBytes(data);
    } catch (Exception e) {
      throw new RuntimeException("Error serializing EnrichedSubmission object", e);
    }
  }

  @Override
  public void close() {
  }

}

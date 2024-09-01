package aggregator.serdes;

import java.util.Map;
import org.apache.kafka.common.serialization.Serializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import aggregator.models.Submission;

public class SubmissionSerializer implements Serializer<Submission> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    // Configuration if needed
  }

  @Override
  public byte[] serialize(String topic, Submission data) {
    try {
      if (data == null) {
        return null;
      }
      return objectMapper.writeValueAsBytes(data);
    } catch (Exception e) {
      throw new RuntimeException("Error serializing Submission object", e);
    }
  }

  @Override
  public void close() {
  }

}

package aggregator.serdes;

import java.util.Map;
import org.apache.kafka.common.serialization.Deserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import aggregator.models.User;

public class UserDeserializer implements Deserializer<User> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    // Configuration if needed
  }

  @Override
  public User deserialize(String topic, byte[] data) {
    try {
      if (data == null || data.length == 0) {
        return null;
      }
      return objectMapper.readValue(data, User.class);
    } catch (Exception e) {
      throw new RuntimeException("Error deserializing User object", e);
    }
  }

  @Override
  public void close() {
  }
}
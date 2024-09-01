package aggregator.serdes;

import java.util.Map;
import org.apache.kafka.common.serialization.Serializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import aggregator.models.User;

public class UserSerializer implements Serializer<User> {

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {
    // Configuration if needed
  }

  @Override
  public byte[] serialize(String topic, User data) {
    try {
      if (data == null) {
        return null;
      }
      return objectMapper.writeValueAsBytes(data);
    } catch (Exception e) {
      throw new RuntimeException("Error serializing User object", e);
    }
  }

  @Override
  public void close() {
  }

}

package aggregator.models;

public class Submission {
  private String id;
  private String userId;
  private String date;
  private String status;

  public Submission() {
  }

  public Submission(String id, String userId, String date, String status) {
    this.id = id;
    this.userId = userId;
    this.date = date;
    this.status = status;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return "Submission{" +
        "id='" + id + '\'' +
        ", userId='" + userId + '\'' +
        ", date=" + date +
        ", status='" + status + '\'' +
        '}';
  }
}

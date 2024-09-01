package aggregator.models;

public class EnrichedSubmission extends Submission {
  private User user;

  public EnrichedSubmission() {
  }

  public EnrichedSubmission(Submission submission, User user) {
    super(submission.getId(), submission.getUserId(), submission.getDate(), submission.getStatus());
    this.user = user;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public String toString() {
    return "EnrichedSubmission{" +
        "id='" + getId() + '\'' +
        ", userId='" + getUserId() + '\'' +
        ", date=" + getDate() +
        ", status='" + getStatus() + '\'' +
        ", user=" + user +
        '}';
  }

}

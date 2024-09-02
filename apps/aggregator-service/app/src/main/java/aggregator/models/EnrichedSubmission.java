package aggregator.models;

public class EnrichedSubmission extends Submission {
  private User user;

  public EnrichedSubmission() {
  }

  public User getUser() {
    return user;
  }

  public void setSubmission(Submission submission) {
    setId(submission.getId());
    setUserId(submission.getUserId());
    setDate(submission.getDate());
    setStatus(submission.getStatus());
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

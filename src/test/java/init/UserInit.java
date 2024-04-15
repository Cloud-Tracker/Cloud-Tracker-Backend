package init;

import com.example.cloud_tracker.model.User;

public class UserInit {
  public static User createUser() {
    return new User(1, "test@test.com", "test", "test", null, null);
  }
}

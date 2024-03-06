package init;

import com.example.cloud_tracker.data.User;
import org.springframework.security.core.userdetails.UserDetails;

public class UserInit {
    public static User createUser() {
        return new User(1, "test@test.com", "test", "test", null);
    }
}

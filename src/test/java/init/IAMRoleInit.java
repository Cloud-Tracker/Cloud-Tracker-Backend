package init;

import com.example.cloud_tracker.model.IAMRole;

public class IAMRoleInit {
    public static IAMRole createIAMRole() {
        return new IAMRole("123456789012", "testRole", 1);
    }
}

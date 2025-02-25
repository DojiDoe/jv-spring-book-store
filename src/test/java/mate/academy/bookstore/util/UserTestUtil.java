package mate.academy.bookstore.util;

import java.util.Set;
import mate.academy.bookstore.model.Role;
import mate.academy.bookstore.model.User;

public class UserTestUtil {
    private UserTestUtil() {
    }

    public static User createUser() {
        return new User()
                .setId(2L)
                .setEmail("dojidoe@gmail.com")
                .setPassword("$2a$10$Ul8.kr1zQbH8deHIXuGBzO1tlQYairNeXZ0MB0Mg7FYcOS.dcxU62")
                .setFirstName("Doji")
                .setLastName("Doe")
                .setShippingAddress("Stepana Bandery St, 1")
                .setRoles(Set.of(createUserRole()));
    }

    public static Role createUserRole() {
        Role role = new Role();
        role.setId(3L);
        role.setRole(Role.RoleName.ROLE_USER);
        return role;
    }
}

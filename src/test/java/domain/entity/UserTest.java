package domain.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {
    @Test
    void userGettersWork() {
        User u = new User("alice","id123");
        assertEquals("alice", u.getUsername());
        assertEquals("id123", u.getUserId());
    }
}

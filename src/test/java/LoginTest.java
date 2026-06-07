import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Login class.
 * PROG5121 POE - Part 1
 * Tests match exactly the test data specified in the assignment document.
 */
public class LoginTest {

    @BeforeEach
    public void setUp() {
        // Clear any stored registration data before each test
        Login.clearRegistration();
    }

    // ── checkUserName() tests 

    @Test
    public void testUsernameCorrectlyFormatted() {
        // Test Data: "kyl_1" — contains underscore, 5 chars or less
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(login.checkUserName());
    }

    @Test
    public void testUsernameIncorrectlyFormatted() {
        // Test Data: "kyle!!!!!!!" — no underscore, more than 5 chars
        Login login = new Login("kyle!!!!!!!", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(login.checkUserName());
    }

    @Test
    public void testUsernameNoUnderscore() {
        // Username has no underscore
        Login login = new Login("kyle1", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(login.checkUserName());
    }

    @Test
    public void testUsernameTooLong() {
        // Username has underscore but is longer than 5 chars
        Login login = new Login("kyle_1", "Ch&&sec@ke99!", "+27838968976");
        assertFalse(login.checkUserName());
    }

    // ── checkPasswordComplexity() tests 

    @Test
    public void testPasswordMeetsComplexity() {
        // Test Data: "Ch&&sec@ke99!" — meets all complexity rules
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(login.checkPasswordComplexity());
    }

    @Test
    public void testPasswordDoesNotMeetComplexity() {
        // Test Data: "password" — does not meet complexity rules
        Login login = new Login("kyl_1", "password", "+27838968976");
        assertFalse(login.checkPasswordComplexity());
    }

    @Test
    public void testPasswordTooShort() {
        Login login = new Login("kyl_1", "Ab1!", "+27838968976");
        assertFalse(login.checkPasswordComplexity());
    }

    @Test
    public void testPasswordNoCapital() {
        Login login = new Login("kyl_1", "ch&&sec@ke99!", "+27838968976");
        assertFalse(login.checkPasswordComplexity());
    }

    @Test
    public void testPasswordNoNumber() {
        Login login = new Login("kyl_1", "Ch&&sec@ke!!", "+27838968976");
        assertFalse(login.checkPasswordComplexity());
    }

    @Test
    public void testPasswordNoSpecialChar() {
        Login login = new Login("kyl_1", "Chsecake99", "+27838968976");
        assertFalse(login.checkPasswordComplexity());
    }

    // ── checkCellPhoneNumber() tests 

    @Test
    public void testCellPhoneCorrectlyFormatted() {
        // Test Data: +27838968976 — has international code, correct length
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(login.checkCellPhoneNumber());
    }

    @Test
    public void testCellPhoneIncorrectlyFormatted() {
        // Test Data: 08966553 — no international code
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "08966553");
        assertFalse(login.checkCellPhoneNumber());
    }

    @Test
    public void testCellPhoneNoPlus() {
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "27838968976");
        assertFalse(login.checkCellPhoneNumber());
    }

    // ── registerUser() tests 

    @Test
    public void testRegisterUserSuccess() {
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976",
                                "Kyle", "Smith");
        String result = login.registerUser();
        assertTrue(result.contains("successfully"));
    }

    @Test
    public void testRegisterUserBadUsername() {
        Login login = new Login("kyle!!!!!!!", "Ch&&sec@ke99!", "+27838968976",
                                "Kyle", "Smith");
        String result = login.registerUser();
        assertEquals("Username is not correctly formatted; please ensure that your "
                + "username contains an underscore and is no more than five "
                + "characters in length.", result);
    }

    @Test
    public void testRegisterUserBadPassword() {
        Login login = new Login("kyl_1", "password", "+27838968976",
                                "Kyle", "Smith");
        String result = login.registerUser();
        assertEquals("Password is not correctly formatted; please ensure that the "
                + "password contains at least eight characters, a capital "
                + "letter, a number, and a special character.", result);
    }

    @Test
    public void testRegisterUserBadCell() {
        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "08966553",
                                "Kyle", "Smith");
        String result = login.registerUser();
        assertEquals("Cell phone number incorrectly formatted or does not contain "
                + "international code.", result);
    }

    // ── loginUser() tests 

    @Test
    public void testLoginSuccessful() {
        // Register first, then log in with same credentials
        Login reg = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976",
                              "Kyle", "Smith");
        reg.registerUser();

        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        assertTrue(login.loginUser());
    }

    @Test
    public void testLoginFailed() {
        // Register first, then log in with wrong password
        Login reg = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976",
                              "Kyle", "Smith");
        reg.registerUser();

        Login login = new Login("kyl_1", "wrongpassword", "+27838968976");
        assertFalse(login.loginUser());
    }

    // ── returnLoginStatus() tests 

    @Test
    public void testReturnLoginStatusSuccess() {
        Login reg = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976",
                              "Kyle", "Smith");
        reg.registerUser();

        Login login = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976");
        String status = login.returnLoginStatus();
        assertTrue(status.contains("Welcome"));
        assertTrue(status.contains("great to see you again"));
    }

    @Test
    public void testReturnLoginStatusFailed() {
        Login reg = new Login("kyl_1", "Ch&&sec@ke99!", "+27838968976",
                              "Kyle", "Smith");
        reg.registerUser();

        Login login = new Login("kyl_1", "wrongpassword", "+27838968976");
        assertEquals("Username or password incorrect, please try again.",
                     login.returnLoginStatus());
    }
}

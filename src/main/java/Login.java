import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Login class handles user registration and login for QuickChat.
 * PROG5121 POE - Part 1
 */
public class Login {

    private String username;
    private String password;
    private String cellPhoneNumber;
    private String firstName;
    private String lastName;

    // Stored credentials after registration (simple single-user store)
    private static String registeredUsername;
    private static String registeredPassword;
    private static String registeredFirstName;
    private static String registeredLastName;

    public Login(String username, String password, String cellPhoneNumber) {
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
    }

    public Login(String username, String password, String cellPhoneNumber,
                 String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Checks that the username contains an underscore (_) and
     * is no more than five characters long.
     *
     * @return true if valid, false otherwise
     */
    public boolean checkUserName() {
        return username != null
                && username.contains("_")
                && username.length() <= 5;
    }

    /**
     * Checks that the password meets complexity rules:
     * at least 8 characters, one capital letter, one number,
     * one special character.
     *
     * @return true if valid, false otherwise
     */
    public boolean checkPasswordComplexity() {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpper   = password.chars().anyMatch(Character::isUpperCase);
        boolean hasDigit   = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(c ->
                !Character.isLetterOrDigit(c));
        return hasUpper && hasDigit && hasSpecial;
    }

    /**
     * Checks that the cell phone number contains the international
     * country code and the number is no more than ten characters long.
     * Source: regex pattern research - validates +<countrycode><number>
     * format with total length constraint.
     * Reference: https://www.baeldung.com/java-regex-validate-phone-numbers
     *
     * @return true if valid, false otherwise
     */
    public boolean checkCellPhoneNumber() {
        if (cellPhoneNumber == null) {
            return false;
        }
        // Must start with + followed by digits, total length <= 12 (+27 + 9 digits = 12)
        // The number portion (after +code) must be <= 10 chars total including the +code
        String regex = "^\\+\\d{1,3}\\d{1,9}$";
        return Pattern.matches(regex, cellPhoneNumber)
                && cellPhoneNumber.length() <= 12;
    }

    /**
     * Registers the user and returns the appropriate message based on
     * whether username, password, and cell phone conditions are met.
     *
     * @return registration status message
     */
    public String registerUser() {
        if (!checkUserName()) {
            return "Username is not correctly formatted; please ensure that your "
                    + "username contains an underscore and is no more than five "
                    + "characters in length.";
        }
        if (!checkPasswordComplexity()) {
            return "Password is not correctly formatted; please ensure that the "
                    + "password contains at least eight characters, a capital "
                    + "letter, a number, and a special character.";
        }
        if (!checkCellPhoneNumber()) {
            return "Cell phone number incorrectly formatted or does not contain "
                    + "international code.";
        }
        // All conditions met — store credentials
        registeredUsername  = this.username;
        registeredPassword  = this.password;
        registeredFirstName = this.firstName;
        registeredLastName  = this.lastName;

        return "Username successfully captured.\n"
                + "Password successfully captured.\n"
                + "Cell phone number successfully added.\n"
                + "Registration successful.";
    }

    /**
     * Verifies that the login details entered match the stored credentials.
     *
     * @return true if login is successful, false otherwise
     */
    public boolean loginUser() {
        if (registeredUsername == null || registeredPassword == null) {
            return false;
        }
        return registeredUsername.equals(this.username)
                && registeredPassword.equals(this.password);
    }

    /**
     * Returns the appropriate login status message.
     *
     * @return welcome message on success, error message on failure
     */
    public String returnLoginStatus() {
        if (loginUser()) {
            String first = (registeredFirstName != null) ? registeredFirstName : "";
            String last  = (registeredLastName  != null) ? registeredLastName  : "";
            return "Welcome " + first + " " + last + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }

    // ── Getters (needed for unit tests) 

    public String getUsername()        { return username; }
    public String getPassword()        { return password; }
    public String getCellPhoneNumber() { return cellPhoneNumber; }

    public static String getRegisteredUsername()  { return registeredUsername; }
    public static String getRegisteredFirstName() { return registeredFirstName; }
    public static String getRegisteredLastName()  { return registeredLastName; }

    /** Clears static state — used between unit tests. */
    public static void clearRegistration() {
        registeredUsername  = null;
        registeredPassword  = null;
        registeredFirstName = null;
        registeredLastName  = null;
    }
}

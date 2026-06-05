import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Message class.
 * PROG5121 POE - Part 2 & Part 3
 * Tests match exactly the test data specified in the assignment document.
 */
public class MessageTest {

    @BeforeEach
    public void setUp() {
        // Clear all static message state before each test
        Message.clearAll();
    }

    // ── Part 2: validateMessageLength() 

    @Test
    public void testMessageLengthSuccess() {
        // Message under 250 chars — should return "Message ready to send."
        Message msg = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message ready to send.", msg.validateMessageLength());
    }

    @Test
    public void testMessageLengthFailure() {
        // Message over 250 chars — should return error
        String longText = "A".repeat(251);
        Message msg = new Message("+27718693002", longText);
        assertEquals("Please enter a message of less than 250 characters.",
                     msg.validateMessageLength());
    }

    @Test
    public void testMessageLengthExactly250() {
        // Exactly 250 chars — should still pass
        String text = "A".repeat(250);
        Message msg = new Message("+27718693002", text);
        assertEquals("Message ready to send.", msg.validateMessageLength());
    }

    // ── Part 2: checkMessageID() 

    @Test
    public void testMessageIDCreated() {
        // Message ID should be auto-generated and not more than 10 chars
        Message msg = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertTrue(msg.checkMessageID());
        System.out.println("Message ID generated: " + msg.getMessageID());
    }

    @Test
    public void testMessageIDLength() {
        Message msg = new Message("+27718693002", "Test message");
        assertTrue(msg.getMessageID().length() <= 10);
    }

    // ── Part 2: checkRecipientCell() 

    @Test
    public void testRecipientCellValid() {
        // Test Data: +27718693002 — valid international format
        Message msg = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Cell phone number successfully added.", msg.checkRecipientCell());
    }

    @Test
    public void testRecipientCellInvalid() {
        // Test Data: 08575975889 — no international code
        Message msg = new Message("08575975889", "Hi Keegan, did you receive the payment?");
        assertEquals("Cell number is incorrectly formatted or does not contain "
                + "an international code; please correct the number and try again.",
                msg.checkRecipientCell());
    }

    // ── Part 2: createMessageHash() 

    @Test
    public void testMessageHashCorrect() {
        // Test Case 1: recipient +27718693002, message "Hi Mike, can you join us for dinner tonight?"
        // Expected hash: first 2 of ID + ":0:" + first word "HI" + last word "TONIGHT?"
        // Document example format: 00:0:HITONIGHT
        Message msg = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?");
        String hash = msg.getMessageHash();

        assertNotNull(hash);
        assertTrue(hash.equals(hash.toUpperCase())); // must be all caps

        // Hash must follow pattern: XX:N:FIRSTWORDLASTWORD
        assertTrue(hash.matches("[A-Z0-9]+:\\d+:[A-Z,!?']+"));
        System.out.println("Generated hash: " + hash);
    }

    @Test
    public void testMessageHashFormat() {
        Message msg = new Message("+27718693002", "Hi Thanks");
        String hash = msg.getMessageHash();
        // Should contain two colons
        assertEquals(2, hash.chars().filter(c -> c == ':').count());
    }

    // ── Part 2: sentMessage() 

    @Test
    public void testSentMessageSend() {
        // User selected "Send Message" — expect "Message successfully sent."
        Message msg = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message successfully sent.", msg.sentMessage(1));
    }

    @Test
    public void testSentMessageDisregard() {
        // User selected "Disregard Message" — expect "Press 0 to delete the message."
        Message msg = new Message("08575975889", "Hi Keegan, did you receive the payment?");
        assertEquals("Press 0 to delete the message.", msg.sentMessage(2));
    }

    @Test
    public void testSentMessageStore() {
        // User selected "Store Message" — expect "Message successfully stored."
        Message msg = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message successfully stored.", msg.sentMessage(3));
    }

    // ── Part 2: returnTotalMessages() 

    @Test
    public void testReturnTotalMessages() {
        // Send two messages, total should be 2
        Message msg1 = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?");
        msg1.sentMessage(1); // Send

        Message msg2 = new Message("08575975889", "Hi Keegan, did you receive the payment?");
        msg2.sentMessage(2); // Disregard — does NOT count toward total sent

        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    public void testReturnTotalMessagesAfterMultipleSends() {
        Message msg1 = new Message("+27718693002", "Message one");
        msg1.sentMessage(1);

        Message msg2 = new Message("+27718693002", "Message two");
        msg2.sentMessage(1);

        assertEquals(2, Message.returnTotalMessages());
    }

    // ── Part 3: Sent Messages array 

    @Test
    public void testSentMessagesArrayCorrectlyPopulated() {
        // Test Data: messages 1 and 4 are "Sent" (from Part 3 test data)
        // Message 1: "Did you get the cake?"
        // Message 4: "It is dinner time!"
        Message msg1 = new Message("+27834557896", "Did you get the cake?");
        msg1.sentMessage(1);

        Message msg2 = new Message("+27838884567",
                "Where are you? You are late! I have asked you to be on time.");
        msg2.sentMessage(3); // Stored

        Message msg3 = new Message("+27834484567", "Yohoooo, I am at your gate.");
        msg3.sentMessage(2); // Disregarded

        Message msg4 = new Message("0838884567", "It is dinner time!");
        msg4.sentMessage(1); // Sent

        List<String> sent = Message.getSentMessages();
        assertTrue(sent.contains("Did you get the cake?"));
        assertTrue(sent.contains("It is dinner time!"));
    }

    // ── Part 3: displayLongestMessage() 

    @Test
    public void testDisplayLongestMessage() {
        // Test Data messages 1-4 — longest should be message 2
        Message msg1 = new Message("+27834557896", "Did you get the cake?");
        msg1.sentMessage(3); // store so it appears in storedMessages

        Message msg2 = new Message("+27838884567",
                "Where are you? You are late! I have asked you to be on time.");
        msg2.sentMessage(3);

        Message msg3 = new Message("+27834484567", "Yohoooo, I am at your gate.");
        msg3.sentMessage(3);

        Message msg4 = new Message("0838884567", "It is dinner time!");
        msg4.sentMessage(3);

        String longest = Message.displayLongestMessage();
        assertEquals("Where are you? You are late! I have asked you to be on time.", longest);
    }

    // ── Part 3: searchByMessageID() 

    @Test
    public void testSearchByMessageID() {
        // Test Data: message 4 — recipient 0838884567, message "It is dinner time!"
        Message msg4 = new Message("0838884567", "It is dinner time!");
        msg4.sentMessage(1);

        List<Message> allMessages = new ArrayList<>();
        allMessages.add(msg4);

        String result = Message.searchByMessageID(msg4.getMessageID(), allMessages);
        assertEquals("\"It is dinner time!\"", result);
    }

    @Test
    public void testSearchByMessageIDNotFound() {
        List<Message> allMessages = new ArrayList<>();
        String result = Message.searchByMessageID("0000000000", allMessages);
        assertEquals("Message ID not found.", result);
    }

    // ── Part 3: searchByRecipient() 

    @Test
    public void testSearchByRecipient() {
        // Test Data: recipient +27838884567 has messages 2 and 5
        // Message 2: "Where are you? You are late! I have asked you to be on time."
        // Message 5: "Ok, I am leaving without you."
        Message msg2 = new Message("+27838884567",
                "Where are you? You are late! I have asked you to be on time.");
        Message msg5 = new Message("+27838884567", "Ok, I am leaving without you.");

        List<Message> allMessages = new ArrayList<>();
        allMessages.add(msg2);
        allMessages.add(msg5);

        String result = Message.searchByRecipient("+27838884567", allMessages);
        assertTrue(result.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue(result.contains("Ok, I am leaving without you."));
    }

    // ── Part 3: deleteByHash() 

    @Test
    public void testDeleteByHash() {
        // Test Data: delete Test Message 2
        // "Where are you? You are late! I have asked you to be on time."
        Message msg2 = new Message("+27838884567",
                "Where are you? You are late! I have asked you to be on time.");
        msg2.sentMessage(3); // store it first

        List<Message> allMessages = new ArrayList<>();
        allMessages.add(msg2);

        String hash = msg2.getMessageHash();
        String result = Message.deleteByHash(hash, allMessages);

        assertTrue(result.contains("successfully deleted"));
        assertTrue(allMessages.isEmpty());
    }

    @Test
    public void testDeleteByHashNotFound() {
        List<Message> allMessages = new ArrayList<>();
        String result = Message.deleteByHash("FAKEHASH", allMessages);
        assertEquals("Message hash not found.", result);
    }

    // ── Part 3: displayStoredReport() 

    @Test
    public void testDisplayStoredReport() {
        Message msg1 = new Message("+27834557896", "Did you get the cake?");
        Message msg2 = new Message("+27838884567",
                "Where are you? You are late! I have asked you to be on time.");

        List<Message> storedList = new ArrayList<>();
        storedList.add(msg1);
        storedList.add(msg2);

        String report = Message.displayStoredReport(storedList);
        assertTrue(report.contains("Message Hash"));
        assertTrue(report.contains("Recipient"));
        assertTrue(report.contains("Message"));
        assertTrue(report.contains("+27834557896"));
        assertTrue(report.contains("Did you get the cake?"));
    }

    @Test
    public void testDisplayStoredReportEmpty() {
        List<Message> storedList = new ArrayList<>();
        assertEquals("No stored messages to report.", Message.displayStoredReport(storedList));
    }
}

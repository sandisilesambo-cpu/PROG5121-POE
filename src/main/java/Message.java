import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Message class handles message creation, validation, sending,
 * storing and reporting for QuickChat.
 * PROG5121 POE - Part 2 & Part 3
 */
public class Message {

    private String messageID;
    private String recipient;
    private String messageText;
    private String messageHash;
    private String flag; // "Sent", "Stored", "Disregarded"

    // ── Static arrays (Part 3) 
    private static List<String> sentMessages        = new ArrayList<>();
    private static List<String> disregardedMessages = new ArrayList<>();
    private static List<String> storedMessages      = new ArrayList<>();
    private static List<String> messageHashes       = new ArrayList<>();
    private static List<String> messageIDs          = new ArrayList<>();

    private static int totalMessagesSent    = 0;
    private static int numMessagesSent      = 0; // auto-incremented counter

    public Message(String recipient, String messageText) {
        this.recipient   = recipient;
        this.messageText = messageText;
        this.messageID   = generateMessageID();
        this.messageHash = createMessageHash();
    }

    // Constructor used when populating arrays from test data (Part 3)
    public Message(String recipient, String messageText, String flag) {
        this.recipient   = recipient;
        this.messageText = messageText;
        this.flag        = flag;
        this.messageID   = generateMessageID();
        this.messageHash = createMessageHash();
    }

    // ── ID generation 

    /**
     * Generates a random ten-digit number as the message ID.
     *
     * @return ten-digit message ID string
     */
    private String generateMessageID() {
        Random rng = new Random();
        long id = (long)(rng.nextDouble() * 9_000_000_000L) + 1_000_000_000L;
        return String.valueOf(id);
    }

    // ── Validation 

    /**
     * Ensures the message ID is not more than ten characters.
     *
     * @return true if valid
     */
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    /**
     * Ensures the recipient cell number is no more than ten characters long
     * and contains an international code (starts with +).
     *
     * @return "Cell phone number successfully added." or error message
     */
    public String checkRecipientCell() {
        if (recipient == null) {
            return "Cell number is incorrectly formatted or does not contain "
                    + "an international code; please correct the number and try again.";
        }
        if (recipient.startsWith("+") && recipient.length() <= 12) {
            return "Cell phone number successfully added.";
        }
        return "Cell number is incorrectly formatted or does not contain "
                + "an international code; please correct the number and try again.";
    }

    // ── Hash 

    /**
     * Creates and returns the Message Hash.
     * Format: first two digits of message ID + ":" + message number + ":"
     *         + first word of message + last word of message — all in caps.
     * Example: 00:0:HITHANKS
     *
     * @return message hash string in all caps
     */
    public String createMessageHash() {
        numMessagesSent++;
        String idPrefix   = (messageID != null && messageID.length() >= 2)
                            ? messageID.substring(0, 2) : "00";
        String[] words    = (messageText != null && !messageText.isBlank())
                            ? messageText.trim().split("\\s+") : new String[]{"", ""};
        String firstWord  = words[0];
        String lastWord   = words[words.length - 1];
        String hash = (idPrefix + ":" + numMessagesSent + ":" + firstWord + lastWord)
                        .toUpperCase();
        this.messageHash = hash;
        return hash;
    }

    // ── Message sending 

    /**
     * Validates message length (≤ 250 chars).
     *
     * @return "Message ready to send." or error
     */
    public String validateMessageLength() {
        if (messageText != null && messageText.length() <= 250) {
            return "Message ready to send.";
        }
        return "Please enter a message of less than 250 characters.";
    }

    /**
     * Allows the user to choose if they want to send, store, or disregard
     * the message. Returns the appropriate status string.
     *
     * @param choice 1 = Send, 2 = Disregard, 3 = Store
     * @return status message
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                flag = "Sent";
                sentMessages.add(messageText);
                messageHashes.add(messageHash);
                messageIDs.add(messageID);
                totalMessagesSent++;
                return "Message successfully sent.";
            case 2:
                flag = "Disregarded";
                disregardedMessages.add(messageText);
                return "Press 0 to delete the message.";
            case 3:
                flag = "Stored";
                storedMessages.add(messageText);
                messageHashes.add(messageHash);
                messageIDs.add(messageID);
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }

    /**
     * Returns all messages sent while the program is running.
     *
     * @return formatted string of all sent messages
     */
    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages sent yet.";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sentMessages.size(); i++) {
            sb.append("Message ").append(i + 1).append(": ")
              .append(sentMessages.get(i)).append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * Returns the total number of messages sent.
     *
     * @return total sent count
     */
    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    // ── Part 3 — Stored Messages sub-menu methods 

    /**
     * (a) Displays sender and recipient of all stored messages.
     * Sender is identified by the logged-in user's details (passed in).
     *
     * @param senderName logged-in user's display name
     * @return formatted list
     */
    public static String displayStoredSenderRecipient(String senderName,
                                                       List<String> storedRecipients) {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        StringBuilder sb = new StringBuilder("Stored Messages — Sender & Recipient:\n");
        for (int i = 0; i < storedMessages.size(); i++) {
            String rec = (storedRecipients != null && i < storedRecipients.size())
                         ? storedRecipients.get(i) : "Unknown";
            sb.append("Sender: ").append(senderName)
              .append(" | Recipient: ").append(rec).append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * (b) Displays the longest stored message.
     *
     * @return the longest message text
     */
    public static String displayLongestMessage() {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        String longest = "";
        for (String msg : storedMessages) {
            if (msg.length() > longest.length()) {
                longest = msg;
            }
        }
        return longest;
    }

    /**
     * (c) Searches for a message by ID and displays the corresponding
     * recipient and message.
     *
     * @param id          the message ID to search for
     * @param allMessages list of all Message objects (sent + stored)
     * @return matching message details or not-found message
     */
    public static String searchByMessageID(String id, List<Message> allMessages) {
        for (Message m : allMessages) {
            if (m.messageID.equals(id)) {
                return "\"" + m.messageText + "\"";
            }
        }
        return "Message ID not found.";
    }

    /**
     * (d) Searches for all messages sent or stored for a particular recipient.
     *
     * @param recipientNumber recipient cell number to search
     * @param allMessages     list of all Message objects
     * @return all matching messages
     */
    public static String searchByRecipient(String recipientNumber,
                                            List<Message> allMessages) {
        StringBuilder sb = new StringBuilder();
        for (Message m : allMessages) {
            if (recipientNumber.equals(m.recipient)) {
                sb.append("\"").append(m.messageText).append("\" ");
            }
        }
        return sb.length() > 0 ? sb.toString().trim() : "No messages found for that recipient.";
    }

    /**
     * (e) Deletes a message using its message hash.
     *
     * @param hash        the hash to match
     * @param allMessages list of all Message objects (will be mutated)
     * @return deletion confirmation or not-found message
     */
    public static String deleteByHash(String hash, List<Message> allMessages) {
        for (int i = 0; i < allMessages.size(); i++) {
            if (hash.equalsIgnoreCase(allMessages.get(i).messageHash)) {
                String text = allMessages.get(i).messageText;
                allMessages.remove(i);
                // Also remove from storedMessages list if present
                storedMessages.remove(text);
                messageHashes.remove(hash.toUpperCase());
                return "Message: \"" + text + "\" successfully deleted.";
            }
        }
        return "Message hash not found.";
    }

    /**
     * (f) Displays a full report of all stored messages.
     *
     * @param storedList list of stored Message objects
     * @return formatted report: Message Hash, Recipient, Message
     */
    public static String displayStoredReport(List<Message> storedList) {
        if (storedList.isEmpty()) {
            return "No stored messages to report.";
        }
        StringBuilder sb = new StringBuilder("=== Stored Messages Report ===\n");
        for (Message m : storedList) {
            sb.append("Message Hash: ").append(m.messageHash).append("\n");
            sb.append("Recipient   : ").append(m.recipient).append("\n");
            sb.append("Message     : ").append(m.messageText).append("\n");
            sb.append("------------------------------\n");
        }
        return sb.toString().trim();
    }

    // ── Getters 

    public String getMessageID()   { return messageID; }
    public String getRecipient()   { return recipient; }
    public String getMessageText() { return messageText; }
    public String getMessageHash() { return messageHash; }
    public String getFlag()        { return flag; }

    public static List<String> getSentMessages()        { return sentMessages; }
    public static List<String> getDisregardedMessages() { return disregardedMessages; }
    public static List<String> getStoredMessages()      { return storedMessages; }
    public static List<String> getMessageHashes()       { return messageHashes; }
    public static List<String> getMessageIDs()          { return messageIDs; }

    /** Clears all static state — used between unit tests. */
    public static void clearAll() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        totalMessagesSent = 0;
        numMessagesSent   = 0;
    }
}

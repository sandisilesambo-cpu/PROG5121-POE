import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

/**
 * Handles saving and reading messages to and from a JSON file.
 * Research reference: https://code.google.com/archive/p/json-simple/
 */
public class MessageStorage {

    private static final String FILE_NAME = "messages.json";

    /**
     * Saves a message to messages.json.
     * If the file already exists, the new message is added to the existing list.
     * If the file does not exist, it is created with the first message.
     *
     * @param messageID   the unique message ID
     * @param messageHash the generated message hash
     * @param recipient   the recipient cell number
     * @param messageText the message content
     */
    @SuppressWarnings("unchecked")
    public static void storeMessage(String messageID, String messageHash,
                                     String recipient, String messageText) {

        JSONArray messageList = loadExistingMessages();

        // Build a JSON object for this message
        JSONObject entry = new JSONObject();
        entry.put("messageID",   messageID);
        entry.put("messageHash", messageHash);
        entry.put("recipient",   recipient);
        entry.put("message",     messageText);

        messageList.add(entry);

        // Write the updated list back to the file
        try (FileWriter writer = new FileWriter(new File(FILE_NAME))) {
            writer.write(messageList.toJSONString());
            System.out.println("Message successfully stored.");
        } catch (IOException e) {
            System.out.println("Error saving message: " + e.getMessage());
        }
    }

    /**
     * Reads all messages from messages.json and returns them as a JSONArray.
     * Returns an empty array if the file does not exist or cannot be read.
     *
     * @return JSONArray of stored messages
     */
    public static JSONArray loadExistingMessages() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new JSONArray();
        }
        try (FileReader reader = new FileReader(file)) {
            JSONParser parser = new JSONParser();
            Object parsed = parser.parse(reader);
            if (parsed instanceof JSONArray) {
                return (JSONArray) parsed;
            }
        } catch (Exception e) {
            System.out.println("Error reading messages file: " + e.getMessage());
        }
        return new JSONArray();
    }

    /**
     * Prints all stored messages from messages.json to the console.
     */
    public static void printStoredMessages() {
        JSONArray messages = loadExistingMessages();
        if (messages.isEmpty()) {
            System.out.println("No stored messages found.");
            return;
        }
        System.out.println("\n=== Stored Messages from File ===");
        for (Object obj : messages) {
            JSONObject msg = (JSONObject) obj;
            System.out.println("Message ID  : " + msg.get("messageID"));
            System.out.println("Message Hash: " + msg.get("messageHash"));
            System.out.println("Recipient   : " + msg.get("recipient"));
            System.out.println("Message     : " + msg.get("message"));
            System.out.println("------------------------------");
        }
    }
}

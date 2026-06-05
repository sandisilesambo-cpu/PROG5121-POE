import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * QuickChat — main console entry point.
 * PROG5121 POE — all three parts.
 * No GUI / JOptionPane used.
 */
public class QuickChat {

    private static final Scanner scanner = new Scanner(System.in);
    private static List<Message> allMessages = new ArrayList<>();

    public static void main(String[] args) {

        // ── REGISTRATION 
        System.out.println("=== QuickChat Registration ===");
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Enter username (must contain _ and be ≤ 5 chars): ");
        String username = scanner.nextLine();
        System.out.print("Enter password (≥8 chars, capital, number, special): ");
        String password = scanner.nextLine();
        System.out.print("Enter cell phone number (international format, e.g. +27838968976): ");
        String cell = scanner.nextLine();

        Login login = new Login(username, password, cell, firstName, lastName);
        System.out.println(login.registerUser());

        // If registration failed, exit
        if (Login.getRegisteredUsername() == null) {
            System.out.println("Registration failed. Please restart and try again.");
            return;
        }

        // ── LOGIN 
        System.out.println("\n=== QuickChat Login ===");
        System.out.print("Username: ");
        String loginUser = scanner.nextLine();
        System.out.print("Password: ");
        String loginPass = scanner.nextLine();

        Login loginAttempt = new Login(loginUser, loginPass, cell);
        System.out.println(loginAttempt.returnLoginStatus());

        if (!loginAttempt.loginUser()) {
            System.out.println("Access denied. Exiting.");
            return;
        }

        // ── MAIN MENU 
        System.out.println("\nWelcome to QuickChat.");

        System.out.print("How many messages do you wish to enter? ");
        int numMessages = 0;
        try {
            numMessages = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Exiting.");
            return;
        }

        int messagesSentThisSession = 0;
        boolean running = true;

        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");   // Part 3
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    if (messagesSentThisSession >= numMessages) {
                        System.out.println("You have reached your message limit.");
                        break;
                    }
                    sendMessage();
                    messagesSentThisSession++;
                    break;

                case "2":
                    System.out.println("Coming Soon.");
                    break;

                case "3":
                    running = false;
                    System.out.println("Total messages sent: " + Message.returnTotalMessages());
                    break;

                case "4":
                    storedMessagesMenu();
                    break;

                default:
                    System.out.println("Invalid option. Please choose 1, 2, 3, or 4.");
            }
        }
    }

    // ── Send a message 

    private static void sendMessage() {
        System.out.print("Enter recipient cell number (international format): ");
        String recipient = scanner.nextLine();

        System.out.print("Enter your message (max 250 chars): ");
        String text = scanner.nextLine();

        Message msg = new Message(recipient, text);

        // Validate message length
        System.out.println(msg.validateMessageLength());
        if (text.length() > 250) {
            return;
        }

        // Validate recipient
        System.out.println(msg.checkRecipientCell());
        if (!recipient.startsWith("+") || recipient.length() > 12) {
            return;
        }

        // Show message details
        System.out.println("Message ID  : " + msg.getMessageID());
        System.out.println("Message Hash: " + msg.getMessageHash());
        System.out.println("Recipient   : " + msg.getRecipient());
        System.out.println("Message     : " + msg.getMessageText());

        // Ask user what to do
        System.out.println("\nWhat would you like to do?");
        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");
        System.out.print("Choice: ");

        int sendChoice = 0;
        try {
            sendChoice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice.");
            return;
        }

        System.out.println(msg.sentMessage(sendChoice));
        allMessages.add(msg);
    }

    // ── Stored Messages sub-menu (Part 3) 

    private static void storedMessagesMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Stored Messages ---");
            System.out.println("a) Display sender and recipient of all stored messages");
            System.out.println("b) Display the longest stored message");
            System.out.println("c) Search for a message ID");
            System.out.println("d) Search messages by recipient");
            System.out.println("e) Delete a message using message hash");
            System.out.println("f) Display full report of stored messages");
            System.out.println("x) Back");
            System.out.print("Choose: ");
            String opt = scanner.nextLine().trim().toLowerCase();

            switch (opt) {
                case "a":
                    String senderName = Login.getRegisteredFirstName() + " "
                                      + Login.getRegisteredLastName();
                    // Build recipient list from allMessages where flag = Stored
                    List<String> storedRecipients = new ArrayList<>();
                    for (Message m : allMessages) {
                        if ("Stored".equals(m.getFlag())) {
                            storedRecipients.add(m.getRecipient());
                        }
                    }
                    System.out.println(Message.displayStoredSenderRecipient(
                            senderName, storedRecipients));
                    break;

                case "b":
                    System.out.println(Message.displayLongestMessage());
                    break;

                case "c":
                    System.out.print("Enter Message ID to search: ");
                    String searchID = scanner.nextLine().trim();
                    System.out.println(Message.searchByMessageID(searchID, allMessages));
                    break;

                case "d":
                    System.out.print("Enter recipient number: ");
                    String searchRec = scanner.nextLine().trim();
                    System.out.println(Message.searchByRecipient(searchRec, allMessages));
                    break;

                case "e":
                    System.out.print("Enter message hash to delete: ");
                    String delHash = scanner.nextLine().trim();
                    System.out.println(Message.deleteByHash(delHash, allMessages));
                    break;

                case "f":
                    // Only stored messages
                    List<Message> storedList = new ArrayList<>();
                    for (Message m : allMessages) {
                        if ("Stored".equals(m.getFlag())) {
                            storedList.add(m);
                        }
                    }
                    System.out.println(Message.displayStoredReport(storedList));
                    break;

                case "x":
                    back = true;
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}

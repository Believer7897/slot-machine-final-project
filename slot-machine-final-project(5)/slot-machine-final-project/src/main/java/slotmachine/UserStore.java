package slotmachine;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class UserStore {

    private final Path usersFile;
    private final Path creditsFile;

    public UserStore() {
        Path folder = Path.of(System.getProperty("user.home"), ".slot-machine");
        this.usersFile   = folder.resolve("users.txt");
        this.creditsFile = folder.resolve("credits.txt");
        try {
            Files.createDirectories(folder);
            if (!Files.exists(usersFile))   Files.createFile(usersFile);
            if (!Files.exists(creditsFile)) Files.createFile(creditsFile);
        } catch (IOException e) {
            throw new RuntimeException("UserStore init failed", e);
        }
    }

    public boolean register(String username, String password) throws IOException {
        Map<String, String> users = loadFile(usersFile);
        if (users.containsKey(username)) return false;
        // If the username exists return false
        try (BufferedWriter bw = Files.newBufferedWriter(usersFile, StandardOpenOption.APPEND)) {
            // Open the file in append mode to add the new user
            bw.write(username + ":" + password);
            bw.newLine();//moves to next line for next users
        }
        return true;
    }

    public boolean login(String username, String password) throws IOException {
        Map<String, String> users = loadFile(usersFile);
        //Check if the given password matches the stored password for this username
        //If username doesn't exist, users.get() returns null and equals() safely returns false
        return password.equals(users.get(username));
    }

    public int loadCredit(String username, int defaultCredit) throws IOException {
        Map<String, String> credits = loadFile(creditsFile);
        if (!credits.containsKey(username)) return defaultCredit;
        // If this user has no saved credit, return the default value(100)
        try {
            return Integer.parseInt(credits.get(username));
        } catch (NumberFormatException e) {
            return defaultCredit;
            //If stored value is corrupted, go back to default.
        }
    }

    public void saveCredit(String username, int credit) throws IOException {
        //Read the credits file into a map
        Map<String, String> credits = loadFile(creditsFile);
        // Add or update user's credit
        credits.put(username, String.valueOf(credit));
        try (BufferedWriter bw = Files.newBufferedWriter(creditsFile)) {
            for (Map.Entry<String, String> entry : credits.entrySet()) {
                bw.write(entry.getKey() + ":" + entry.getValue());
                bw.newLine();
            }
        }
        // Overwrite the file with the updated map
    }

    private Map<String, String> loadFile(Path path) throws IOException {
        Map<String, String> map = new HashMap<>();
        for (String line : Files.readAllLines(path)) {
            //Split the line at the first colon: "batu:1500" → ["batu", "1500"]
            String[] parts = line.split(":", 2);
            if (parts.length == 2) map.put(parts[0], parts[1]);
            //Only add valid lines that have matching a key and a value
        }
        return map;
    }
}

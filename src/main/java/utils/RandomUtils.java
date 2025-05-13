package utils;

import java.util.Random;

public class RandomUtils {
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random random = new Random();

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    public static String generateRandomEmail() {
        return generateRandomString(10) + "@example.com";
    }

    public static String generateRandomName() {
        return "User_" + generateRandomString(5);
    }
}
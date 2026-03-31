package com.foodapp.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public static void log(String message) {
        System.out.println("[" + LocalDateTime.now().format(formatter) + "] " + message);
    }
    
    public static void logError(String message, Exception e) {
        System.err.println("[" + LocalDateTime.now().format(formatter) + "] ERROR: " + message);
        if (e != null) {
            e.printStackTrace();
        }
    }
    
    public static void logUserAction(String action, String userEmail) {
        log("USER ACTION: " + userEmail + " - " + action);
    }
    
    public static void logAPICall(String method, String endpoint) {
        log("API CALL: " + method + " " + endpoint);
    }
}

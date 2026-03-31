package com.foodapp.util;

public class EmailUtil {
    
    public static void sendRegistrationEmail(String toEmail, String userName) {
        Logger.log("=== EMAIL NOTIFICATION ===");
        Logger.log("To: " + toEmail);
        Logger.log("Subject: Welcome to Food Ordering App!");
        Logger.log("Body: Dear " + userName + ",\n" +
                   "Thank you for registering with Food Ordering App.\n" +
                   "You can now browse restaurants and place orders.\n" +
                   "Happy ordering!");
        Logger.log("========================");
    }
    
    public static void sendOrderConfirmationEmail(String toEmail, String userName, int orderId, double totalAmount) {
        Logger.log("=== EMAIL NOTIFICATION ===");
        Logger.log("To: " + toEmail);
        Logger.log("Subject: Order Confirmation - Order #" + orderId);
        Logger.log("Body: Dear " + userName + ",\n" +
                   "Your order #" + orderId + " has been confirmed!\n" +
                   "Total Amount: $" + totalAmount + "\n" +
                   "We'll notify you when your order is ready.\n" +
                   "Thank you for your order!");
        Logger.log("========================");
    }
    
    public static void sendOrderCancellationEmail(String toEmail, String userName, int orderId) {
        Logger.log("=== EMAIL NOTIFICATION ===");
        Logger.log("To: " + toEmail);
        Logger.log("Subject: Order Cancelled - Order #" + orderId);
        Logger.log("Body: Dear " + userName + ",\n" +
                   "Your order #" + orderId + " has been cancelled as requested.\n" +
                   "We hope to serve you again soon!");
        Logger.log("========================");
    }
}

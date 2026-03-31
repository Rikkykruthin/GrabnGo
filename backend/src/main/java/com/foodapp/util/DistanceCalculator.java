package com.foodapp.util;

public class DistanceCalculator {
    
    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double DELIVERY_FEE_PER_KM = 10.0; // ₹10 per km
    private static final double FREE_DELIVERY_THRESHOLD_KM = 3.0;
    
    /**
     * Calculate distance between two points using Haversine formula
     * @param lat1 Latitude of point 1
     * @param lon1 Longitude of point 1
     * @param lat2 Latitude of point 2
     * @param lon2 Longitude of point 2
     * @return Distance in kilometers
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
    
    /**
     * Calculate delivery fee based on distance
     * Rule: <3km = ₹0, >=3km = ₹10 per km
     * @param distanceKm Distance in kilometers
     * @return Delivery fee in rupees
     */
    public static double calculateDeliveryFee(double distanceKm) {
        if (distanceKm < FREE_DELIVERY_THRESHOLD_KM) {
            return 0.0;
        }
        return Math.round(distanceKm * DELIVERY_FEE_PER_KM * 100.0) / 100.0;
    }
    
    /**
     * Calculate both distance and delivery fee
     * @param userLat User latitude
     * @param userLon User longitude
     * @param restaurantLat Restaurant latitude
     * @param restaurantLon Restaurant longitude
     * @return Array with [distance, deliveryFee]
     */
    public static double[] calculateDistanceAndFee(double userLat, double userLon, 
                                                   double restaurantLat, double restaurantLon) {
        double distance = calculateDistance(userLat, userLon, restaurantLat, restaurantLon);
        double fee = calculateDeliveryFee(distance);
        return new double[]{distance, fee};
    }
}

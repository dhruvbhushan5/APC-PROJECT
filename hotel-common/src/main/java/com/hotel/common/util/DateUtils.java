package com.hotel.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for date operations commonly used in hotel booking system
 */
public class DateUtils {
    
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private DateUtils() {
        // Utility class
    }
    
    /**
     * Calculate number of nights between check-in and check-out dates
     * @param checkIn Check-in date
     * @param checkOut Check-out date
     * @return Number of nights
     */
    public static int calculateNumberOfNights(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(checkIn, checkOut);
    }
    
    /**
     * Check if dates overlap (for booking conflicts)
     * @param start1 First period start
     * @param end1 First period end
     * @param start2 Second period start
     * @param end2 Second period end
     * @return true if dates overlap
     */
    public static boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !end1.isBefore(start2) && !start1.isAfter(end2);
    }
    
    /**
     * Check if a date is within a date range
     * @param date Date to check
     * @param startDate Range start
     * @param endDate Range end
     * @return true if date is within range
     */
    public static boolean isDateInRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
    
    /**
     * Get start of day timestamp
     * @param date Date
     * @return Start of day LocalDateTime
     */
    public static LocalDateTime getStartOfDay(LocalDate date) {
        return date.atStartOfDay();
    }
    
    /**
     * Get end of day timestamp
     * @param date Date
     * @return End of day LocalDateTime
     */
    public static LocalDateTime getEndOfDay(LocalDate date) {
        return date.atTime(23, 59, 59, 999999999);
    }
    
    /**
     * Format date for display
     * @param date Date to format
     * @return Formatted date string
     */
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : null;
    }
    
    /**
     * Format datetime for display
     * @param dateTime DateTime to format
     * @return Formatted datetime string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : null;
    }
    
    /**
     * Check if check-in date is valid (not in the past)
     * @param checkInDate Check-in date
     * @return true if valid
     */
    public static boolean isValidCheckInDate(LocalDate checkInDate) {
        return checkInDate != null && !checkInDate.isBefore(LocalDate.now());
    }
    
    /**
     * Check if check-out date is after check-in date
     * @param checkInDate Check-in date
     * @param checkOutDate Check-out date
     * @return true if valid
     */
    public static boolean isValidCheckOutDate(LocalDate checkInDate, LocalDate checkOutDate) {
        return checkInDate != null && checkOutDate != null && checkOutDate.isAfter(checkInDate);
    }
    
    /**
     * Validate booking dates
     * @param checkInDate Check-in date
     * @param checkOutDate Check-out date
     * @return true if both dates are valid
     */
    public static boolean areValidBookingDates(LocalDate checkInDate, LocalDate checkOutDate) {
        return isValidCheckInDate(checkInDate) && isValidCheckOutDate(checkInDate, checkOutDate);
    }
}

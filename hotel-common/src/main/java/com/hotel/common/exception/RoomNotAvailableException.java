package com.hotel.common.exception;

/**
 * Exception thrown when a room is not available for booking
 */
public class RoomNotAvailableException extends BusinessException {
    
    public RoomNotAvailableException(String message) {
        super(message, "ROOM_NOT_AVAILABLE");
    }
    
    public RoomNotAvailableException(Long roomId) {
        super(String.format("Room with id %s is not available for booking", roomId), "ROOM_NOT_AVAILABLE");
    }
    
    public RoomNotAvailableException(String message, Throwable cause) {
        super(message, "ROOM_NOT_AVAILABLE", cause);
    }
}

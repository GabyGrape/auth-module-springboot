package com.oksys.auth.exception;

// Custom RuntimeException khusus untuk menandai resource (seperti Ticket) yang tidak ditemukan di DB
public class ResourceNotFoundException extends RuntimeException{
    // Meneruskan pesan error ke parent class RuntimeException
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

package com.smartplanner.exception;

import lombok.Getter;

@Getter
public class InvalidDataProvidedException extends RuntimeException {

    public InvalidDataProvidedException(String message) {
        super(message);
    }
}

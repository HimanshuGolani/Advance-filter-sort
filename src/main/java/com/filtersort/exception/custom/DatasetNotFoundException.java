package com.filtersort.exception.custom;

public class DatasetNotFoundException extends RuntimeException {
    public DatasetNotFoundException(String message) {
        super(message);
    }
}

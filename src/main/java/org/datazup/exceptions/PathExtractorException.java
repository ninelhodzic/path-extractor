package org.datazup.exceptions;

/**
 * Created by ninel on 10/28/17.
 */
public class PathExtractorException extends RuntimeException {

    public PathExtractorException(String message){
        super(message);
    }

    public PathExtractorException(String message, Throwable throwable){
        super(message, throwable);
    }
}

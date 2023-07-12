package com.hangout.hangout.domain.image.exception;

import static com.hangout.hangout.global.error.ResponseEntity.failureResponse;

import com.hangout.hangout.global.error.ResponseEntity;
import com.hangout.hangout.global.error.ResponseType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class FileExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<HttpStatus> fileSizeLimitExceededException() {
        return failureResponse(ResponseType.MAX_UPLOAD_SIZE_EXCEEDED);
    }

    @ExceptionHandler(UnSupportedFileTypeException.class)
    public ResponseEntity<HttpStatus> UnSupportedFileTypeException() {
        return failureResponse(ResponseType.INVALID_FILE_TYPE);
    }
}

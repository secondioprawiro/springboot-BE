package com.bootcamp.userservice.exception;

import com.bootcamp.userservice.dto.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;

@ControllerAdvice
public class GlobalAdviceException {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BaseResponse<Object>> handleBadRequest(BadRequestException e) {
        //status f
        //message = ex
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.failed(null, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        ArrayList<String> errors = new ArrayList<>();
        e.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.add(error.getDefaultMessage()));
        BaseResponse<Object> response = new BaseResponse<>();
        response.setStatus("F");
        response.setMsg("Validation Error");
        response.setData(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.failed(errors, "Validation Error"));
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<BaseResponse<Object>> handleDataNotFound(DataNotFoundException e) {
        //status f
        //message = ex
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.failed("Kosong Bos", e.getMessage()));
    }

    @ExceptionHandler(FeignExceptionNotFound.class)
    public ResponseEntity<BaseResponse<Object>> handleFeignNotFound(FeignExceptionNotFound e) {
        //status f
        //message = ex
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.failed("Kosong Bos", e.getMessage()));
    }
}

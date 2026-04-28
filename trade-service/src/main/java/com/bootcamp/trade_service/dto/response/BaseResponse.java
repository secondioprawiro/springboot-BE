package com.bootcamp.trade_service.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class BaseResponse<T> {
    private UUID reqId = UUID.randomUUID();
    private String status;
    private String msg;
    private T data;

    public static <T> BaseResponse<T> success(T data, String msg) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus("T");
        response.setMsg(msg);
        response.setData(data);
        return response;
    }

    public static <T> BaseResponse<T> failed(T data, String msg) {
        BaseResponse<T> response = new BaseResponse<>();
        response.setStatus("F");
        response.setMsg(msg);
        response.setData(data);
        return response;
    }
}

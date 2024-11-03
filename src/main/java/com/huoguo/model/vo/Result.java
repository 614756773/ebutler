package com.huoguo.model.vo;

import lombok.Data;

@Data
public class Result<T> {
    private Boolean success;
    private T data;
    private String code;
    private String message;
    public static<T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setData(data);
        return result;
    }

    public static<T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setMessage(message);
        result.setCode("500");
        return result;
    }

    public static<T> Result<T> fail(String message) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setMessage(message);
        result.setCode("400");
        return result;
    }
}

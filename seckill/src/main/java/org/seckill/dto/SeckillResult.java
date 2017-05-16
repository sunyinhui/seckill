package org.seckill.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by sunyinhui on 5/16/17.
 */
// 封装ajax请求返回json数据
public class SeckillResult<T> {

    private boolean success;

    @JsonProperty
    private T data;

    private String error;

    // 成功
    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    // 失败
    public SeckillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

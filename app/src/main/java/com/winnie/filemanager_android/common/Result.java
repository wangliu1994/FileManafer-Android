package com.winnie.filemanager_android.common;

/**
 * @author: winnie
 * @date: 2020年10月11日
 */
public class Result<T> {
    private T data;

    private String errorCode;

    private String errorMessage;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}

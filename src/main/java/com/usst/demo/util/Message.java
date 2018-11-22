package com.usst.demo.util;

public class Message {
    public static final Integer SUCCESS = 200;
    public static final Integer PERMISSION_LIMITED = 300;
    public static final Integer NOT_FOUND = 404;
    public static final Integer UNKNOWN_ERROR = 500;
    private String message;
    private Integer status;

    public Message(){}
    public Message(Integer status, String message){
        this.status = status;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

package com.zero.common;

public class Result<T> {
    private int code = 0;
    private long count;
    private String msg;
    private T data;

    public Result() {
    }

    public Result(T data) {
        this.data = data;
    }

    public Result(String msg) {
        this.msg = msg;
    }

    public Result(long count, T data) {
        this.count = count;
        this.data = data;
    }

    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(int code, long count, String msg, T data) {
        this.code = code;
        this.count = count;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static Result resultSuccess(long count, Object data){
        return new Result(count, data);
    }

    public static Result resultSuccess(){
        return new Result();
    }

    public static Result resultSuccess(Object data) {
        return new Result(data);
    }

    public static Result resultSuccess(String msg) {
        return new Result(msg);
    }

    public static Result resultFailure(String msg) {
        return new Result(-1, msg);
    }
}

package com.huang.base.exception;

public class XueChengPlusException extends RuntimeException{
    private String errMessage;

    public XueChengPlusException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }

    public XueChengPlusException() {
    }

    public static void cast(String errMessage){
        throw new XueChengPlusException(errMessage);
    }
    public static void cast(CommonError error){
        throw new XueChengPlusException(error.getErrMessage());
    }

}

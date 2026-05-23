package edu.njucm.retrievejava.vo;

public enum ResultEnum {
    OK(true,200,"Success!"),
    BAD_REQUEST(true,400,"Parameter is incorrect!");
    private Boolean success;
    private Integer code;
    private String message;

    ResultEnum(Boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

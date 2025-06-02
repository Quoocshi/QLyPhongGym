package hahaha.model;

import java.io.Serializable;

public class PaymentRes implements Serializable {
    private String status;
    private String message;
    private String URL;
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getURL() {
        return URL;
    }
    public void setURL(String uRL) {
        URL = uRL;
    }
}

package hahaha.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PaymentRes implements Serializable {
    private String status;
    private String message;
    private String URL;
}

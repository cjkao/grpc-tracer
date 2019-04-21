package cj.poc.domain;

import lombok.Data;

import java.io.Serializable;

@Data
//@AllArgsConstructor
public class AdpCustomer implements Serializable {
    private String firstName;
    private String lastName;
    private String street;
    private String city;

    public AdpCustomer(Object... arr) {
        if (arr.length == 4) {
            firstName = (String) arr[0];
            lastName = (String) arr[1];
            street = (String) arr[2];
            city = (String) arr[3];
        }
    }
}

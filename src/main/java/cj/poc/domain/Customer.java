package cj.poc.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Customer {
    private String FIRSTNAME;
    private String LASTNAME;
    private String STREET;
    private String CITY;

}

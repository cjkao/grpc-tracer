package cj.poc.domain;

import lombok.Data;

@Data
public class Invoice {
    public Invoice(Object... input) {
        if (input.length >= 3) {
            id = Integer.parseInt((String)input[0]);
            custId = Integer.parseInt((String)input[1]);
            total = Double.parseDouble((String)input[2]);
        }
    }

    private int id;
    private int custId;
    private double total;

}

package ru.otus;

import java.util.*;

public class CustomerReverseOrder {

    private final Deque<Customer> customersLIFO = new ArrayDeque<>();
    public void add(Customer customer) {
        customersLIFO.addLast(customer);
    }

    public Customer take() {
        return customersLIFO.removeLast();
    }
}
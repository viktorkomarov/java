package ru.otus;

import java.util.*;

public class CustomerService {
    private final NavigableMap<Customer, String> sortedByScoreCustomers =new TreeMap<>(Comparator.comparingLong(Customer::getScores));

    public Map.Entry<Customer, String> getSmallest() {
        return nullOrCopy(sortedByScoreCustomers.firstEntry());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        return nullOrCopy(sortedByScoreCustomers.higherEntry(customer));
    }

    public void add(Customer customer, String data) {
        sortedByScoreCustomers.put(customer, data);
    }

    private Map.Entry<Customer, String> nullOrCopy(Map.Entry<Customer, String> entry) {
        if (entry == null) {
            return null;
        }
        return Map.entry(Customer.copyOf(entry.getKey()), entry.getValue());
    }
}

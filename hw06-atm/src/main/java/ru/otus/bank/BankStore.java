package ru.otus.bank;

import ru.otus.money.Denomination;

import java.util.List;
import java.util.Map;

public interface BankStore {
    void deposit(Denomination denomination, Integer amount);
    Integer withdrawal(Denomination denomination, Integer amount);
    Map<Denomination, Integer> availableBalance();
    List<Denomination> availableDenomination();
}

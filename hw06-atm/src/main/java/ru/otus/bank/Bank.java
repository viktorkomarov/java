package ru.otus.bank;

import ru.otus.money.Denomination;

import java.util.*;

public class Bank implements BankStore {
    private final Map<Denomination, Integer> balanceByDenomination = new HashMap<>();
    private final List<Denomination> denominations;
    public Bank(List<Denomination> denominations){
        denominations.forEach(n -> this.balanceByDenomination.put(n, 0));
        this.denominations = denominations.
                stream().sorted(Comparator.comparingInt(Denomination::getCents).reversed()).
                toList();
    }

    @Override
    public void deposit(Denomination denomination, Integer amount){
        if (amount < 0) {
            throw new IllegalArgumentException("amount can't be < 0: " + amount);
        }
        balanceByDenomination.put(
                denomination, balanceByDenomination.get(denomination) + amount
        );
    }

    @Override
    public Integer withdrawal(Denomination denomination, Integer amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount can't be < 0: " + amount);
        }

        var balance = balanceByDenomination.get(denomination);
        if (balance < amount) {
            throw new IllegalStateException("balance < amount: " + balance + " < " + amount);
        }
        balanceByDenomination.put(denomination, balance - amount);
        return amount;
    }

    @Override
    public Map<Denomination, Integer> availableBalance() {
        return Collections.unmodifiableMap(balanceByDenomination);
    }

    @Override
    public List<Denomination> availableDenomination() {
        return Collections.unmodifiableList(denominations);
    }
}

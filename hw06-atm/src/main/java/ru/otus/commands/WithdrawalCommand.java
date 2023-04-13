package ru.otus.commands;


import ru.otus.bank.BankStore;
import ru.otus.money.Denomination;

import java.util.*;

public class WithdrawalCommand {
    private final BankStore store;
    private int targetAmount;

    public WithdrawalCommand(BankStore store, int targetAmount) {
        var minMoney = store.
                availableDenomination().
                stream().
                min(Comparator.comparingInt(Denomination::getCents)).get();

        if (targetAmount % minMoney.getCents() != 0) {
            throw new IllegalArgumentException("target % min_cents != 0");
        }
        this.store = store;
        this.targetAmount = targetAmount;
    }

    public ResultCommand<Map<Denomination, Integer>> execute() {
        var toWithdrawal = calculateWithdrawalBanknotes();
        if (toWithdrawal.isEmpty()) {
            return ResultCommand.declineOf("not enough banknotes");
        }
        return withdrawal(toWithdrawal.get());
    }

    private ResultCommand<Map<Denomination, Integer>> withdrawal(Map<Denomination, Integer> toWithdrawal) {
        for (var denomination : toWithdrawal.keySet()) {
            var amount = toWithdrawal.get(denomination);
            if (!store.withdrawal(denomination, amount).equals(amount)) {
               return ResultCommand.declineOf("not enough amount of " + denomination);
            }
        }
        return ResultCommand.successOf(toWithdrawal);
    }

    private Optional<Map<Denomination, Integer>> calculateWithdrawalBanknotes() {
        var denominations = store.availableDenomination();
        var balance = store.availableBalance();
        Map<Denomination, Integer> toWithdrawal = new HashMap<>();
        for (int i = 0; i < denominations.size() && targetAmount != 0; i++) {
            var denomination = denominations.get(i);
            var amount = Math.min(balance.get(denomination),  targetAmount / denomination.getCents());
            if (amount != 0) {
                toWithdrawal.put(denomination, amount);
                targetAmount -= amount * denomination.getCents();
            }
        }

        if (targetAmount == 0) {
            return Optional.of(toWithdrawal);
        }
        return Optional.empty();
    }

}

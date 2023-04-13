package ru.otus;

import ru.otus.bank.BankStore;
import ru.otus.commands.ResultCommand;
import ru.otus.commands.WithdrawalCommand;
import ru.otus.money.Denomination;

import java.util.Map;

public class ATM {
    private final BankStore bank;

    public ATM(BankStore bank) {
        this.bank = bank;
    }

    public ResultCommand<Map<Denomination, Integer>> withdrawal(Integer amount) {
        var cmd = new WithdrawalCommand(bank, amount);
        return cmd.execute();
    }

    public Map<Denomination, Integer> balance() {
        return bank.availableBalance();
    }
}

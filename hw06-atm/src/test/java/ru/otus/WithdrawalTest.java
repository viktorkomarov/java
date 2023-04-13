package ru.otus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.bank.Bank;
import ru.otus.bank.BankStore;
import ru.otus.commands.WithdrawalCommand;
import ru.otus.money.Denomination;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WithdrawalTest {
    private BankStore bankStore;
    private final List<Denomination> denominations = List.of(Denomination.DENOMINATION_10, Denomination.DENOMINATION_5);
    @BeforeEach
    public void initBankStore() {
        this.bankStore = new Bank(denominations);
    }

    @Test
    public void exceptionIllegalAmount(){
        assertThrows(IllegalArgumentException.class, ()->new WithdrawalCommand(bankStore, 2));
    }

    @Test
    public void happyPath() {
        bankStore.deposit(Denomination.DENOMINATION_5, 10);
        bankStore.deposit(Denomination.DENOMINATION_10, 20);

        var cmd = new WithdrawalCommand(bankStore, 25);
        var expected = cmd.execute();

        assertThat(expected.isResult()).isEqualTo(true);
        assertThat(expected.getResult()).isEqualTo(
                Map.of(Denomination.DENOMINATION_10, 2, Denomination.DENOMINATION_5, 1)
        );
    }

    @Test
    public void notEnoughBanknotes() {
        var cmd = new WithdrawalCommand(bankStore, 25);
        var expected = cmd.execute();
        assertThat(expected.isResult()).isEqualTo(false);
    }
}

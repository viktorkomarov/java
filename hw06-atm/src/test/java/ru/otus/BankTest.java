package ru.otus;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.bank.Bank;
import ru.otus.money.Denomination;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class BankTest {
    @Test
    public void balanceAfterDeposit() {
        List<Denomination>  denominations = List.of(Denomination.DENOMINATION_5, Denomination.DENOMINATION_10);
        var bank = new Bank(denominations);

        bank.deposit(Denomination.DENOMINATION_10, 10);

        var balanceAfterDeposit = bank.availableBalance();
        assertThat(balanceAfterDeposit.get(Denomination.DENOMINATION_10)).isEqualTo(10);
        assertThat(balanceAfterDeposit.get(Denomination.DENOMINATION_5)).isEqualTo(0);
    }

    @Test
    public void illegalAmountDeposit() {
        List<Denomination>  denominations = List.of(Denomination.DENOMINATION_5, Denomination.DENOMINATION_10);
        var bank = new Bank(denominations);


        assertThrows(IllegalArgumentException.class, ()->bank.deposit(Denomination.DENOMINATION_10, -10));
    }

    @Test
    public void balanceAfterWithdrawal() {
        List<Denomination>  denominations = List.of(Denomination.DENOMINATION_5, Denomination.DENOMINATION_10);
        var bank = new Bank(denominations);
        bank.deposit(Denomination.DENOMINATION_10, 10);

        bank.withdrawal(Denomination.DENOMINATION_10, 5);

        var balanceAfterWithdrawal = bank.availableBalance();
        assertThat(balanceAfterWithdrawal.get(Denomination.DENOMINATION_10)).isEqualTo(5);
        assertThat(balanceAfterWithdrawal.get(Denomination.DENOMINATION_5)).isEqualTo(0);
    }

    @Test
    public void failedAttemptToWithdrawalMoreThanHave() {
        List<Denomination>  denominations = List.of(Denomination.DENOMINATION_5, Denomination.DENOMINATION_10);
        var bank = new Bank(denominations);
        bank.deposit(Denomination.DENOMINATION_10, 10);

        assertThrows(IllegalStateException.class, ()->bank.withdrawal(Denomination.DENOMINATION_10, 15));
    }

    @Test
    public void illegalAmountWithdrawal() {
        List<Denomination>  denominations = List.of(Denomination.DENOMINATION_5, Denomination.DENOMINATION_10);
        var bank = new Bank(denominations);
        assertThrows(IllegalArgumentException.class, ()->bank.deposit(Denomination.DENOMINATION_10, -10));
    }

    @Test
    public void denomination(){
        List<Denomination>  denominations = List.of(Denomination.DENOMINATION_10, Denomination.DENOMINATION_5);
        var bank = new Bank(denominations);
        assertThat(bank.availableDenomination()).isEqualTo(denominations);
    }
}

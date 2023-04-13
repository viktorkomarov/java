package ru.otus.money;

public enum Denomination implements Comparable<Denomination>{
    DENOMINATION_5(5), DENOMINATION_10(10);
    private final int cents;
    private Denomination(int cents) {
        this.cents = cents;
    }

    public int getCents() {
        return cents;
    }
}

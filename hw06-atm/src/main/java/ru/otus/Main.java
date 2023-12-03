package ru.otus;


public class Main {
    private static <T> T identify(T id) {
        return id;
    }

    public static void main(String[] args) {
        Integer five = identify(5);
    }
}
package ru.otus;


import ru.otus.logger.Log;
import ru.otus.logger.TestLoggingInterface;

class TestImpl implements TestLoggingInterface {

    @Override
    @Log
    public void calculation(int param) {

    }

    @Override
    public void calculation(int param, int param1, int param2) {

    }

    @Override
    @Log
    public void calculation(int param, String param1) {

    }

    @Override
    public void calculation(String param, int param1, String param3) {

    }
}

public class Main {
    public static void main(String[] args) {
        TestLoggingInterface test = IoCTestLoggingInterface.of(new TestImpl());
        test.calculation(56);
        test.calculation(56,10, 100);
        test.calculation(50, "Show ME");
        test.calculation("Hidden", 100, "Hidden");
    }
}
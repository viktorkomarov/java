package ru.otus.commands;

public class ResultCommand<T> {
    private final String declineReason;
    private final T result;

    private ResultCommand(String declineReason){
        this.declineReason = declineReason;
        this.result = null;
    }

    private ResultCommand(T result){
        this.result = result;
        this.declineReason = null;
    }

    public boolean isResult() {
        return result != null;
    }

    public T getResult(){
        return result;
    }

    public String getDeclineReason(){
        return declineReason;
    }

    public static<T> ResultCommand<T> declineOf(String reason) {
        return new ResultCommand<>(reason);
    }

    public static <T> ResultCommand<T> successOf(T result) {
        return new ResultCommand<>(result);
    }

}

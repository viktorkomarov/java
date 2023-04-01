package ru.otus;

public class ExecutionStat {
    public final static ExecutionStat ZERO = new ExecutionStat(0,0,0);
    public final static ExecutionStat SINGLE_FAIL = new ExecutionStat(1,1,0);
    public final static ExecutionStat SINGLE_SUCCESS = new ExecutionStat(1,0,1);
    private final long allCases;
    private final long failedCases;
    private final long successfulCases;
    private ExecutionStat(long allCases,long failedCases,long successfulCases ) {
        this.allCases = allCases;
        this.failedCases = failedCases;
        this.successfulCases = successfulCases;
    }

    public ExecutionStat apply(ExecutionStat stat) {
        return new ExecutionStat(
                allCases+stat.allCases,
                failedCases+stat.failedCases,
                successfulCases+stat.successfulCases
        );
    }
    public boolean isFailed() {
        return failedCases > 0;
    }

    public long getAllCases() {
        return allCases;
    }

    public long getFailedCases() {
        return failedCases;
    }

    public long getSuccessfulCases() {
        return successfulCases;
    }
}

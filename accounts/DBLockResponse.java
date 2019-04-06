package org.debatetool.io.accounts;

public class DBLockResponse {
    private final String message;
    private final ResultType resultType;

    public DBLockResponse(String message, ResultType resultType) {
        this.message = message;
        this.resultType = resultType;
    }

    public String getMessage() {
        return message;
    }

    public ResultType getResultType() {
        return resultType;
    }

    public enum ResultType {SUCCESS, FAILURE_LOCKEDBY}


}

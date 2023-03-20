package org.example.common.solr.exception;

public class RepositoryException extends HdrException {
    public static final int SERVER_ERROR = 501;
    public static final int DUPLICATED_ERROR = 502;
    private static final long serialVersionUID = 8300417997332022872L;
    private Throwable throwable;

    public RepositoryException(int code) {
        super(code);
    }

    public RepositoryException(int code, String message) {
        super(code, message);
    }

    public RepositoryException(int code, Object data, String message) {
        super(code, data, message);
    }

    public RepositoryException(int code, Throwable throwable) {
        super(code);
        this.throwable = throwable;
    }

    public RepositoryException(int code, String message, Throwable throwable) {
        super(code, message);
        this.throwable = throwable;
    }

    public RepositoryException(int code, Object data, String message, Throwable throwable) {
        super(code, data, message);
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }
}

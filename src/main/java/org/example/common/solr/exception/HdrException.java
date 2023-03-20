package org.example.common.solr.exception;

public class HdrException extends Exception {
    private static final long serialVersionUID = 1L;
    protected int code;
    private Object data;

    public HdrException(int code) {
        this.code = code;
    }

    public HdrException(int code, String message) {
        super(message);
        this.code = code;
    }

    public HdrException(int code, Object data, String message) {
        super(data + ": " + message);
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String toString() {
        return this.getClass().getSimpleName() + " Error-Code:" + this.code + ", Data:" + this.data + ", Message:" + this.getMessage();
    }

    public static class UnknownException extends HdrException {
        private static final long serialVersionUID = -7257749122280775815L;

        public UnknownException(int code, Object data, String message) {
            super(code, data, message);
        }

        public UnknownException(ResponseCode code, String field) {
            super(code.getUnknownCode(), field, code.getUnknownMessage());
        }

        public UnknownException(ResponseCode code, String field, String message) {
            super(code.getUnknownCode(), field, message);
        }
    }

    public static class DateTimeFormatException extends HdrException {
        private static final long serialVersionUID = -4657301284704548008L;

        public DateTimeFormatException(int code, Object data, String message) {
            super(code, data, message);
        }

        public DateTimeFormatException(ResponseCode code, String field) {
            super(code.getEmailFormatCode(), field, code.getEmailFormatMessage());
        }

        public DateTimeFormatException(ResponseCode code, String field, String message) {
            super(code.getEmailFormatCode(), field, message);
        }
    }

    public static class EmailFormatException extends HdrException {
        private static final long serialVersionUID = -4657301284704548008L;

        public EmailFormatException(int code, Object data, String message) {
            super(code, data, message);
        }

        public EmailFormatException(ResponseCode code, String field) {
            super(code.getEmailFormatCode(), field, code.getEmailFormatMessage());
        }

        public EmailFormatException(ResponseCode code, String field, String message) {
            super(code.getEmailFormatCode(), field, message);
        }
    }

    public static class NumberFormatException extends HdrException {
        private static final long serialVersionUID = -4657301284704548008L;

        public NumberFormatException(int code, Object data, String message) {
            super(code, data, message);
        }

        public NumberFormatException(ResponseCode code, String field) {
            super(code.getNumberFormatCode(), field, code.getNumberFormatMessage());
        }

        public NumberFormatException(ResponseCode code, String field, String message) {
            super(code.getNumberFormatCode(), field, message);
        }
    }

    public static class PhoneNumberFormatException extends HdrException {
        private static final long serialVersionUID = -4657301284704548008L;

        public PhoneNumberFormatException(int code, Object data, String message) {
            super(code, data, message);
        }

        public PhoneNumberFormatException(ResponseCode code, String field) {
            super(code.getPhoneNumberFormatCode(), field, code.getPhoneNumberFormatMessage());
        }

        public PhoneNumberFormatException(ResponseCode code, String field, String message) {
            super(code.getPhoneNumberFormatCode(), field, message);
        }
    }

    public static class DecryptCodeException extends HdrException {
        private static final long serialVersionUID = -4657301284704548008L;

        public DecryptCodeException(int code, Object data, String message) {
            super(code, data, message);
        }

        public DecryptCodeException(ResponseCode code, String field) {
            super(code.getDecryptCodeCode(), field, code.getDecryptCodeMessage());
        }

        public DecryptCodeException(ResponseCode code, String field, String message) {
            super(code.getDecryptCodeCode(), field, message);
        }
    }

    public static class EncryptCodeException extends HdrException {
        private static final long serialVersionUID = -4657301284704548008L;

        public EncryptCodeException(int code, Object data, String message) {
            super(code, data, message);
        }

        public EncryptCodeException(ResponseCode code, String field) {
            super(code.getEncryptCodeCode(), field, code.getEncryptCodeMessage());
        }

        public EncryptCodeException(ResponseCode code, String field, String message) {
            super(code.getEncryptCodeCode(), field, message);
        }
    }

    public static class NotAvailableEntityException extends HdrException {
        private static final long serialVersionUID = -2085391098403067857L;

        public NotAvailableEntityException(int code, Object data, String message) {
            super(code, data, message);
        }

        public NotAvailableEntityException(ResponseCode code, String message) {
            super(code.getNotAvailableEntityCode(), message);
        }

        public NotAvailableEntityException(ResponseCode code, String field, String message) {
            super(code.getNotAvailableEntityCode(), field, message);
        }
    }

    public static class UsedEntityException extends HdrException {
        private static final long serialVersionUID = -4657301284704548008L;

        public UsedEntityException(int code, Object data, String message) {
            super(code, data, message);
        }

        public UsedEntityException(ResponseCode code, String field) {
            super(code.getUsedEntityCode(), field, code.getUsedEntityMessage());
        }

        public UsedEntityException(ResponseCode code, String field, String message) {
            super(code.getUsedEntityCode(), field, message);
        }
    }

    public static class LockedEntityException extends HdrException {
        private static final long serialVersionUID = -4657301284704548008L;

        public LockedEntityException(int code, Object data, String message) {
            super(code, data, message);
        }

        public LockedEntityException(ResponseCode code, String field) {
            super(code.getLockedEntityCode(), field, code.getLockedEntityMessage());
        }

        public LockedEntityException(ResponseCode code, String field, String message) {
            super(code.getLockedEntityCode(), field, message);
        }
    }

    public static class DuplicatedEntityException extends HdrException {
        private static final long serialVersionUID = 6685802703164214179L;

        public DuplicatedEntityException(int code, Object data, String message) {
            super(code, data, message);
        }

        public DuplicatedEntityException(ResponseCode code, String field) {
            super(code.getDuplicatedEntityCode(), field, code.getDuplicatedEntityMessage());
        }

        public DuplicatedEntityException(ResponseCode code, String field, String message) {
            super(code.getDuplicatedEntityCode(), field, message);
        }
    }

    public static class NotFoundEntityException extends HdrException {
        private static final long serialVersionUID = -4657301284704548008L;

        public NotFoundEntityException(int code, Object data, String message) {
            super(code, data, message);
        }

        public NotFoundEntityException(ResponseCode code, String field) {
            super(code.getNotFoundEntityCode(), field, code.getNotFoundEntityMessage());
        }

        public NotFoundEntityException(ResponseCode code, String field, String message) {
            super(code.getNotFoundEntityCode(), field, message);
        }
    }

    public static class ExpiredEntityException extends HdrException {
        private static final long serialVersionUID = -4657301284704548008L;

        public ExpiredEntityException(int code, Object data, String message) {
            super(code, data, message);
        }

        public ExpiredEntityException(ResponseCode code, String field) {
            super(code.getExpiredEntityCode(), field, code.getExpiredEntityMessage());
        }

        public ExpiredEntityException(ResponseCode code, String field, String message) {
            super(code.getExpiredEntityCode(), field, message);
        }
    }

    public static class NotActiveEntityException extends HdrException {
        private static final long serialVersionUID = -4657301284704548008L;

        public NotActiveEntityException(int code, String field, String message) {
            super(code, field, message);
        }

        public NotActiveEntityException(ResponseCode code, String field, String message) {
            super(code.getNotActiveEntityCode(), field, message);
        }

        public NotActiveEntityException(ResponseCode code, String field) {
            super(code.getNotActiveEntityCode(), field, code.getNotActiveEntityMessage());
        }
    }

    public static class InvalidInputException extends HdrException {
        private static final long serialVersionUID = -7257749122280775815L;

        public InvalidInputException(ResponseCode code, String field, String message) {
            super(code.getInvalidInputCode(), field, message);
        }

        public InvalidInputException(int code, String field, String message) {
            super(code, field, message);
        }

        public InvalidInputException(ResponseCode code, String field) {
            super(code.getInvalidInputCode(), field, code.getInvalidInputMessage());
        }
    }

    public static class NoDataException extends HdrException {
        private static final long serialVersionUID = -7257749122280775815L;

        public NoDataException(ResponseCode code, String field, String message) {
            super(code.getNoDataCode(), field, message);
        }

        public NoDataException(int code, String field, String message) {
            super(code, field, message);
        }

        public NoDataException(ResponseCode code, String field) {
            super(code.getNoDataCode(), field, code.getNoDataMessage());
        }
    }
}

package org.example.common.solr.exception;

public interface ResponseCode {
    default int getSuccessCode() {
        return 1;
    }

    default String getSuccessMessage() {
        return "Success";
    }

    int getInvalidInputCode();

    String getInvalidInputMessage();

    int getUnknownCode();

    String getUnknownMessage();

    default int getNoDataCode() {
        return this.getInvalidInputCode();
    }

    default String getNoDataMessage() {
        return this.getInvalidInputMessage();
    }

    int getNotActiveEntityCode();

    String getNotActiveEntityMessage();

    default int getExpiredEntityCode() {
        return this.getUnknownCode();
    }

    default String getExpiredEntityMessage() {
        return this.getUnknownMessage();
    }

    int getNotFoundEntityCode();

    String getNotFoundEntityMessage();

    int getDuplicatedEntityCode();

    String getDuplicatedEntityMessage();

    int getLockedEntityCode();

    String getLockedEntityMessage();

    int getNotAvailableEntityCode();

    String getNotAvailableEntityMessage();

    default int getUsedEntityCode() {
        return this.getUnknownCode();
    }

    default String getUsedEntityMessage() {
        return this.getUnknownMessage();
    }

    default int getEncryptCodeCode() {
        return this.getUnknownCode();
    }

    default String getEncryptCodeMessage() {
        return this.getUnknownMessage();
    }

    default int getDecryptCodeCode() {
        return this.getUnknownCode();
    }

    default String getDecryptCodeMessage() {
        return this.getUnknownMessage();
    }

    default int getNumberFormatCode() {
        return this.getInvalidInputCode();
    }

    default String getNumberFormatMessage() {
        return this.getInvalidInputMessage();
    }

    default int getPhoneNumberFormatCode() {
        return this.getInvalidInputCode();
    }

    default String getPhoneNumberFormatMessage() {
        return this.getInvalidInputMessage();
    }

    default int getEmailFormatCode() {
        return this.getInvalidInputCode();
    }

    default String getEmailFormatMessage() {
        return this.getInvalidInputMessage();
    }

    default int getDateTimeFormatCode() {
        return this.getInvalidInputCode();
    }

    default String getDateTimeFormatMessage() {
        return this.getInvalidInputMessage();
    }
}


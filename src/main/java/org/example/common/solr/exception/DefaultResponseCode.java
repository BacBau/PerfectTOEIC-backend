package org.example.common.solr.exception;

public class DefaultResponseCode implements ResponseCode {
    protected int unknownCode = 99;
    protected String unknownMessage = "Unknown";
    protected int invalidInputCode;
    protected String invalidInputCodeMessage = "Invalid Input";
    protected int notActiveEntityCode;
    protected String notActiveEntityMessage = "Not Active";
    protected int notFoundEntityCode;
    protected String notFoundEntityMessage = "Not Found Data";
    protected int duplicatedEntityCode;
    protected String duplicatedEntityMessage = "Duplicated Data";
    protected int lockedEntityCode;
    protected String lockedEntityMessage = "Locked Entity";
    protected int noDataCode;
    protected String noDataMessage = "Data is Null or Empty";
    protected int expiredEntityCode;
    protected String expiredEntityMessage = "Expired Data";
    protected int usedEntityCode;
    protected String usedEntityMessage = "Used Data";
    protected int notAvailableEntityCode;
    protected String notAvailableEntityMessage = "Not Available Data";
    protected int encryptCodeCode;
    protected String encryptCodeMessage = "Encrypt Error";
    protected int decryptCodeCode;
    protected String decryptCodeMessage = "Decrypt Error";

    public DefaultResponseCode() {
    }

    public int getInvalidInputCode() {
        return this.invalidInputCode;
    }

    public String getInvalidInputMessage() {
        return this.invalidInputCodeMessage;
    }

    public int getUnknownCode() {
        return this.unknownCode;
    }

    public String getUnknownMessage() {
        return this.unknownMessage;
    }

    public int getNotActiveEntityCode() {
        return this.notActiveEntityCode;
    }

    public String getNotActiveEntityMessage() {
        return this.notActiveEntityMessage;
    }

    public int getNotFoundEntityCode() {
        return this.notFoundEntityCode;
    }

    public String getNotFoundEntityMessage() {
        return this.notFoundEntityMessage;
    }

    public int getDuplicatedEntityCode() {
        return this.duplicatedEntityCode;
    }

    public String getDuplicatedEntityMessage() {
        return this.duplicatedEntityMessage;
    }

    public int getLockedEntityCode() {
        return this.lockedEntityCode;
    }

    public String getLockedEntityMessage() {
        return this.lockedEntityMessage;
    }

    public int getNoDataCode() {
        return this.noDataCode;
    }

    public String getNoDataMessage() {
        return this.noDataMessage;
    }

    public int getExpiredEntityCode() {
        return this.expiredEntityCode;
    }

    public String getExpiredEntityMessage() {
        return this.expiredEntityMessage;
    }

    public int getUsedEntityCode() {
        return this.usedEntityCode;
    }

    public String getUsedEntityMessage() {
        return this.usedEntityMessage;
    }

    public int getNotAvailableEntityCode() {
        return this.notAvailableEntityCode;
    }

    public String getNotAvailableEntityMessage() {
        return this.notAvailableEntityMessage;
    }

    public int getEncryptCodeCode() {
        return this.encryptCodeCode;
    }

    public String getEncryptCodeMessage() {
        return this.encryptCodeMessage;
    }

    public int getDecryptCodeCode() {
        return this.decryptCodeCode;
    }

    public String getDecryptCodeMessage() {
        return this.decryptCodeMessage;
    }
}

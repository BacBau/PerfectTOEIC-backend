package org.example.common.solr.repository;

public class DataContainer<T> {
    private T value;
    private int errorTime = 0;

    public DataContainer() {
    }

    public DataContainer(T value) {
        this.value = value;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public int getErrorTime() {
        return this.errorTime;
    }

    public void setErrorTime(int errorTime) {
        this.errorTime = errorTime;
    }
}

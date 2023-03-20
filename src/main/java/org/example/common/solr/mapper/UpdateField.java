package org.example.common.solr.mapper;

import org.springframework.util.StringUtils;

public class UpdateField<T> {
    public static final String ACTION_SET = "set";
    public static final String ACTION_ADD = "add";
    private String action;
    private String name;
    private T value;
    private String id;
    private long creationTime;

    public UpdateField(String name, T value) {
        this.action = "set";
        this.creationTime = System.currentTimeMillis();
        this.name = name;
        this.value = value;
    }

    public UpdateField(String name, T value, String action) {
        this(name, value);
        this.action = action;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isLazy() {
        return !StringUtils.isEmpty(this.id);
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String toUniqueIdentifier() {
        return "set".equals(this.action) ? this.id + "_" + this.name + "_" + this.action : this.id + "_" + this.name + "_" + this.action + "_" + this.creationTime;
    }
}

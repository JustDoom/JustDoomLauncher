package com.imjustdoom.doomlauncher.justdoomlauncher.files.setting;

public class JsonSetting {

    private String key;
    private String path;
    private Object value;
    private Class clazz;
    private final boolean override;
    private boolean updated = false;

    public JsonSetting(String key, String path, Object value, Class clazz, boolean override) {
        this.key = key;
        this.path = path;
        this.value = value;
        this.clazz = clazz;
        this.override = override;
    }

    public JsonSetting(String key, String path, Object value, Class clazz) {
        this(key, path, value, clazz, false);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Class getClazz() {
        return this.clazz;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof JsonSetting setting)) return false;

        return setting.getValue().equals(value)
                && setting.getKey().equals(key)
                && setting.getPath().equals(path);
    }

    public boolean isSetting(Object o) {
        if (o == this) return true;
        if (!(o instanceof JsonSetting setting)) return false;

        return setting.getKey().equals(key)
                && setting.getPath().equals(path);
    }

    public boolean isSetting(String setting, Object type) {
        return type instanceof String && key.equals(setting);
    }

    public boolean isSetting(String setting) {
        return key.equals(setting);
    }

    public boolean isOverride() {
        return this.override;
    }

    public boolean isString() {
        return value instanceof String;
    }

    public boolean isInteger() {
        return value instanceof Integer;
    }

    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    public boolean getAsBoolean() {
        return (boolean) value;
    }

    public int getAsInteger() {
        return (int) value;
    }

    public String getAsString() {
        return (String) value;
    }

    public JsonSetting copy() {
        return new JsonSetting(key, path, value, clazz, override);
    }
}

package com.kabunx.component.log;

public enum TemplateEnum {
    ADD("", "【】从【空】修改为【】"),
    DEL("", "");

    private final String name;

    private final String tpl;

    TemplateEnum(String name, String tpl) {
        this.name = name;
        this.tpl = tpl;
    }

    public String getName() {
        return name;
    }

    public String getTpl() {
        return tpl;
    }
}

package com.kabunx.component.log;

public enum TemplateEnum {
    CREATE("create", "新建了数据"),
    UPDATE("update", "修改了数据"),
    DELETE("delete", "删除了数据");

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

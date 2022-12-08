package com.kabunx.component.core.constant.enums;

public enum MenuTypeEnum implements BaseEnum<Integer> {

    NULL(0, null),
    MENU(1, "菜单"),
    CATALOG(2, "目录");

    private final Integer value;

    private final String label;

    MenuTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }


    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String getLabel() {
        return this.label;
    }
}

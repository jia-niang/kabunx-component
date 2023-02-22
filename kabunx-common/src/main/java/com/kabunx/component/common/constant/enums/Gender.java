package com.kabunx.component.common.constant.enums;

public enum Gender implements EnumInfo<Integer> {
    UNKNOWN(0, "保密"),
    MALE(1, "男"),
    FEMALE(2, "女");

    private final Integer value;

    private final String label;

    Gender(Integer value, String label) {
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

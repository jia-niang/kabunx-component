package com.kabunx.component.dev.mybatis;

import org.mybatis.generator.api.PluginAdapter;

import java.util.List;

public class LombokAnnotationPlugin extends PluginAdapter {
    @Override
    public boolean validate(List<String> list) {
        return false;
    }
}

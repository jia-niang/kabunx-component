package com.kabunx.component.dev.mybatis;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.kotlin.KotlinFile;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.util.StringUtility;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

/**
 * 自定义注解生成器
 */
public class CustomCommentGenerator implements CommentGenerator {

    private static final String COMMENT_GENERATOR_ALLOW_DATE = "allowDate";

    private static final String COMMENT_GENERATOR_ALLOW_ALL_COMMENTS = "allowAllComments";
    private final Properties properties = new Properties();

    private boolean allowDate;

    private boolean allowAllComments;

    private final boolean ignoreAllComments;

    private final boolean ignoreXmlComments;

    private final boolean ignoreJavadocTag;

    /**
     * If suppressAllComments is true, this option is ignored.
     */
    private boolean addRemarkComments;

    private SimpleDateFormat dateFormat;

    public CustomCommentGenerator() {
        super();
        allowDate = true;
        allowAllComments = true;
        ignoreAllComments = true;
        ignoreXmlComments = true;
        ignoreJavadocTag = true;
        addRemarkComments = true;
    }

    /**
     * 添加合适的注释以警告用户该元素已生成，并且生成的时间
     *
     * @param xmlElement the xml element
     */
    @Override
    public void addComment(XmlElement xmlElement) {
        if (ignoreAllComments || ignoreXmlComments) {
            return;
        }
    }

    @Override
    public void addConfigurationProperties(Properties props) {
        this.properties.putAll(props);

        allowDate = StringUtility.isTrue(properties.getProperty(COMMENT_GENERATOR_ALLOW_DATE));

        allowAllComments = StringUtility.isTrue(properties.getProperty(COMMENT_GENERATOR_ALLOW_ALL_COMMENTS));

        addRemarkComments = StringUtility.isTrue(properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_ADD_REMARK_COMMENTS));

        String dateFormatString = properties.getProperty(PropertyRegistry.COMMENT_GENERATOR_DATE_FORMAT);
        if (StringUtility.stringHasValue(dateFormatString)) {
            dateFormat = new SimpleDateFormat(dateFormatString);
        }
    }

    /**
     * 此方法为添加自定义 javadoc 标记。如果你不这样做，你可能什么都不做
     * 希望包含 Javadoc 标记 - 但是，如果您不包含 Javadoc标记，那么 eclipse 插件的 Java 合并功能将中断。
     *
     * @param javaElement       the java element
     * @param markAsDoNotDelete the mark as do not delete
     */
    protected void addJavadocTag(JavaElement javaElement, boolean markAsDoNotDelete) {
        if (ignoreJavadocTag) {
            return;
        }
        javaElement.addJavaDocLine(" *"); //$NON-NLS-1$
        StringBuilder sb = new StringBuilder();
        sb.append(" * "); //$NON-NLS-1$
        sb.append(MergeConstants.NEW_ELEMENT_TAG);
        if (markAsDoNotDelete) {
            sb.append(" 在合并期间不要删除"); //$NON-NLS-1$
        }
        String s = getDateString();
        if (s != null) {
            sb.append(' ');
            sb.append(s);
        }
        javaElement.addJavaDocLine(sb.toString());
    }

    /**
     * 返回格式化的日期字符串以包含在 Javadoc 标记和 XML 中注释。
     * 如果您不想要这些中的日期，您可以返回 null文档元素。
     *
     * @return a string representing the current timestamp, or null
     */
    protected String getDateString() {
        if (allowDate) {
            return null;
        } else if (dateFormat != null) {
            return dateFormat.format(new Date());
        } else {
            return new Date().toString();
        }
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
        if (checkIfIgnoreAllComments(innerClass, introspectedTable)) {
            return;
        }

        addJavadocTag(innerClass, false);

        innerClass.addJavaDocLine(" */"); //$NON-NLS-1$
    }

    private boolean checkIfIgnoreAllComments(InnerClass innerClass, IntrospectedTable introspectedTable) {
        if (ignoreAllComments) {
            return true;
        }

        StringBuilder sb = new StringBuilder();

        innerClass.addJavaDocLine("/**"); //$NON-NLS-1$
        innerClass.addJavaDocLine(" * 这个类是由自定义 MyBatis Generator 生成的。"); //$NON-NLS-1$

        sb.append(" * 这个类对应数据库表 "); //$NON-NLS-1$
        sb.append(introspectedTable.getFullyQualifiedTable());
        innerClass.addJavaDocLine(sb.toString());
        return false;
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        if (checkIfIgnoreAllComments(innerClass, introspectedTable)) {
            return;
        }

        addJavadocTag(innerClass, markAsDoNotDelete);

        innerClass.addJavaDocLine(" */"); //$NON-NLS-1$
    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (allowAllComments || !addRemarkComments) {
            return;
        }

        topLevelClass.addJavaDocLine("/**"); //$NON-NLS-1$

        String remarks = introspectedTable.getRemarks();
        if (addRemarkComments && StringUtility.stringHasValue(remarks)) {
            topLevelClass.addJavaDocLine(" * 数据库表备注："); //$NON-NLS-1$
            String[] remarkLines = remarks.split(System.getProperty("line.separator")); //$NON-NLS-1$
            for (String remarkLine : remarkLines) {
                topLevelClass.addJavaDocLine(" *   " + remarkLine); //$NON-NLS-1$
            }
        }
        topLevelClass.addJavaDocLine(" *"); //$NON-NLS-1$

        topLevelClass.addJavaDocLine(" * 这个类是由 MyBatis Generator 生成的。"); //$NON-NLS-1$

        String s = " * 这个类对应数据库表 " + introspectedTable.getFullyQualifiedTable();
        topLevelClass.addJavaDocLine(s);

        topLevelClass.addJavaDocLine(" */"); //$NON-NLS-1$
    }

    @Override
    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
        if (allowAllComments) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        innerEnum.addJavaDocLine("/**"); //$NON-NLS-1$
        innerEnum.addJavaDocLine(" * 这个枚举是由 MyBatis Generator 生成的。"); //$NON-NLS-1$

        sb.append(" * 这个枚举对应于数据库表 "); //$NON-NLS-1$
        sb.append(introspectedTable.getFullyQualifiedTable());
        innerEnum.addJavaDocLine(sb.toString());

        addJavadocTag(innerEnum, false);

        innerEnum.addJavaDocLine(" */"); //$NON-NLS-1$
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable,
                                IntrospectedColumn introspectedColumn) {
        if (allowAllComments) {
            return;
        }

        field.addJavaDocLine("/**"); //$NON-NLS-1$

        String remarks = introspectedColumn.getRemarks();
        if (addRemarkComments && StringUtility.stringHasValue(remarks)) {
            String[] remarkLines = remarks.split(System.getProperty("line.separator")); //$NON-NLS-1$
            for (String remarkLine : remarkLines) {
                field.addJavaDocLine(" * " + remarkLine); //$NON-NLS-1$
            }
        }

//        addJavadocTag(field, false);

        field.addJavaDocLine(" */"); //$NON-NLS-1$
    }

    /**
     * 添加
     *
     * @param field             the field
     * @param introspectedTable the introspected table
     */
    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
        if (ignoreAllComments) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        field.addJavaDocLine("/**"); //$NON-NLS-1$
        field.addJavaDocLine(" * 该字段由 MyBatis Generator 生成。"); //$NON-NLS-1$

        sb.append(" * 该字段对应数据库表 "); //$NON-NLS-1$
        sb.append(introspectedTable.getFullyQualifiedTable());
        field.addJavaDocLine(sb.toString());

        addJavadocTag(field, false);

        field.addJavaDocLine(" */"); //$NON-NLS-1$
    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        if (allowAllComments) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        method.addJavaDocLine("/**"); //$NON-NLS-1$
        method.addJavaDocLine(" * 该方法由 MyBatis Generator 生成。"); //$NON-NLS-1$

        sb.append(" * 该方法对应数据库表 "); //$NON-NLS-1$
        sb.append(introspectedTable.getFullyQualifiedTable());
        method.addJavaDocLine(sb.toString());

        addJavadocTag(method, false);

        method.addJavaDocLine(" */"); //$NON-NLS-1$
    }

    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        if (ignoreAllComments) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        method.addJavaDocLine("/**"); //$NON-NLS-1$
        method.addJavaDocLine(" * 该方法由 MyBatis Generator 生成。"); //$NON-NLS-1$

        sb.append(" * 此方法返回数据库列的值 "); //$NON-NLS-1$
        sb.append(introspectedTable.getFullyQualifiedTable());
        sb.append('.');
        sb.append(introspectedColumn.getActualColumnName());
        method.addJavaDocLine(sb.toString());

        method.addJavaDocLine(" *"); //$NON-NLS-1$

        sb.setLength(0);
        sb.append(" * @return the value of "); //$NON-NLS-1$
        sb.append(introspectedTable.getFullyQualifiedTable());
        sb.append('.');
        sb.append(introspectedColumn.getActualColumnName());
        method.addJavaDocLine(sb.toString());

        addJavadocTag(method, false);

        method.addJavaDocLine(" */"); //$NON-NLS-1$
    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
        if (allowAllComments) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        method.addJavaDocLine("/**"); //$NON-NLS-1$
        method.addJavaDocLine(" * 该方法由 MyBatis Generator 生成。"); //$NON-NLS-1$

        sb.append(" * 此方法设置数据库列的值 "); //$NON-NLS-1$
        sb.append(introspectedTable.getFullyQualifiedTable());
        sb.append('.');
        sb.append(introspectedColumn.getActualColumnName());
        method.addJavaDocLine(sb.toString());

        method.addJavaDocLine(" *"); //$NON-NLS-1$

        Parameter param = method.getParameters().get(0);
        sb.setLength(0);
        sb.append(" * @param "); //$NON-NLS-1$
        sb.append(param.getName());
        sb.append(" the value for "); //$NON-NLS-1$
        sb.append(introspectedTable.getFullyQualifiedTable());
        sb.append('.');
        sb.append(introspectedColumn.getActualColumnName());
        method.addJavaDocLine(sb.toString());

        addJavadocTag(method, false);

        method.addJavaDocLine(" */"); //$NON-NLS-1$
    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable,
                                           Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated")); //$NON-NLS-1$
        String comment = "Source Table: " + introspectedTable.getFullyQualifiedTable().toString(); //$NON-NLS-1$
        method.addAnnotation(getGeneratedAnnotation(comment));
    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable,
                                           IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated")); //$NON-NLS-1$
        String comment = "Source field: " //$NON-NLS-1$
                + introspectedTable.getFullyQualifiedTable().toString() + "." //$NON-NLS-1$
                + introspectedColumn.getActualColumnName();
        method.addAnnotation(getGeneratedAnnotation(comment));
    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable,
                                   Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated")); //$NON-NLS-1$
        String comment = "Source Table: " + introspectedTable.getFullyQualifiedTable().toString(); //$NON-NLS-1$
        field.addAnnotation(getGeneratedAnnotation(comment));
    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable,
                                   IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated")); //$NON-NLS-1$
        String comment = "Source field: " //$NON-NLS-1$
                + introspectedTable.getFullyQualifiedTable().toString() + "." //$NON-NLS-1$
                + introspectedColumn.getActualColumnName();
        field.addAnnotation(getGeneratedAnnotation(comment));

        if (allowAllComments && addRemarkComments) {
            String remarks = introspectedColumn.getRemarks();
            if (addRemarkComments && StringUtility.stringHasValue(remarks)) {
                field.addJavaDocLine("/**"); //$NON-NLS-1$
                field.addJavaDocLine(" * 数据库列备注："); //$NON-NLS-1$
                String[] remarkLines = remarks.split(System.getProperty("line.separator")); //$NON-NLS-1$
                for (String remarkLine : remarkLines) {
                    field.addJavaDocLine(" *   " + remarkLine); //$NON-NLS-1$
                }
                field.addJavaDocLine(" */"); //$NON-NLS-1$
            }
        }
    }

    @Override
    public void addClassAnnotation(InnerClass innerClass, IntrospectedTable introspectedTable,
                                   Set<FullyQualifiedJavaType> imports) {
        imports.add(new FullyQualifiedJavaType("javax.annotation.Generated")); //$NON-NLS-1$
        String comment = "Source Table: " + introspectedTable.getFullyQualifiedTable().toString(); //$NON-NLS-1$
        innerClass.addAnnotation(getGeneratedAnnotation(comment));
    }

    private String getGeneratedAnnotation(String comment) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("@Generated("); //$NON-NLS-1$
        if (allowAllComments) {
            buffer.append('\"');
        } else {
            buffer.append("value=\""); //$NON-NLS-1$
        }

        buffer.append(MyBatisGenerator.class.getName());
        buffer.append('\"');

        if (!allowDate && !allowAllComments) {
            buffer.append(", date=\""); //$NON-NLS-1$
            buffer.append(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.now()));
            buffer.append('\"');
        }

        if (!allowAllComments) {
            buffer.append(", comments=\""); //$NON-NLS-1$
            buffer.append(comment);
            buffer.append('\"');
        }

        buffer.append(')');
        return buffer.toString();
    }

    @Override
    public void addFileComment(KotlinFile kotlinFile) {
        if (!allowAllComments) {
            return;
        }

        kotlinFile.addFileCommentLine("/*"); //$NON-NLS-1$
        kotlinFile.addFileCommentLine(" * 自动生成的文件。"); //$NON-NLS-1$
        if (!allowDate) {
            kotlinFile.addFileCommentLine(" * 生成日期：" //$NON-NLS-1$
                    + DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.now()));
        }
        kotlinFile.addFileCommentLine(" */"); //$NON-NLS-1$
    }
}

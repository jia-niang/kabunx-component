package com.kabunx.component.log;

import com.kabunx.component.log.annotation.LogRecord;
import com.kabunx.component.log.dto.LogRecordOperation;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class LogRecordOperationSource {
    public Collection<LogRecordOperation> computeLogRecordOperations(Method method, Class<?> targetClass) {
        // Don't allow no-public methods as required.
        if (!Modifier.isPublic(method.getModifiers())) {
            return Collections.emptyList();
        }

        // The method may be on an interface, but we need attributes from the target class.
        // If the target class is null, the method will be unchanged.
        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
        // If we are dealing with method with generic parameters, find the original method.
        specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);

        // First try is the method in the target class.
        Collection<LogRecordOperation> operations = parseLogRecordAnnotations(specificMethod);
        Collection<LogRecordOperation> operationCollection = parseLogRecordAnnotations(ClassUtils.getInterfaceMethodIfPossible(method));
        HashSet<LogRecordOperation> result = new HashSet<>();
        result.addAll(operations);
        result.addAll(operationCollection);
        return result;
    }

    private Collection<LogRecordOperation> parseLogRecordAnnotations(AnnotatedElement ae) {
        Collection<LogRecord> logRecordAnnotationAnnotations = AnnotatedElementUtils.findAllMergedAnnotations(ae, LogRecord.class);
        Collection<LogRecordOperation> operations = new ArrayList<>();
        if (!logRecordAnnotationAnnotations.isEmpty()) {
            for (LogRecord recordAnnotation : logRecordAnnotationAnnotations) {
                operations.add(parseLogRecordAnnotation(ae, recordAnnotation));
            }
        }
        return operations;
    }

    private LogRecordOperation parseLogRecordAnnotation(AnnotatedElement ae, LogRecord logRecord) {
        LogRecordOperation operation = LogRecordOperation.builder()
                .successTemplate(logRecord.success())
                .failTemplate(logRecord.fail())
                .bizNo(logRecord.bizNo())
                .operator(logRecord.operator())
                .condition(logRecord.condition())
                .build();
        validateLogRecordOperation(ae, operation);
        return operation;
    }


    private void validateLogRecordOperation(AnnotatedElement ae, LogRecordOperation operation) {
        if (!StringUtils.hasText(operation.getSuccessTemplate()) && !StringUtils.hasText(operation.getFailTemplate())) {
            throw new IllegalStateException("Invalid logRecord annotation configuration on '" +
                    ae.toString() + "'. 'one of successTemplate and failLogTemplate' attribute must be set.");
        }
    }
}

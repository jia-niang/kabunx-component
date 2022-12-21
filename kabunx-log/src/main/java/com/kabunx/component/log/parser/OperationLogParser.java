package com.kabunx.component.log.parser;

import com.kabunx.component.log.annotation.OperationLog;
import com.kabunx.component.log.dto.OperationLogMetadata;
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

public class OperationLogParser {
    public Collection<OperationLogMetadata> buildLogRecordOperations(Method method, Class<?> targetClass) {
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
        Collection<OperationLogMetadata> operations = parseLogRecordAnnotations(specificMethod);
        Collection<OperationLogMetadata> operationCollection = parseLogRecordAnnotations(ClassUtils.getInterfaceMethodIfPossible(method));
        HashSet<OperationLogMetadata> result = new HashSet<>();
        result.addAll(operations);
        result.addAll(operationCollection);
        return result;
    }

    private Collection<OperationLogMetadata> parseLogRecordAnnotations(AnnotatedElement ae) {
        Collection<OperationLog> operationLogAnnotationAnnotations = AnnotatedElementUtils.findAllMergedAnnotations(ae, OperationLog.class);
        Collection<OperationLogMetadata> operations = new ArrayList<>();
        if (!operationLogAnnotationAnnotations.isEmpty()) {
            for (OperationLog recordAnnotation : operationLogAnnotationAnnotations) {
                operations.add(parseLogRecordAnnotation(ae, recordAnnotation));
            }
        }
        return operations;
    }

    private OperationLogMetadata parseLogRecordAnnotation(AnnotatedElement ae, OperationLog operationLog) {
        OperationLogMetadata operation = OperationLogMetadata.builder()
                .successTemplate(operationLog.success())
                .failTemplate(operationLog.fail())
                .bizNo(operationLog.bizNo())
                .operator(operationLog.operator())
                .condition(operationLog.condition())
                .build();
        validateLogRecordOperation(ae, operation);
        return operation;
    }


    private void validateLogRecordOperation(AnnotatedElement ae, OperationLogMetadata operation) {
        if (!StringUtils.hasText(operation.getSuccessTemplate()) && !StringUtils.hasText(operation.getFailTemplate())) {
            throw new IllegalStateException("Invalid logRecord annotation configuration on '" +
                    ae.toString() + "'. 'one of successTemplate and failLogTemplate' attribute must be set.");
        }
    }
}

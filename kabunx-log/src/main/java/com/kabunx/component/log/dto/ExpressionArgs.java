package com.kabunx.component.log.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;

@Data
@AllArgsConstructor
public class ExpressionArgs {
    private final EvaluationContext context;
    private final AnnotatedElementKey methodKey;
}

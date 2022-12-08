package com.kabunx.component.web.util;

import org.springframework.validation.BindException;
import org.springframework.validation.MapBindingResult;

import javax.validation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 通用验证器工具类
 */
public class ValidatorUtils {
    private static final Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public static Validator getValidator() {
        return validator;
    }

    public static <T> void validate(T t, String objName) throws BindException {
        Set<ConstraintViolation<T>> violations = validator.validate(t);
        if (violations.size() > 0) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<T> violation : violations) {
                errors.put(violation.getLeafBean().toString(), violation.getMessage());
            }
            throw new BindException(new MapBindingResult(errors, objName));
        }
    }
}

package org.smart4j.framework.annotation;

import java.lang.annotation.*;

/**
 * Aspect方法 注解
 *
 * @author wang yi zhe
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {
    /**
     * 注解
     */
    Class<? extends Annotation> value();
}

package com.maxchen.trubbo.spring.annotation;

import com.maxchen.trubbo.spring.TrubboInitializer;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import(TrubboInitializer.class)
public @interface EnableTrubbo {
}

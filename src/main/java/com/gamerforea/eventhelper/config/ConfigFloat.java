package com.gamerforea.eventhelper.config;

import net.minecraftforge.common.config.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigFloat
{
	String name() default "";

	String category() default Configuration.CATEGORY_GENERAL;

	String comment() default "";

	float min() default Float.MIN_VALUE;

	float max() default Float.MAX_VALUE;

	String oldName() default "";

	String oldCategory() default "";
}

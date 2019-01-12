package com.gamerforea.eventhelper.config;

import net.minecraftforge.common.config.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigItemBlockList
{
	String name() default "";

	String category() default Configuration.CATEGORY_GENERAL;

	String comment() default "";

	String oldName() default "";

	String oldCategory() default "";
}

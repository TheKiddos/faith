package org.thekiddos.faith.utils;

import org.springframework.context.ApplicationContext;

public final class ContextManager {
    private static ApplicationContext context;

    private ContextManager() {}

    /**
     * Used to get spring bean in classes that are not managed by spring boot
     * @param beanClass
     * @param <T>
     * @return
     */
    public static <T> T getBean( Class<T> beanClass ) {
        return context.getBean( beanClass );
    }

    /**
     * Must be called when spring starts
     * @param context
     */
    public static void setContext( ApplicationContext context ) {
        ContextManager.context = context;
    }
}

package com.maxchen.trubbo.common.spi;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class ExtensionLoader {
    private static final Map<Class<?>, List<String>> EXTENSION_MAP = new ConcurrentHashMap<>();
    private static final Map<String, Class<?>> ALIAS_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Object> EXTENSION_INSTANCE_MAP = new ConcurrentHashMap<>();
    private static final Set<Class<?>> LOADED_INTERFACES = new CopyOnWriteArraySet<>();

    public static void registerExtension(Class<?> interfaceClass, String name, Class<?> implClass) {
        if (!interfaceClass.isAssignableFrom(implClass)) {
            throw new IllegalArgumentException("the implClass is not a subclass of interfaceClass");
        }
        ALIAS_MAP.put(getExtensionKey(interfaceClass, name), implClass);
    }

    public static void registerExtension(Class<?> interfaceClass, Class<?> implClass) {
        if (!interfaceClass.isAssignableFrom(implClass)) {
            throw new IllegalArgumentException("the implClass is not a subclass of interfaceClass");
        }
        ALIAS_MAP.put(getExtensionKey(interfaceClass, implClass.getName()), implClass);
    }

    public static <T> T getExtension(Class<T> interfaceClass, String name) {
        if (!LOADED_INTERFACES.contains(interfaceClass)) {
            synchronized (interfaceClass) {
                if (!LOADED_INTERFACES.contains(interfaceClass)) {
                    ServiceLoader<T> load = ServiceLoader.load(interfaceClass);
                    for (T t : load) {
                        registerExtension(interfaceClass, t.getClass());
                    }
                    LOADED_INTERFACES.add(interfaceClass);
                }
            }
        }
        if (ALIAS_MAP.containsKey(getExtensionKey(interfaceClass, name))) {
            return loadExtension(interfaceClass, name);
        } else {
            throw new IllegalArgumentException("No such extension: " + name);
        }
    }

    private static <T> T loadExtension(Class<T> interfaceClass, String name) {
        Class<?> clazz = ALIAS_MAP.get(getExtensionKey(interfaceClass, name));
        Object o = EXTENSION_INSTANCE_MAP.computeIfAbsent(clazz, k -> {
            try {
                return k.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException
                     | InvocationTargetException | NoSuchMethodException e) {
                throw new IllegalArgumentException("the Extension cannot be instanced properly");
            }
        });
        return (T) o;
    }

    private static String getExtensionKey(Class<?> interfaceClass, String name) {
        return interfaceClass.getName() + ":" + name;
    }
}

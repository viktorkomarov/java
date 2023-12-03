package ru.otus.appcontainer;

import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final Map<Class<?>, List<Object>> appComponents = new HashMap<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    private void processConfig(Class<?> configClass){
        checkConfigClass(configClass);
        checkUniqueNames(configClass);

        try {
            var configEntity = configClass.getDeclaredConstructor().newInstance();
            Queue<Method> byOrder = new PriorityQueue<>(Comparator.comparingInt(
                    m->m.getAnnotation(AppComponent.class).order()
            ));
            for (var method :  configClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(AppComponent.class)) {
                    byOrder.add(method);
                }
            }
            while(!byOrder.isEmpty()) {
                var method = byOrder.remove();
                List<Object>args = new ArrayList<>();
                for (var dep: method.getParameterTypes()) {
                    args.add(appComponents.get(dep).get(0));
                }
                var out = method.invoke(configEntity, args.toArray());
                appComponentsByName.put(method.getName(), out);
                var tmp =  appComponents.getOrDefault(method.getReturnType(), new ArrayList<>());
                tmp.add(out);
                appComponents.put(method.getReturnType(),tmp);
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("invalid format");
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    private void checkUniqueNames(Class<?> configClass) {
        Set<String> unique = new HashSet<>();
        for(var method : configClass.getDeclaredMethods()) {
            var metaInfo = method.getAnnotation(AppComponent.class);
            if (metaInfo != null){
                if (unique.contains(metaInfo.name())) {
                    throw new IllegalArgumentException(String.format("%s is not unique name", metaInfo.name()));
                }
                unique.add(metaInfo.name());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private<C> C mustBeSingle(List<Object> objects) {
        if (objects.size() != 1) {
            throw new NoSuchElementException();
        }
        return (C)objects.get(0);
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        if (componentClass.isInterface()) {
            return mustBeSingle(appComponents.get(componentClass));
        }
        for(var clazz : appComponents.keySet()) {
            for (var implementedInterfaces : componentClass.getInterfaces()) {
                if (implementedInterfaces.isAssignableFrom(clazz)) {
                    return mustBeSingle(appComponents.get(clazz));
                }
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C getAppComponent(String componentName) {
        if (appComponentsByName.containsKey(componentName)) {
            return (C)appComponentsByName.get(componentName);
        }
        throw new NoSuchElementException();
    }
}

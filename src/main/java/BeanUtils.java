import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BeanUtils {
    /**
     * Scans object "from" for all getters. If object "to"
     * contains correspondent setter, it will invoke it
     * to set property value for "to" which equals to the property
     * of "from".
     * <p>
     * The type in setter should be compatible to the value returned
     * by getter (if not, no invocation performed).
     * Compatible means that parameter type in setter should
     * be the same or be superclass of the return type of the getter.
     * <p>
     * The method takes care only about public methods.
     *
     * @param to   Object which properties will be set.
     * @param from Object which properties will be used to get values.
     */
    public static void assign(Object to, Object from) {
        if (to == null || from == null) {
            throw new IllegalArgumentException("Input arguments shouldn't be null");
        }
        Function<Method, Boolean> setterCondition = method ->
                method.getReturnType() == void.class
                && method.getParameterCount() == 1
                && method.getName().matches("^set[A-Za-z]*");

        Function<Method, Boolean> getterCondition = method ->
                method.getReturnType() != void.class
                && method.getParameterCount() == 0
                && method.getName().matches("^get[A-Za-z]*");

        BiFunction<Object, Function<Method,Boolean>, Map<String, Method>> getFuncWithPatternName = (o, methodPattern) -> {
            Map<String, Method> methods = new HashMap<>();
            Class<?> clazz = o.getClass();
            while (clazz!=null) {
                for (Method method : clazz.getMethods()) {
                    if (methodPattern.apply(method)) {
                        methods.put(method.getName().substring(3),method);
                    }
                }
                clazz = clazz.getSuperclass();
            }
            return methods;
        };
        Map<String, Method> toSetterMethods = getFuncWithPatternName.apply(to, setterCondition);
        Map<String, Method> fromGetterMethods = getFuncWithPatternName.apply(from, getterCondition);

        List<String> getAndSet = toSetterMethods.keySet()
                .stream()
                .filter(fromGetterMethods::containsKey) // Оставили только те гетеры и сетеры, которы совпадают по имени
                .filter(s -> toSetterMethods.get(s).getParameterTypes()[0] // Убежадемся в совместимости типов
                        .isAssignableFrom(fromGetterMethods.get(s).getReturnType()))
                .collect(Collectors.toList());
        for (String methodName : getAndSet) {
            try {
                Object get;
                get = fromGetterMethods.get(methodName).invoke(from);
                toSetterMethods.get(methodName).invoke(to,get);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new RuntimeException("IllegalAccessException ",e);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
                throw new RuntimeException("InvocationTargetException ",e);
            }
        }
    }
}
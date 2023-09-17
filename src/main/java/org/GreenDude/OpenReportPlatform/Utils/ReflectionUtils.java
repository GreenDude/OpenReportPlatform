package org.GreenDude.OpenReportPlatform.Utils;

import org.GreenDude.OpenReportPlatform.Utils.CustomDataTypes.ExcelField;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectionUtils {

    public Package getObjectPackage(Object obj){
//        String packageName = obj.getClass().getPackage().getName().concat(".Models");
//        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(ClassLoader.getSystemClassLoader()
//                .getResourceAsStream(packageName.replaceAll("[.]", "/")))));
//        Set<Class> bob = reader.lines()
//                .filter(line -> line.endsWith(".class"))
//                .map(line -> getClass(line, packageName))
//                .collect(Collectors.toSet());
        return obj.getClass().getPackage();
    }

    public void ss(Package pack, String goal){
        Arrays.stream(Package.getPackages()).filter(
                x->x.getName().startsWith(pack.getName()) && x.getName().contains(goal)).findFirst().orElseThrow();
    }

    private Class getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            // handle the exception
        }
        return null;
    }

    public Object generateObjectInstance(String className, List<ExcelField> excelFields) {
        Class<?> extractedClass = null;
        try {
            extractedClass = Class.forName(className);
            Constructor<?>[] constructors = extractedClass.getDeclaredConstructors();
            TreeMap<Integer, List<Constructor<?>>> map = new TreeMap<>();
            for (Constructor<?> constructor : constructors) {
                map.computeIfAbsent(constructor.getParameterCount(), k -> new ArrayList<>()).add(constructor);
            }
            Constructor<?> constructor = map.lastEntry().getValue().stream().findFirst().orElseThrow();
            Object[] parameters = enrichParams(constructor.getParameters(), excelFields);

            return constructor.newInstance(parameters);

        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private Object[] enrichParams(Parameter[] params, List<ExcelField> excelFields){
        List<Parameter> parameterList = Arrays.stream(params).toList();
        List<Object> objects = new ArrayList<>();
        excelFields.forEach(excelField -> objects.add(excelField.getValue()));

        return objects.toArray();
    }
}

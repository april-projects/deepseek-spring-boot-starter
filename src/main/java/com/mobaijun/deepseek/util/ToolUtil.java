package com.mobaijun.deepseek.util;

import cn.hutool.json.JSONUtil;
import com.mobaijun.deepseek.annotation.FunctionCall;
import com.mobaijun.deepseek.annotation.FunctionParameter;
import com.mobaijun.deepseek.annotation.FunctionRequest;
import com.mobaijun.deepseek.tool.Tool;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 * Description: [工具类，提供工具调用的反射功能，包括获取函数信息、调用工具函数等。]
 * Author: [mobaijun]
 * Date: [2025/2/6 14:45]
 * IntelliJ IDEA Version: [IntelliJ IDEA 2023.1.4]
 */
@Slf4j
public class ToolUtil {

    /**
     * 存储函数实体的缓存
     */
    public static Map<String, Tool> toolEntityMap = new ConcurrentHashMap<>();
    /**
     * 存储函数类的缓存
     */
    public static Map<String, Class<?>> toolClassMap = new ConcurrentHashMap<>();
    /**
     * 存储函数请求类的缓存
     */
    public static Map<String, Class<?>> toolRequestMap = new ConcurrentHashMap<>();
    /**
     * 用于获取反射信息的工具类
     */
    static Reflections reflections = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.forPackage(""))
            .setScanners(Scanners.TypesAnnotated));

    /**
     * 调用指定的函数，并传入参数进行执行。
     *
     * @param functionName 函数名称
     * @param argument     函数参数（JSON 格式字符串）
     * @return 函数执行后的响应结果
     */
    public static String invoke(String functionName, String argument) {
        long currentTimeMillis = System.currentTimeMillis();

        // 获取函数类和函数请求类
        Class<?> functionClass = toolClassMap.get(functionName);
        Class<?> functionRequestClass = toolRequestMap.get(functionName);

        log.info("tool call function {}, argument {}", functionName, argument);

        try {
            // 获取调用函数的方法
            Method apply = functionClass.getMethod("apply", functionRequestClass);

            // 解析参数
            Object arg = JSONUtil.toBean(argument, functionRequestClass);

            // 调用函数
            Object invoke = apply.invoke(functionClass.getDeclaredConstructor().newInstance(), arg);

            // 将返回结果转为JSON字符串
            String response = JSONUtil.toJsonStr(invoke);
            log.info("response {}, cost {} ms", response, System.currentTimeMillis() - currentTimeMillis);
            return response;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取所有函数工具对象。
     *
     * @param functionList 函数名称列表
     * @return 工具对象列表
     */
    public static List<Tool> getAllFunctionTools(List<String> functionList) {
        List<Tool> tools = new ArrayList<>();
        for (String functionName : functionList) {
            Tool tool = toolEntityMap.get(functionName);
            if (tool == null) {
                tool = getToolEntity(functionName);
            }
            if (tool != null) {
                toolEntityMap.put(functionName, tool);
                tools.add(tool);
            }
        }
        return !tools.isEmpty() ? tools : null;
    }

    /**
     * 获取工具实体对象。
     *
     * @param functionName 函数名称
     * @return 工具实体对象
     */
    public static Tool getToolEntity(String functionName) {
        Tool.Function functionEntity = getFunctionEntity(functionName);
        if (functionEntity != null) {
            Tool tool = new Tool();
            tool.setType("function");
            tool.setFunction(functionEntity);
            return tool;
        }

        return null;
    }

    /**
     * 获取函数实体对象。
     *
     * @param functionName 函数名称
     * @return 函数对象
     */
    public static Tool.Function getFunctionEntity(String functionName) {
        // 获取所有标注了 @FunctionCall 注解的函数类
        Set<Class<?>> functionSet = reflections.getTypesAnnotatedWith(FunctionCall.class);

        for (Class<?> functionClass : functionSet) {
            FunctionCall functionCall = functionClass.getAnnotation(FunctionCall.class);
            String currentFunctionName = functionCall.name();
            if (currentFunctionName.equals(functionName)) {
                Tool.Function function = new Tool.Function();
                function.setName(currentFunctionName);
                function.setDescription(functionCall.description());
                setFunctionParameters(function, functionClass);

                toolClassMap.put(functionName, functionClass);
                return function;
            }
        }
        return null;
    }

    /**
     * 设置函数参数信息，包括参数的类型、描述以及必填项。
     *
     * @param function      函数对象
     * @param functionClass 函数类
     */
    private static void setFunctionParameters(Tool.Function function, Class<?> functionClass) {
        Class<?>[] classes = functionClass.getDeclaredClasses();
        Map<String, Tool.Function.Property> parameters = new HashMap<>();
        List<String> requiredParameters = new ArrayList<>();

        for (Class<?> clazz : classes) {
            FunctionRequest request = clazz.getAnnotation(FunctionRequest.class);
            if (request == null) continue;
            toolRequestMap.put(function.getName(), clazz);

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                FunctionParameter parameter = field.getAnnotation(FunctionParameter.class);
                if (parameter == null) continue;
                Class<?> fieldType = field.getType();
                String jsonType = mapJavaTypeToJsonSchemaType(fieldType);
                Tool.Function.Property property = new Tool.Function.Property();
                property.setType(jsonType);
                property.setDescription(parameter.description());
                if (fieldType.isEnum()) {
                    property.setEnumValues(getEnumValues(fieldType));
                }

                parameters.put(field.getName(), property);
                if (parameter.required()) {
                    requiredParameters.add(field.getName());
                }
            }
        }
        Tool.Function.Parameter parameter = new Tool.Function.Parameter("object", parameters, requiredParameters);
        function.setParameters(parameter);
    }

    /**
     * 将Java类型映射到JSON Schema数据类型。
     *
     * @param fieldType Java字段类型
     * @return JSON Schema数据类型
     */
    private static String mapJavaTypeToJsonSchemaType(Class<?> fieldType) {
        if (fieldType.isEnum()) {
            return "string";
        } else if (fieldType.equals(String.class)) {
            return "string";
        } else if (fieldType.equals(int.class) || fieldType.equals(Integer.class) ||
                fieldType.equals(long.class) || fieldType.equals(Long.class) ||
                fieldType.equals(short.class) || fieldType.equals(Short.class) ||
                fieldType.equals(float.class) || fieldType.equals(Float.class) ||
                fieldType.equals(double.class) || fieldType.equals(Double.class)) {
            return "number";
        } else if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)) {
            return "boolean";
        } else if (fieldType.isArray()) {
            return "array";
        } else if (Collection.class.isAssignableFrom(fieldType)) {
            return "array";
        } else if (Map.class.isAssignableFrom(fieldType)) {
            return "object";
        } else {
            return "object";
        }
    }

    /**
     * 获取枚举类型的所有可能值。
     *
     * @param enumType 枚举类型
     * @return 枚举值列表
     */
    private static List<String> getEnumValues(Class<?> enumType) {
        List<String> enumValues = new ArrayList<>();
        for (Object enumConstant : enumType.getEnumConstants()) {
            enumValues.add(enumConstant.toString());
        }
        return enumValues;
    }
}

package cn.feiliu.taskflow.toolkit.mail;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.schema.JSONSchema;
import com.alibaba.fastjson2.schema.ValidateResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.victools.jsonschema.generator.*;
import com.github.victools.jsonschema.module.javax.validation.JavaxValidationModule;
import com.google.common.collect.Lists;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-11-18
 */
public class MySchemaTest {
    public static String getSchemaJson(Class<?> mainType) {
        SchemaGeneratorConfigBuilder configBuilder = new SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2020_12, OptionPreset.PLAIN_JSON);
        //自定义模块以设置必需字段
        configBuilder.forFields().withRequiredCheck((field) -> field.getName().equals("order") ? false : true);
        //自定义模块以设置字段描述
        configBuilder.forFields().withDescriptionResolver((field) -> field.getName() + "自定义描述");
        // 自定义模块以设置枚举值
        configBuilder.forFields().withEnumResolver((field) -> {
            if (field.getType().isInstanceOf(MyType.class)) {
                //返回枚举常量的名称
                return Collections.singleton(field.getType().getErasedType().getEnumConstants());
            }
            return null;
        });
        configBuilder.with(new JavaxValidationModule());
        SchemaGeneratorConfig config = configBuilder.build();
        SchemaGenerator generator = new SchemaGenerator(config);
        JsonNode jsonSchema = generator.generateSchema(mainType);
        return jsonSchema.toPrettyString();
    }

    @Test
    public void testSchemaGeneration() {
        System.out.println(getSchemaJson(MyUser.class));
    }

    @Test
    public void testValidate() {
        JSONSchema schema = JSONSchema.parseSchema(getSchemaJson(MyUser.class));
        // 输出 JSON Schema
        JSONObject schemaJson = schema.toJSONObject();
        System.out.println(schemaJson.toJSONString());
        Map<String, Object> map = new HashMap<>();
        map.put("age", 32);
        map.put("email", "x");
        map.put("isActive", true);
        map.put("name", "张栈");
        map.put("type", "A");
        map.put("types", Lists.newArrayList("A"));
        ValidateResult validateResult = schema.validate(map);
        System.out.println(validateResult.getMessage());
        Assert.assertTrue(validateResult.isSuccess());
    }

    @Test
    public void testFastjson() {
        /*Fastjson 默认把基础类型（非包装类型默认定义成必须）*/
        JSONSchema schema = JSONSchema.of(MyUser.class);
        System.out.println(schema.toJSONObject().toJSONString(JSONWriter.Feature.PrettyFormat));
        Map<String, Object> map = new HashMap<>();
        map.put("age", 32);
        map.put("active", false);
        ValidateResult result = schema.validate(map);
        System.out.println("ValidateResult: " + result.getMessage());
        Assert.assertTrue(result.isSuccess());
        Assert.assertTrue(schema.isValid(map));
    }

    @Data
    public static class MyUser {
        @NotBlank
        private String name;
        @Min(18)
        @Max(32)
        private int age;
        @NotNull
        private String email;
        private boolean isActive;
        private Order order;

    }

    public static class Order {
        private Long orderId;
        private List<MyType> types;
    }

    public enum MyType {
        A, B, C
    }
}

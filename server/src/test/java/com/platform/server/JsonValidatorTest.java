package com.platform.server;

import com.fasterxml.jackson.core.JsonLocation;
import com.networknt.schema.Error;
import com.networknt.schema.InputFormat;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaLocation;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.SchemaRegistryConfig;
import com.networknt.schema.SpecificationVersion;
import com.networknt.schema.dialect.Dialects;
import com.networknt.schema.regex.JoniRegularExpressionFactory;
import com.networknt.schema.serialization.DefaultNodeReader;
import com.networknt.schema.utils.JsonNodes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonValidatorTest {

    static void main(String[] args) throws Exception {
    }

    private static void example1() {
        SchemaRegistryConfig schemaRegistryConfig = SchemaRegistryConfig.builder()
                .regularExpressionFactory(JoniRegularExpressionFactory.getInstance()).build();

        SchemaRegistry schemaRegistry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12,
                builder -> builder.schemaRegistryConfig(schemaRegistryConfig)
                        .schemaIdResolvers(schemaIdResolvers -> schemaIdResolvers
                                .mapPrefix("https://www.example.com/schema", "classpath:schema")));

        Schema schema = schemaRegistry.getSchema(SchemaLocation.of("https://www.example.com/schema/example-main.json"));
        String input = """
                {
                  "main": {
                    "common": {
                      "field": "invalidfield"
                    }
                  }
                }
                """;

        List<Error> errors = schema.validate(input, InputFormat.JSON, executionContext ->
                executionContext.executionConfig(executionConfig -> executionConfig.formatAssertionsEnabled(true)));

    }

    private static void example2() {
        SchemaRegistry schemaRegistry = SchemaRegistry.withDialect(Dialects.getDraft202012());
        Schema schema = schemaRegistry.getSchema(SchemaLocation.of(Dialects.getDraft202012().getId()));
        String input = """
                {
                  "type": "object",
                  "properties": {
                    "key": {
                      "title" : "My key",
                      "type": "invalidtype"
                    }
                  }
                }
                """;
        List<Error> errors = schema.validate(input, InputFormat.JSON, executionContext ->
                executionContext.executionConfig(executionConfig -> executionConfig.formatAssertionsEnabled(true)));
    }

    private static void example3() {
        String schemaData = """
                {
                  "$id": "https://schema/myschema",
                  "properties": {
                    "startDate": {
                      "format": "date",
                      "minLength": 6
                    }
                  }
                }
                """;
        String inputData = "{ \"startDate\": \"1\" }";
        SchemaRegistry schemaRegistry = SchemaRegistry.withDialect(Dialects.getDraft202012(),
                builder -> builder.nodeReader(DefaultNodeReader.Builder::locationAware));

        Schema schema = schemaRegistry.getSchema(schemaData, InputFormat.JSON);
        List<Error> errors = schema.validate(inputData, InputFormat.JSON, executionContext ->
                executionContext.executionConfig(executionConfig -> executionConfig.formatAssertionsEnabled(true)));
        Error format = errors.getFirst();
        JsonLocation formatInstanceNodeTokenLocation = JsonNodes.tokenStreamLocationOf(format.getInstanceNode());
        JsonLocation formatSchemaNodeTokenLocation = JsonNodes.tokenStreamLocationOf(format.getSchemaNode());
        Error minLength = errors.get(1);
        JsonLocation minLengthInstanceNodeTokenLocation = JsonNodes.tokenStreamLocationOf(minLength.getInstanceNode());
        JsonLocation minLengthSchemaNodeTokenLocation = JsonNodes.tokenStreamLocationOf(minLength.getSchemaNode());

        assertEquals("format", format.getKeyword());
        assertEquals("date", format.getSchemaNode().asText());
        assertEquals(5, formatSchemaNodeTokenLocation.getLineNr());
        assertEquals(17, formatSchemaNodeTokenLocation.getColumnNr());
        assertEquals("1", format.getInstanceNode().asText());
        assertEquals(2, formatInstanceNodeTokenLocation.getLineNr());
        assertEquals(16, formatInstanceNodeTokenLocation.getColumnNr());
        assertEquals("minLength", minLength.getKeyword());
        assertEquals("6", minLength.getSchemaNode().asText());
        assertEquals(6, minLengthSchemaNodeTokenLocation.getLineNr());
        assertEquals(20, minLengthSchemaNodeTokenLocation.getColumnNr());
        assertEquals("1", minLength.getInstanceNode().asText());
        assertEquals(2, minLengthInstanceNodeTokenLocation.getLineNr());
        assertEquals(16, minLengthInstanceNodeTokenLocation.getColumnNr());
        assertEquals(16, minLengthInstanceNodeTokenLocation.getColumnNr());
    }
}

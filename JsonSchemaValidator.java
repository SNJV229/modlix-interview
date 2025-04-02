import java.util.*;

public class JsonSchemaValidator {
    public static String validateJson(Map<String, Object> schema, Object data, String path) {
        if (!schema.containsKey("type")) {
            return "Error: Missing 'type' in schema at " + path;
        }
        
        String expectedType = (String) schema.get("type");
        switch (expectedType) {
            case "object":
                if (!(data instanceof Map)) {
                    return "Error: Expected object at " + path + ", but got " + data.getClass().getSimpleName();
                }
                
                Map<String, Object> properties = (Map<String, Object>) schema.getOrDefault("properties", new HashMap<>());
                List<String> requiredFields = (List<String>) schema.getOrDefault("required", new ArrayList<>());
                Map<String, Object> dataMap = (Map<String, Object>) data;
                
                for (String field : requiredFields) {
                    if (!dataMap.containsKey(field)) {
                        return "Error: Missing required field '" + field + "' at " + path;
                    }
                }
                
                for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                    if (properties.containsKey(entry.getKey())) {
                        String error = validateJson((Map<String, Object>) properties.get(entry.getKey()), entry.getValue(), path + "." + entry.getKey());
                        if (error != null) {
                            return error;
                        }
                    }
                }
                break;
            
            case "string":
                if (!(data instanceof String)) {
                    return "Error: Expected string at " + path + ", but got " + data.getClass().getSimpleName();
                }
                break;
                
            case "integer":
                if (!(data instanceof Integer)) {
                    return "Error: Expected integer at " + path + ", but got " + data.getClass().getSimpleName();
                }
                
                if (schema.containsKey("minimum") && (Integer) data < (Integer) schema.get("minimum")) {
                    return "Error: Value at " + path + " should be at least " + schema.get("minimum");
                }
                if (schema.containsKey("maximum") && (Integer) data > (Integer) schema.get("maximum")) {
                    return "Error: Value at " + path + " should be at most " + schema.get("maximum");
                }
                break;
                
            case "boolean":
                if (!(data instanceof Boolean)) {
                    return "Error: Expected boolean at " + path + ", but got " + data.getClass().getSimpleName();
                }
                break;
                
            case "array":
                if (!(data instanceof List)) {
                    return "Error: Expected array at " + path + ", but got " + data.getClass().getSimpleName();
                }
                
                Map<String, Object> itemSchema = (Map<String, Object>) schema.getOrDefault("items", new HashMap<>());
                List<?> dataList = (List<?>) data;
                
                for (int i = 0; i < dataList.size(); i++) {
                    String error = validateJson(itemSchema, dataList.get(i), path + "[" + i + "]");
                    if (error != null) {
                        return error;
                    }
                }
                break;
                
            default:
                return "Error: Unsupported type '" + expectedType + "' in schema at " + path;
        }
        
        return null;
    }
    
    public static void main(String[] args) {
        Map<String, Object> schema = new HashMap<>() {{
            put("type", "object");
            put("properties", Map.of(
                "name", Map.of("type", "string"),
                "age", Map.of("type", "integer", "minimum", 18)
            ));
            put("required", List.of("name", "age"));
        }};
        
        Map<String, Object> dataValid = new HashMap<>() {{
            put("name", "Alice");
            put("age", 25);
        }};
        
        Map<String, Object> dataInvalid = new HashMap<>() {{
            put("name", "Bob");
            put("age", "twenty");
        }};
        
        String result = validateJson(schema, dataValid, "root");
        System.out.println(result == null ? "Valid JSON" : result);
        
        result = validateJson(schema, dataInvalid, "root");
        System.out.println(result == null ? "Valid JSON" : result);
    }
}

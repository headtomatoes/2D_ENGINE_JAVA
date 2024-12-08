package AnhNe.Components;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ComponentsDeserializer implements JsonDeserializer<Component>, JsonSerializer<Component> {
//    @Override
//    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext Context) throws JsonParseException {
//        JsonObject jsonObject = json.getAsJsonObject();
//        String type = jsonObject.get("type").getAsString();
//        JsonElement element = jsonObject.get("properties");
//        try {
//            return Context.deserialize(element, Class.forName(type));
//        } catch (ClassNotFoundException e) {
//            throw new JsonParseException("Unknown elements type" + type ,e);
//        }
//    }

    @Override
    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // Null-safe type retrieval
        if (!jsonObject.has("type")) {
            throw new JsonParseException("Component is missing 'type' field");
        }

        String type = jsonObject.get("type").getAsString();

        try {
            Class<?> componentClass = Class.forName(type);

            // Check if the class is a subclass of Component
            if (!Component.class.isAssignableFrom(componentClass)) {
                throw new JsonParseException("Type " + type + " is not a Component");
            }

            // Determine which element to deserialize
            JsonElement elementToDeserialize = jsonObject.has("properties")
                    ? jsonObject.get("properties")
                    : jsonObject.get("data");

            if (elementToDeserialize == null) {
                // Try to deserialize the entire object if no specific properties section
                return (Component) context.deserialize(jsonObject, componentClass);
            }

            return (Component) context.deserialize(elementToDeserialize, componentClass);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown component type: " + type, e);
        }
    }

    @Override
    public JsonElement serialize(Component src, Type typeOfSrc, com.google.gson.JsonSerializationContext Context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
        result.add("data", Context.serialize(src , src.getClass()));
        return result;
    }
}

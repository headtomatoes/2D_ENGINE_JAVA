package AnhNe.Firstep;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ComponentsDeserializer implements JsonDeserializer<Component>, JsonSerializer<Component> {
    @Override
    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext Context) {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");
        try {
            return Context.deserialize(element, Class.forName(type));
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown elements type" + type ,e);
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

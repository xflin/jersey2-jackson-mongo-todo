package org.example.todo;

import java.io.IOException;
import java.lang.annotation.Annotation;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bson.types.ObjectId;

/**
 * Modified from "https://github.com/yamsellem/jongo-jersey".
 * This is to fix ObjectId JSON serialization issue.
 */
@Provider
public class JacksonMapperProvider implements ContextResolver<ObjectMapper> {
    private final ObjectMapper mapper;

    public JacksonMapperProvider() {
        mapper = createMapper();
    }

    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }

    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule("jersey").
                addSerializer(_id, _idSerializer()).
                addDeserializer(_id, _idDeserializer()));
        mapper.setAnnotationIntrospector(new ObjectIdIntrospector());
        return mapper;
    }

    private static Class<ObjectId> _id = ObjectId.class;

    private static JsonDeserializer<ObjectId> _idDeserializer() {
        return new JsonDeserializer<ObjectId>() {
            @Override
            public ObjectId deserialize(JsonParser jsonParser,
                    DeserializationContext deserializationContext)
                    throws IOException {
                return new ObjectId(jsonParser.readValueAs(String.class));
            }
        };
    }

    private static JsonSerializer<Object> _idSerializer() {
        return new JsonSerializer<Object>() {
            @Override
            public void serialize(Object obj, JsonGenerator jsonGenerator,
                    SerializerProvider serializerProvider)
                    throws IOException {
                jsonGenerator.writeString(obj == null ? null : obj.toString());
            }
        };
    }

    private static class ObjectIdIntrospector
            extends JacksonAnnotationIntrospector {
        @Override
        public boolean isAnnotationBundle(Annotation ann) {
            if(ann.annotationType().equals(
                    org.jongo.marshall.jackson.oid.ObjectId.class)) {
                return false;
            }
            return super.isAnnotationBundle(ann);
        }
    }
}

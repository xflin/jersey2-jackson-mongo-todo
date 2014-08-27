package org.example.todo;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class ToDoAppConfig extends ResourceConfig {
    public ToDoAppConfig() {
        super(JacksonFeature.class);
        packages("org.example.todo");
    }
}

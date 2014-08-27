package org.example.todo;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import java.util.List;

import static org.junit.Assert.*;

public class ToDoResourceTest extends JerseyTest {
    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return new ToDoAppConfig();
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(new JacksonFeature());
    }

    @Test
    public void testGetToDoList() {
        WebTarget target = target();
        List response = target.path("todo").request(MediaType.APPLICATION_JSON).
                get(List.class);
        assertEquals("Should response with one-item list.", 1, response.size());
    }
}

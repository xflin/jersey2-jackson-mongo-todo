package org.example.todo;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;

import java.util.List;

import static org.junit.Assert.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public class ToDoResourceTest extends JerseyTest {
    private MongoTestHelper mongoTestHelper;

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        mongoTestHelper = new MongoTestHelper();
        mongoTestHelper.setUp();
        ToDoResource.serverAddress = mongoTestHelper.getServerAddress();
        ToDoAppConfig app = new ToDoAppConfig();
        return app;
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(new JacksonFeature());
    }

    @Test
    public void testGetToDoList() {
        WebTarget target = target();
        List response = target.path("todo").request(APPLICATION_JSON).
                get(List.class);
        assertEquals("Should have 3 todo items.", 3, response.size());
    }

    @Test
    public void testGetById() {
        ToDoItem item1 = mongoTestHelper.getAdapter().findOne();
        String id = item1.get_id();

        WebTarget target = target();
        ToDoItem item2 = target.path("todo/" + id).request(APPLICATION_JSON).
                get(ToDoItem.class);
        Assert.assertEquals(id, item2.get_id());
        Assert.assertEquals(item1.getTitle(), item2.getTitle());
        Assert.assertEquals(item1.getBody(), item2.getBody());
        Assert.assertEquals(item1.isDone(), item2.isDone());
    }
}

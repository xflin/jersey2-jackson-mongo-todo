package org.example.todo;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import javax.ws.rs.core.Response.Status;

// TODO:
// + should verify http response type and status
// + should verify raw response body (instead of deserialized Java object)
// + add negative tests
public class ToDoResourceTest extends JerseyTest {
    private MongoTestHelper mongoTestHelper;

    @Override
    protected Application configure() {
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        mongoTestHelper = new MongoTestHelper();
        mongoTestHelper.setUp();
        ToDoResource.mongoServerAddress = mongoTestHelper.getServerAddress();
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
        assertEquals(id, item2.get_id());
        assertEquals(item1.getTitle(), item2.getTitle());
        assertEquals(item1.getBody(), item2.getBody());
        assertEquals(item1.isDone(), item2.isDone());
    }

    @Test
    public void testDeleteById() {
        ToDoItem item1 = mongoTestHelper.getAdapter().findOne();
        String id = item1.get_id();

        WebTarget target = target();
        Response response = target.path("todo/" + id).request().delete();
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());

        List<ToDoItem> items = mongoTestHelper.getAdapter().findAll();
        assertEquals(2, items.size());
        Set<String> ids = new HashSet<>(items.size());
        for(ToDoItem item : items) ids.add(item.get_id());
        assertFalse("To-Do item#" + id + " should have been deleted.",
                ids.contains(id));
    }

    @Test
    public void testUpdateOne() {
        ToDoItem item1 = mongoTestHelper.getAdapter().findOne();
        String id = item1.get_id();

        ToDoItem item2 = new ToDoItem("new title", item1.getBody(),
                item1.isDone());
        WebTarget target = target();
        ToDoItem item3 = target.path("todo/" + id).request(APPLICATION_JSON).
                put(Entity.entity(item2, APPLICATION_JSON), ToDoItem.class);
        assertEquals(id, item3.get_id());
        assertEquals("new title", item3.getTitle());
        assertEquals(item1.getBody(), item3.getBody());
        assertEquals(item1.isDone(), item3.isDone());
    }

    @Test
    public void testPatchOne() {
        ToDoItem item1 = mongoTestHelper.getAdapter().findOne();
        String id = item1.get_id();
        WebTarget target = target();

        ToDoItem item2 = new ToDoItem("patched title", null, null);
        ToDoItem item3 = target.path("todo/" + id + "/patch").
                request(APPLICATION_JSON).
                put(Entity.entity(item2, APPLICATION_JSON), ToDoItem.class);
        assertEquals(id, item3.get_id());
        assertEquals("patched title", item3.getTitle());
        assertEquals(item1.getBody(), item3.getBody());
        assertEquals(item1.isDone(), item3.isDone());
    }

    @Test
    public void testSaveOne() {
        String title = "finish homework";
        String body = "Finish this week's assignment before Wednesday.";
        ToDoItem item1 = new ToDoItem(title, body, true);
        WebTarget target = target();
        ToDoItem item2 = target.path("todo").request(APPLICATION_JSON).
                post(Entity.entity(item1, APPLICATION_JSON), ToDoItem.class);
        assertEquals(title, item2.getTitle());
        assertEquals(body, item2.getBody());
        assertTrue(item2.isDone());
    }

    @Test
    public void testMarkDone() {
        ToDoItem item1 =
                mongoTestHelper.getAdapter().findOneByQuery("{done:#}", false);
        String id = item1.get_id();

        WebTarget target = target();
        ToDoItem change = new ToDoItem(null, null, true);
        ToDoItem item2 = target.path("todo/" + id + "/patch").
                request(APPLICATION_JSON).
                put(Entity.entity(change, APPLICATION_JSON), ToDoItem.class);
        assertEquals(id, item2.get_id());
        assertTrue(item2.isDone());
    }
}

package org.example.todo;

import com.mongodb.ServerAddress;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.PUT;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/todo")
@Produces(APPLICATION_JSON)
@Singleton
public class ToDoResource {
    // For testing purpose. Should have been a configuration property value.
    public static ServerAddress serverAddress;

    private ToDoMongoAdapter mongo;

    public ToDoResource() {
        mongo = new ToDoMongoAdapter(serverAddress);
    }

    @GET
    public List<ToDoItem> getToDoList() {
        return mongo.findAll();
    }

    @GET
    @Path("{id: [a-f0-9]+}")
    public ToDoItem getToDoItemById(@PathParam("id") String id) {
        return mongo.findById(id);
    }

    @DELETE
    @Path("{id: [a-f0-9]+}")
    public void deleteOne(@PathParam("id") String id) {
        // TODO: Should throw NotFoundException if it doesn't exist.
        mongo.deleteOne(id);
    }

    @PUT
    @Path("{id: [a-f0-9]+}")
    @Consumes(APPLICATION_JSON)
    public ToDoItem updateOne(@PathParam("id") String id, ToDoItem item) {
        if (item == null || item.getTitle() == null) {
            throw new IllegalArgumentException(
                    "Invalid to-do item: title is required.");
        }
        if (item.get_id() == null) item.set_id(id);
        mongo.updateOne(item);
        return item;
    }

    /*
    @PUT
    @Path("{id: [a-f0-9]+}")
    @Consumes(APPLICATION_JSON)
    public ToDoItem updateOne(@PathParam("id") String id, Map<String,> item) {
    }*/
}

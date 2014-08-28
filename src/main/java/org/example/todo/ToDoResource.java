package org.example.todo;

import com.mongodb.ServerAddress;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/todo")
@Singleton
public class ToDoResource {
    // for testing purpose only
    public static ServerAddress serverAddress;

    private ToDoMongoAdapter mongo;

    public ToDoResource() {
        mongo = new ToDoMongoAdapter(serverAddress);
    }

    @GET
    @Produces(APPLICATION_JSON)
    public List<ToDoItem> getToDoList() {
        return mongo.findAll();
    }

    @GET
    @Path("{id: [a-f0-9]+}")
    @Produces(APPLICATION_JSON)
    public ToDoItem getToDoItemById(@PathParam("id") String id) {
        System.out.println("id = '" + id + "'");
        ToDoItem item = mongo.findById(id);
        System.out.println(item);
        return item;
    }
}

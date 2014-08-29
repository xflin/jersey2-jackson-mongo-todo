package org.example.todo;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
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
    public static String mongoDbUrl;

    private ToDoMongoAdapter mongo;

    public ToDoResource() {
        String url = mongoDbUrl;
        if (url == null) url = System.getenv("MONGODB_URL"); //Heroku + MongoHq
        mongo = new ToDoMongoAdapter(url);
    }

    @GET
    public List<ToDoItem> getToDoList() {
        return mongo.findAll();
    }

    // TODO: Distinguish Created(201) and OK(200).
    @POST
    @Consumes(APPLICATION_JSON)
    public ToDoItem saveOne(ToDoItem todo) {
        if (todo == null || todo.getTitle() == null) {
            throw new IllegalArgumentException(
                    "Invalid to-do item: title is required.");
        }
        mongo.saveOne(todo);
        return mongo.findByTitle(todo.getTitle()).get(0);
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
    public ToDoItem updateOrPatchOne(@PathParam("id") String id,
            ToDoItem item) {
        if (item == null || item.getTitle() == null) {
            throw new IllegalArgumentException(
                    "Invalid to-do item: title is required.");
        }
        if (item.get_id() == null) item.set_id(id);
        mongo.updateOne(item);
        return item;
    }

    // TODO: Extend jersey to support @PATCH annotation.
    @PUT
    @Path("{id: [a-f0-9]+}/patch")
    @Consumes(APPLICATION_JSON)
    public ToDoItem patchOne(@PathParam("id") String id, ToDoItem change) {
        ToDoItem item = mongo.findById(id);
        if (item == null) {
            throw new NotFoundException("Couldn't find To-Do item#" + id);
        }
        if (change.getTitle() != null) item.setTitle(change.getTitle());
        if (change.getBody() != null) item.setBody(change.getBody());

        Boolean done = change.isDone();
        if (done != null && !done.equals(item.isDone())) {
            item.setDone(change.isDone());
            // TODO: send mark done/undone message
        }

        mongo.updateOne(item);
        return item;
    }
}

package org.example.todo;

import com.mongodb.ServerAddress;
import com.twilio.sdk.TwilioRestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/todo")
@Produces(APPLICATION_JSON)
@Singleton
public class ToDoResource {
    private static Logger logger = LoggerFactory.getLogger(ToDoResource.class);

    // for unit test purpose only
    public static ServerAddress mongoServerAddress;

    private ToDoMongoAdapter mongo;
    private TwilioUtil twilio;
    private SearchService searchService;

    public ToDoResource() {
        if (mongoServerAddress != null) {
            mongo = new ToDoMongoAdapter(mongoServerAddress);
        } else {
            // Heroku + MongoHQ or local mongodb (when = null)
            String url = System.getenv("MONGODB_URL");
            mongo = new ToDoMongoAdapter(url);

            try {
                searchService = new SearchService();
            } catch (IOException ioe) {
                logger.error("Failed to initialize search client.", ioe);
            }
        }
    }

    @GET
    public List<ToDoItem> getToDoList(@QueryParam("q") String query) {
        return query == null || query.trim().equals("") ? mongo.findAll() :
                search(query);
    }

    @POST
    @Consumes(APPLICATION_JSON)
    public Response saveOne(@Context UriInfo uriInfo, ToDoItem todo) {
        if (todo == null || todo.getTitle() == null) {
            throw new BadRequestException(
                    "Invalid to-do item: title is required.");
        }
        mongo.saveOne(todo);
        ToDoItem created = mongo.findByTitle(todo.getTitle()).get(0);
        URI createdUri = uriInfo.getRequestUriBuilder().path(created.get_id()).
                build();
        logger.info("Created item URI: " + createdUri);

        searchIndex(todo);

        return Response.created(createdUri).entity(created).build();
    }

    @GET
    @Path("{id: [a-f0-9]+}")
    public ToDoItem getToDoItemById(@PathParam("id") String id) {
        ToDoItem todo = null;
        try {
            todo = mongo.findById(id);
        } catch (IllegalArgumentException iae) {
            throw new BadRequestException(iae.getMessage());
        }
        if (todo == null) throw new NotFoundException();
        return todo;
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
    public ToDoItem updateOne(@PathParam("id") String id,
            ToDoItem item) {
        if (item == null || item.getTitle() == null) {
            throw new BadRequestException(
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
        if (change.getSmsPhoneNo() != null)
            item.setSmsPhoneNo(change.getSmsPhoneNo());

        boolean sendMsg = false;
        Boolean done = change.isDone();
        if (done != null && !done.equals(item.isDone())) {
            item.setDone(done);
            sendMsg = true;
        }

        mongo.updateOne(item);

        // send mark done/undone message
        if (sendMsg) {
            String phoneNo = item.getSmsPhoneNo();
            if (twilio == null) twilio = new TwilioUtil();
            if (phoneNo != null) {
                String msg = new StringBuilder(item.getTitle()).
                        append(" has been marked as ").
                        append(item.isDone() ? "'done'" : "'undone'").
                        toString();
                try { // best effort
                    twilio.sms(phoneNo, msg);
                } catch (TwilioRestException e) {
                    logger.error("Failed to send SMS when mark done=" +
                        item.isDone() + " todo#" + item.get_id(), e);
                }
            }
        }

        return item;
    }

    private void checkSearchService() {
        if (searchService == null) {
            try {
                searchService = new SearchService();
            } catch (IOException ioe) {
                logger.trace("Failed to init search.", ioe);
            }
        }
    }

    // best effort indexing
    private void searchIndex(ToDoItem todo) {
        checkSearchService();
        if (searchService != null && todo != null) {
            try {
                searchService.index(todo);
            } catch (IOException ioe) {
                logger.error("Failed to index: + " + todo, ioe);
            }
        }
    }

    private List<ToDoItem> search(String query) {
        checkSearchService();
        if (searchService == null) {
            throw new IllegalStateException("Search service is not ready.");
        } else {
            try {
                return searchService.search(query);
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }
}

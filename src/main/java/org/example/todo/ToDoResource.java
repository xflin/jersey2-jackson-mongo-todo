package org.example.todo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/todo")
public class ToDoResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ToDoItem> getToDoList() {
        List<ToDoItem> toDoList = new ArrayList();
        toDoList.add(new ToDoItem("fix faucet",
                "Fix the leaky faucet in kitchen.", false));
        return toDoList;
    }

}

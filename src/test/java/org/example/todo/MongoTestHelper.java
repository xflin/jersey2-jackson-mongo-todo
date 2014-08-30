package org.example.todo;

import com.mongodb.ServerAddress;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.eclipse.jetty.server.Server;

import java.net.InetSocketAddress;

public class MongoTestHelper {
    private MongoServer mongoServer;
    private ServerAddress serverAddress;
    private ToDoMongoAdapter adapter;

    public void setUp() {
        mongoServer = new MongoServer(new MemoryBackend());
        InetSocketAddress socket = mongoServer.bind();
        serverAddress = new ServerAddress(socket);
        adapter = new ToDoMongoAdapter(serverAddress);

        // seed data
        adapter.deleteAll();
        addToDo("fix faucet", "Fix leaky faucet in kitchen.", false);
        addToDo("tax return",
                "File overdue 2014 tax return before IRS comes at me.", false);
        addToDo("plan vacation", "Book flight tickets for summer vacation.",
                true);
    }

    public void tearDown() {
        try {
            adapter.close();
        } finally {
            mongoServer.shutdownNow();
        }
    }

    public MongoServer getMongoServer() { return mongoServer; }

    public ServerAddress getServerAddress() { return serverAddress; }

    public ToDoMongoAdapter getAdapter() { return adapter; }

    private void addToDo(String title, String body, boolean done) {
        adapter.saveOne(new ToDoItem(title, body, done));
    }
}

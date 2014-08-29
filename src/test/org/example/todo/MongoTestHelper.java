package org.example.todo;

import com.mongodb.ServerAddress;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

import java.net.InetSocketAddress;

public class MongoTestHelper {
    private MongoServer mongoServer;
    private String mongoDbUrl;
    private ToDoMongoAdapter adapter;

    public void setUp() {
        mongoServer = new MongoServer(new MemoryBackend());
        InetSocketAddress socket = mongoServer.bind();
        mongoDbUrl = "mongodb://" + socket.getHostName() + ":" +
                socket.getPort() + "/" + MongoDbUrlParser.DEFAULT_DB;
        adapter = new ToDoMongoAdapter(mongoDbUrl);

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

    public String getMongoDbUrl() { return mongoDbUrl; }

    public ToDoMongoAdapter getAdapter() { return adapter; }

    private void addToDo(String title, String body, boolean done) {
        adapter.saveOne(new ToDoItem(title, body, done));
    }
}

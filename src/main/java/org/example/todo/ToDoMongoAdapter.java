package org.example.todo;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToDoMongoAdapter {
    private static final String COLL_NAME = "todos";

    private final MongoDbUrlParser dbUrlParser;
    private ServerAddress serverAddress;
    private String user, password, dbName;
    private MongoClient mongoClient;
    private Jongo jongo;

    // for local mongodb
    public ToDoMongoAdapter() { this((String)null); }

    // for remote (and local) mongodb such as MongoHQ
    public ToDoMongoAdapter(String dbUrl) {
        dbUrlParser = new MongoDbUrlParser(dbUrl);
        serverAddress = null;
    }

    // mostly for testing purpose
    public ToDoMongoAdapter(ServerAddress serverAddress) {
        this.serverAddress = serverAddress;
        dbUrlParser = null;
    }

    public synchronized void connect() {
        if (jongo != null) return;
        try {
            if (dbUrlParser != null) {
                serverAddress =
                        new ServerAddress(dbUrlParser.host, dbUrlParser.port);
                user = dbUrlParser.user;
                password = dbUrlParser.password;
                dbName = dbUrlParser.db;
            }
            if (dbName == null) dbName = MongoDbUrlParser.DEFAULT_DB;
            if (user != null && password != null) {
                    MongoCredential credential =
                            MongoCredential.createMongoCRCredential(
                                    user, dbName, password.toCharArray());
                    mongoClient = new MongoClient(serverAddress,
                            Arrays.asList(credential));
            } else {
                mongoClient = new MongoClient(serverAddress);
            }
            DB db = mongoClient.getDB(dbName);
            jongo = new Jongo(db);
        } catch (UnknownHostException uhe) {
            throw new RuntimeException(uhe);
        }
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }

    public List<ToDoItem> findAll() {
        MongoCursor<ToDoItem> cursor = collection().find().as(ToDoItem.class);
        return cursorToList(cursor);
    }

    public ToDoItem findOne() {
        return collection().findOne().as(ToDoItem.class);
    }

    public ToDoItem findById(String id) {
        return collection().findOne(new ObjectId(id)).as(ToDoItem.class);
    }

    public ToDoItem findOneByQuery(String query, Object... params) {
        return collection().findOne(query, params).as(ToDoItem.class);
    }

    public List<ToDoItem> findByTitle(String title) {
        MongoCursor<ToDoItem> cursor =
                collection().find("{title:#}", title).as(ToDoItem.class);
        return cursorToList(cursor);
    }

    public void deleteOne(String id) {
        collection().remove(new ObjectId(id));
    }

    public void deleteMatched(String query, Object... params) {
        collection().remove(query, params);
    }

    public void deleteAll() {
        collection().remove();
    }

    public Object saveOne(ToDoItem item) {
        return collection().save(item).getUpsertedId();
    }

    public void updateOne(String id, String modifier, Object... params) {
        collection().update(new ObjectId(id)).with(modifier, params);
    }

    public void updateOne(ToDoItem item) {
        String id = item.get_id();
        if (id == null) {
            throw new IllegalArgumentException("The _id field is missing.");
        }
        collection().update(new ObjectId(id)).with(item);
    }

    public void markDone(String id, boolean done) {
        updateOne(id, "{done:#}", done);
    }

    protected MongoCollection collection() {
        if (jongo == null) connect();
        return jongo.getCollection(COLL_NAME);
    }

    private List<ToDoItem> cursorToList(MongoCursor<ToDoItem> cursor) {
        ArrayList<ToDoItem> list = new ArrayList<>();
        try {
            for(ToDoItem item : cursor) list.add(item);
        } finally {
            try { cursor.close(); } catch (Throwable e) {/* no op */}
        }
        return list;
    }
}

package org.example.todo;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ToDoMongoAdapter {
    private static final String DB_NAME = "tododb";
    private static final String COLL_NAME = "todos";

    private final ServerAddress serverAddress;
    private MongoClient mongoClient;
    private Jongo jongo;

    public ToDoMongoAdapter() { this(null); }

    public ToDoMongoAdapter(ServerAddress serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void connect() {
        close();
        try {
            mongoClient = serverAddress != null ?
                    new MongoClient(serverAddress) : new MongoClient();
            DB db = mongoClient.getDB(DB_NAME);
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

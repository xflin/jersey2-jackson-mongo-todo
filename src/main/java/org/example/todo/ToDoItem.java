package org.example.todo;

import org.jongo.marshall.jackson.oid.ObjectId;

public class ToDoItem {
    @ObjectId private String _id;
    private String title, body;
    private boolean done;

    public ToDoItem() {}

    public ToDoItem(String title, String body, boolean done) {
        this.title = title;
        this.body = body;
        this.done = done;
    }

    public String get_id() { return _id; }
    public void set_id(String id) { this._id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    @Override
    public String toString() {
        return new StringBuilder("{\n").
                append("  _id: ObjectId(").append(_id).append("),\n").
                append("  title: '").append(title).append("',\n").
                append("  body: '").append(body).append("',\n").
                append("  done: ").append(done).append("\n").
                append("}").toString();
    }
}

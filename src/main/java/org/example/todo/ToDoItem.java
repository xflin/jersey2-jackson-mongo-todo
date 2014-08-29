package org.example.todo;

import org.jongo.marshall.jackson.oid.ObjectId;

public class ToDoItem {
    @ObjectId private String _id;
    private String title, body;
    private Boolean done;

    public ToDoItem() {}

    public ToDoItem(String title, String body, Boolean done) {
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

    public Boolean isDone() { return done; }
    public void setDone(Boolean done) { this.done = done; }

    @Override
    public String toString() {
        return new StringBuilder("{\n").
                append("  _id: ObjectId(").append(_id).append("),\n").
                append("  title: ").append(nullOrQuote(title)).append(",\n").
                append("  body: ").append(nullOrQuote(body)).append(",\n").
                append("  done: ").append(done).append("\n").
                append("}").toString();
    }

    private static String nullOrQuote(String s) {
        return s != null ? "'" + s + "'" : "null";
    }
}

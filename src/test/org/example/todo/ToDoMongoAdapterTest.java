package org.example.todo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.util.List;

public class ToDoMongoAdapterTest {
    private MongoTestHelper mongoTestHelper;
    private ToDoMongoAdapter adapter;

    @Before
    public void setUp() {
        mongoTestHelper = new MongoTestHelper();
        mongoTestHelper.setUp();
        adapter = mongoTestHelper.getAdapter();
    }

    @After
    public void tearDown() { mongoTestHelper.tearDown(); }

    @Test
    public void testFindAll() {
        List<ToDoItem> todos = adapter.findAll();
        Assert.assertEquals(3, todos.size());
    }

    @Test
    public void testFindById() {
        ToDoItem item1 = adapter.findOne();
        String id = item1.get_id();
        ToDoItem item2 = adapter.findById(id);
        Assert.assertEquals(item1.get_id(), item2.get_id());
        Assert.assertEquals(item1.getTitle(), item2.getTitle());
    }

    @Test
    public void testDeleteById() {
        ToDoItem item = adapter.findOne();
        String id = item.get_id();
        adapter.deleteOne(id);
        List<ToDoItem> remainings = adapter.findAll();
        Assert.assertEquals(2, remainings.size());
    }

    @Test
    public void testSaveOne() {
        ToDoItem item = adapter.findOne();
        String id = item.get_id();
        item.setTitle("fix something");
        adapter.saveOne(item);
        ToDoItem itemRetrieved = adapter.findById(id);
        Assert.assertEquals("fix something", itemRetrieved.getTitle());
    }

    @Test
    public void testUpdateOne() {
        ToDoItem item = adapter.findOne();
        String id = item.get_id();
        item.setTitle("fix something else");
        adapter.updateOne(item);
        ToDoItem itemRetrieved = adapter.findById(id);
        Assert.assertEquals("fix something else", itemRetrieved.getTitle());
        Assert.assertEquals(item.getBody(), itemRetrieved.getBody());
        Assert.assertEquals(item.isDone(), itemRetrieved.isDone());
    }

    @Test
    public void testMarkDone() {
        ToDoItem item1 = adapter.findOne();
        String id = item1.get_id();
        adapter.markDone(id, true);
        ToDoItem item2 = adapter.findById(id);
        Assert.assertTrue(item2.isDone());

        adapter.markDone(id, false);
        ToDoItem item3 = adapter.findById(id);
        Assert.assertFalse(item3.isDone());
    }
}

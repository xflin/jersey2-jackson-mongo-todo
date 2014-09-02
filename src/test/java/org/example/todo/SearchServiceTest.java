package org.example.todo;

import io.searchbox.client.JestResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This should be classified as a functional test since it depends on
 * searchly.com service. It won't do anything (and won't fail) if the required
 * environment variable is not found.
 */
public class SearchServiceTest {
    private static Logger logger =
            LoggerFactory.getLogger(SearchServiceTest.class);
    private SearchService searchService;

    @Before
    public void setUp() throws IOException {
        try {
            searchService =
                    new SearchService("todos-test", "TestToDoItem", true);
        } catch (IllegalStateException e) {
            logger.error("Failed to initialize search.", e);
        }
    }

    @After
    public void tearDown() {
        if (searchService != null) searchService.shutdown();
    }

    @Test
    public void testSearch() throws IOException {
        if (searchService == null) {
            logger.warn("Search isn't properly initialized. Skip tests.");
            return;
        }

        ToDoItem[] todos = new ToDoItem[] {
            new ToDoItem("fix faucet", "Fix leaky faucet in kitchen.", false),
            new ToDoItem("tax return",
                    "File overdue 2014 tax return before IRS comes at me.",
                    false),
            new ToDoItem("plan vacation",
                    "Book flight tickets for summer vacation.", true)
        };

        JestResult indexResult =
                searchService.index(todos[0], todos[1], todos[2]);
        assertTrue(indexResult.isSucceeded());
        try { Thread.sleep(500); } catch (InterruptedException e) { }

        List<ToDoItem> searchResult = searchService.search("tax");
        assertEquals(1, searchResult.size());
    }
}


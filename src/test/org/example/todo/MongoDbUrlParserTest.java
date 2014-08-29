package org.example.todo;

import com.mongodb.ServerAddress;
import org.junit.Test;

import static org.example.todo.MongoDbUrlParser.DEFAULT_DB;
import static org.junit.Assert.*;

public class MongoDbUrlParserTest {
    @Test
    public void parseMongoHqUrl() {
        String mongoHq = "mongodb://me:secret@kahana.mongohq.com:10075/tododb";
        MongoDbUrlParser parser = new MongoDbUrlParser(mongoHq);
        assertEquals("me", parser.user);
        assertEquals("secret", parser.password);
        assertEquals("kahana.mongohq.com", parser.host);
        assertEquals(10075, parser.port);
        assertEquals("tododb", parser.db);
    }

    @Test
    public void parseDefaultUrl() {
        MongoDbUrlParser parser = new MongoDbUrlParser(null);
        assertNull(parser.user);
        assertNull(parser.password);
        assertTrue("localhost".equals(parser.host) ||
                "127.0.0.1".equals(parser.host));
        assertEquals(ServerAddress.defaultPort(), parser.port);
        assertEquals(DEFAULT_DB, parser.db);
    }

    @Test
    public void parserUrlWithoutCredential() {
        String localDbUrl = "mongodb://localhost:27017/tododb";
        MongoDbUrlParser parser = new MongoDbUrlParser(localDbUrl);
        assertNull(parser.user);
        assertNull(parser.password);
        assertTrue("localhost".equals(parser.host) ||
                "127.0.0.1".equals(parser.host));
        assertEquals(27017, parser.port);
        assertEquals("tododb", parser.db);
    }
}

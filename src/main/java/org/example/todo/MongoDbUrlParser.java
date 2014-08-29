package org.example.todo;

import com.mongodb.ServerAddress;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * It assumes that the mongodb URL pattern looks like the following:
 *   mongodb://<user>:<password>@kahana.mongohq.com:10075/<db>
 */
public class MongoDbUrlParser {
    public static final String DEFAULT_DB = "tododb";

    private static final Pattern DB_URL_PATTERN =
            Pattern.compile("mongodb://(?:(.+):(.+))?@(.+):(\\d+)/(.+)");

    public final String url;
    public final String host;
    public final int port;
    public final String user, password;
    public final String db;

    public MongoDbUrlParser(String url) {
        String host = null, user = null, password = null, db = null;
        int port = 0;

        this.url = url;
        if (url != null) {
            Matcher matcher = DB_URL_PATTERN.matcher(url);
            if (matcher.matches()) {
                user = matcher.group(1);
                password = matcher.group(2);
                host = matcher.group(3);
                port = Integer.parseInt(matcher.group(4));
                db = matcher.group(5);
            }
        }

        this.host = host != null ? host : ServerAddress.defaultHost();
        this.port = port != 0 ? port : ServerAddress.defaultPort();
        this.user = user;
        this.password = password;
        this.db = db != null ? db : DEFAULT_DB;
    }
}

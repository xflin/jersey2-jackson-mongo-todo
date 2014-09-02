package org.example.todo;

import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.IndicesExists;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class SearchService {
    private static final Logger logger =
            LoggerFactory.getLogger(SearchService.class);
    private static final String DEF_INDEX = "todos";
    private static final String DEF_TYPE = "ToDoItem";

    private final String indexName, typeName;
    private JestHttpClient jestClient;

    public SearchService() throws IOException {
        this(DEF_INDEX, DEF_TYPE, true);
    }

    public SearchService(String index, String type, boolean init)
            throws IOException {
        indexName = index == null || index.trim().equals("") ?
                DEF_INDEX : index;
        typeName = type == null || type.trim().equals("") ? DEF_TYPE : type;
        if (init) {
            initSearchClient();
            initIndices(true);
        }
    }

    public void initSearchClient() throws IOException {
        if (jestClient == null) {
            String searchUrl = System.getenv("SEARCH_URL");
            if (searchUrl != null) {
                HttpClientConfig clientConfig =
                        new HttpClientConfig.Builder(searchUrl).
                                multiThreaded(true).build();
                JestClientFactory factory = new JestClientFactory();
                factory.setHttpClientConfig(clientConfig);
                jestClient = (JestHttpClient) factory.getObject();
            } else {
                logger.warn("Failed to load env. variable SEARCHBOX_URL. " +
                        "Skip search initialization!");
            }
        }
    }

    private void initIndices(boolean removeExisting) throws IOException {
        if (jestClient != null) {
            if (removeExisting) {
                jestClient.execute(new DeleteIndex.Builder(indexName).build());
            }
            IndicesExists indicesExists =
                    new IndicesExists.Builder(indexName).build();
            JestResult result = jestClient.execute(indicesExists);
            if (!result.isSucceeded()) {
                CreateIndex createIndex =
                        new CreateIndex.Builder(indexName).build();
                jestClient.execute(createIndex);
            }
        } else {
            throw new IllegalStateException("Search client must be " +
                    "initialized first before index creation.");
        }
    }

    public void shutdown() {
        if (jestClient != null) jestClient.shutdownClient();
    }

    public JestResult index(ToDoItem item, ToDoItem... moreItems)
            throws IOException {
        Index index0 =
                new Index.Builder(item).index(indexName).type(typeName).build();
        if (moreItems.length == 0) {
            return jestClient.execute(index0);
        }
        Bulk.Builder bulkBuilder = new Bulk.Builder().addAction(index0);
        for(ToDoItem todo: moreItems) {
            Index index = new Index.Builder(todo).index(indexName).
                    type(typeName).build();
            bulkBuilder.addAction(index);
        }
        return jestClient.execute(bulkBuilder.build());
    }

    public List<ToDoItem> search(String query) throws IOException {
        SearchSourceBuilder searchBuilder = new SearchSourceBuilder();
        searchBuilder.query(QueryBuilders.queryString(query));
        String queryString = searchBuilder.toString();
        logger.debug("query:\n" + queryString);

        Search search = new Search.Builder(queryString).addIndex(indexName).
                addType(typeName).setHeader("Content-Type", "application/json").build();
        JestResult result = jestClient.execute(search);
        return result.getSourceAsObjectList(ToDoItem.class);
    }
}

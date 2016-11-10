package it.unipi.ing.drsptr.elastic.txt;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

public class ElasticIndexManager {
	
/*
 *		PARAMETERS
 */
	public static final int DEFAULT_PORT = 9300;
	public static final String DEFAULT_CLUSTER = "elasticsearch";

	
	
/*
 * 		MEMBER VARIABLES
 */
	private TransportClient client;
	private BulkProcessor bulkProcessor;
	
	
	
/*
 *		CONSTRUCTORS 		
 */
	public ElasticIndexManager() {
		client = TransportClient.builder()
								.build();		
	}
	
	
	public ElasticIndexManager(String clusterName) {
		Settings settings = Settings.settingsBuilder()
									.put("cluster.name", clusterName)
									.build();
		
		client = TransportClient.builder()
								.settings(settings)
								.build();	
	}


	public ElasticIndexManager(Settings settings) {
		client = TransportClient.builder()
<<<<<<< HEAD
								.settings(settings)
								.build();
=======
				.settings(settings)
				.build();
>>>>>>> origin/master
	}

	
	
/*
 * 		CONNECT/DISCONNECT	
 */
	public void connectTo(InetAddress address, int port) {
		client.addTransportAddress(new InetSocketTransportAddress(address, port));
	}
	
	
	public void connectTo(String hostname, int port) throws UnknownHostException {
		client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostname), port));
	}

	
	public void disconnectFrom(InetAddress address, int port) {
		client.removeTransportAddress(new InetSocketTransportAddress(address, port));
	}
	
	
	public void disconnectFrom(String hostname, int port) throws UnknownHostException {
		client.removeTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostname), port));
	}
	
	
	public void close() {
		client.close();
	}
	
	

/*
 * 		CREATE/DELETE INDEX	
 */
	public void createIndex(String indexName, Settings settings) {
		client.admin().indices().prepareCreate(indexName)
								.setSettings(settings)
								.get();
	}
	
	
	public void deleteIndex(String indexName) {
		client.admin().indices().prepareDelete(indexName)
								.get();
	}
	
	
	public void putMapping(String indexName, String indexType, String mapping) {
		client.admin().indices().preparePutMapping(indexName)
								.setType(indexType)
								.setSource(mapping)
								.get();
	}



/*
 * 		MANAGEMENT	
 */
	public IndexResponse index(String indexName, String indexType, String docId, String jsonDoc) {
		return client.prepareIndex(indexName, indexType, docId)
						.setSource(jsonDoc)
						.get();
	}
	
	
	public GetResponse get(String indexName, String indexType, String docId) {
		return client.prepareGet(indexName, indexType, docId)
						.get();
	}
	
	
	public SearchResponse search(String indexName, String indexType, String fieldName, String fieldValue, int maxResultSize) {
		return client.prepareSearch(indexName)
						.setTypes(indexType)
						.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
						.setQuery(QueryBuilders.queryStringQuery(fieldValue).field(fieldName))
						.setFrom(0)
						.setSize(maxResultSize)
						.setExplain(true)
						.execute()
						.actionGet();
	}
	
	
	public DeleteResponse delete(String indexName, String indexType, String docId) {
		return client.prepareDelete(indexName, indexType, docId).get();
	}
	
	
	public UpdateResponse update(String indexName, String indexType, String docId, String fieldName, String fieldValue) throws IOException, InterruptedException, ExecutionException {
		UpdateRequest updateRequest = new UpdateRequest(indexName, indexType, docId);
		
		updateRequest.doc(jsonBuilder()
		        			.startObject()
		        				.field(fieldName, fieldValue)
		        			.endObject());
		
		return client.update(updateRequest).get();
	}
	
	
	public UpdateResponse upsert(String indexName, String indexType, String docId, String jsonDoc, String fieldName, String fieldValue) throws IOException, InterruptedException, ExecutionException {
		IndexRequest indexRequest = new IndexRequest(indexName, indexType, docId);
		indexRequest.source(jsonDoc);
		
		UpdateRequest updateRequest = new UpdateRequest(indexName, indexType, docId);
		updateRequest.doc(jsonBuilder()
    						.startObject()
    							.field(fieldName, fieldValue)
    						.endObject());
		updateRequest.upsert(indexRequest);
		
		return client.update(updateRequest).get();
	}
	

	public BulkResponse bulkIndex(String indexName, String indexType, Map<String, String> idJsonDocMap, boolean refresh) {
		BulkRequestBuilder bulkRequestBuilder = client.prepareBulk()
														.setRefresh(refresh);

		for(String docId : idJsonDocMap.keySet())
			bulkRequestBuilder.add(client
										.prepareIndex(indexName, indexType, docId)
										.setSource(idJsonDocMap.get(docId)));

		BulkResponse  bulkResponse = bulkRequestBuilder.get();

		return bulkResponse;
	}


	public BulkResponse bulkDelete(String indexName, String indexType, Map<String, String> idJsonDocMap) {
		BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();

		for(String docId : idJsonDocMap.keySet())
			bulkRequestBuilder.add(client
									.prepareDelete(indexName, indexType, docId));

		return bulkRequestBuilder.get();
	}


	public BulkResponse bulkUpdate(String indexName, String indexType, Map<String, String> idJsonDocMap) {
		UpdateRequest updateRequest;
		BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();

		for(String docId : idJsonDocMap.keySet()) {
			updateRequest = new UpdateRequest(indexName, indexType, docId);
			updateRequest.doc(idJsonDocMap.get(docId));
			bulkRequestBuilder.add(updateRequest);
		}

		return bulkRequestBuilder.get();
	}


	public BulkResponse bulkOperation(String indexName, String indexType, Map<String, String> idJsonMapToIndex, Map<String, String> idJsonMapToUpdate, Map<String, String> idJsonMapToDelete) {
		UpdateRequest updateRequest;
		BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();

		if(idJsonMapToIndex != null)
			for(String docId : idJsonMapToIndex.keySet())
				bulkRequestBuilder.add(client
						.prepareIndex(indexName, indexType, docId)
						.setSource(idJsonMapToIndex.get(docId)));

		if(idJsonMapToUpdate != null)
			for(String docId : idJsonMapToUpdate.keySet()) {
				updateRequest = new UpdateRequest(indexName, indexType, docId);
				updateRequest.doc(idJsonMapToUpdate.get(docId));
				bulkRequestBuilder.add(updateRequest);
			}

		if(idJsonMapToDelete != null)
			for(String docId : idJsonMapToDelete.keySet())
				bulkRequestBuilder.add(client
										.prepareDelete(indexName, indexType, idJsonMapToDelete.get(docId)));

		return bulkRequestBuilder.get();
	}


	public void initBulkProcessor(int numberOfRequests) {

	}


	public void forceRefresh() {
		client.admin().indices().prepareRefresh().get();
	}
	
	
	public void forceRefresh(String indexName) {
		client.admin().indices().prepareRefresh(indexName).get();
	}
}
package it.unipi.ing.drsptr.elastic.txt;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.lucene.index.Terms;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.termvectors.TermVectorsResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

/*
 * ElasticIndexManager is the base class which allows you to connect to an ES cluster and to perform operations on it.
 * It allows to: 1) Connect and disconnect to a cluster of nodes
 * 				 2) Create or delete indices and/or types
 * 				 3) Index, delete, update, retrieve and search for documents with respect to an index and type
 * 				 4) Refresh a whole index or a type
 * 				 5) Perform operations as an unique bulk request.
 * @author		Pietro De Rosa
 */
public class ElasticIndexManager {
	
	public static final int DEFAULT_PORT = 9300;
	public static final String DEFAULT_CLUSTER = "elasticsearch";

	protected TransportClient client;
	
	



/*
 * Default constructor. It uses default settings.
 */
	public ElasticIndexManager() {
		client = TransportClient.builder()
								.build();		
	}
	
/*
 * This constructor uses the default settings but it allows to specify the name of the cluster you have to connect to.
 * @param		clustername		-	the name of the cluster you have to connect to
 */
	public ElasticIndexManager(String clusterName) {
		Settings settings = Settings.settingsBuilder()
									.put("cluster.name", clusterName)
									.build();
		
		client = TransportClient.builder()
								.settings(settings)
								.build();	
	}

/*
 * This constructor allows you specify custom settings for your client (cluster's name, ping timeout, etc).
 * @param		settings		-	the settings for your client node
 */
	public ElasticIndexManager(Settings settings) {
		client = TransportClient.builder()
								.settings(settings)
								.build();
	}





/*
 * It allows your client to connect to an elasticsearch node with a given IP address and listening port.
 * @param		address			-	the address of the elasticsearch node you want to connect to
 * @param		port			-	the listening port of the elasticsearch node you want to connect to
 */
	public void connectTo(InetAddress address, int port) {
		client.addTransportAddress(new InetSocketTransportAddress(address, port));
	}

/*
 * It allows your client to connect to an elasticsearch node, given its hostname and its listening port.
 * It tries to resolve the hostname and then connects to it.
 * @param		hostname		-	the name of the host where the elasticsearch node is running
 * @param		port			-	the listening port of the elasticsearch node
 * @throws		UnknownHostException if the hostname cannot be resolved
 */
	public void connectTo(String hostname, int port) throws UnknownHostException {
		client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostname), port));
	}

/*
 * It disconnects your client from an elasticsearch node of the cluster, given its IP address and listening port.
 * @param		address			-	the address of the elasticsearch node you want to disconnect from
 * @param		port			-	the listening port of the elasticsearch node you want to disconnect from
 */
	public void disconnectFrom(InetAddress address, int port) {
		client.removeTransportAddress(new InetSocketTransportAddress(address, port));
	}
	
/*
 * It disconnects your client from an elasticsearch node of the cluster, given its hostname and listening port.
 * @param		hostname		-	the name of the host where the elasticsearch node is running
 * @param		port			-	the listening port of the elasticsearch node you want to disconnect from
 * @throws		UnknownHostException if the hostname cannot be resolved
 */
	public void disconnectFrom(String hostname, int port) throws UnknownHostException {
		client.removeTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostname), port));
	}

/*
 * It closes all the connections with all the elasticsearch nodes of the cluster and returns.
 */
	public void close() {
		client.close();
	}



	

/*
 * It creates a new index in the cluster with the specified settings.
 * @param		indexName		-	the name of the index you're going to create
 * @param		settings		-	the settings of the index you're creating (number of shards, number of replicas, etc)
 */
	public void createIndex(String indexName, Settings settings) {
		client.admin().indices().prepareCreate(indexName)
								.setSettings(settings)
								.get();
	}

/*
 * It deletes an index and all its types.
 * @param		indexName		-	the name of the index you want to delete
 */
	public void deleteIndex(String indexName) {
		client.admin().indices().prepareDelete(indexName)
								.get();
	}

/*
 * It allows to add a new type to an existing index or to update an existing mapping.
 * @param		indexName		-	the name of the index
 * @param		typeName		-	the name of the type
 * @param		mapping			-	the JSON document representing the mapping.
 * 									You can create it using the MappingBuilder class
 */
	public void putMapping(String indexName, String typeName, String mapping) {
		client.admin().indices().preparePutMapping(indexName)
								.setType(typeName)
								.setSource(mapping)
								.get();
	}





/*
 * It allows to index a given document in the specified type within the index.
 * @param		indexName		-	the name of the index
 * @param		typeName		-	the name of the type
 * @param 		docId			-	the name that will be used to uniquely identify the document within the type in the index
 * @param		jsonDoc			-	the JSON document to be indexed
 * @return		the result of the indexing operation
 */
	public IndexResponse index(String indexName, String typeName, String docId, String jsonDoc) {
		return client.prepareIndex(indexName, typeName, docId)
						.setSource(jsonDoc)
						.get();
	}

/*
 * It allows to retrieve an already indexed document on the base of its id.
 * @param		indexName		-	the name of the index
 * @param		typeName		-	the name of the type
 * @param		docId			-	the id of the document to retrieve
 * @return		the response contains both the result of the operation and the JSON document
 */
	public GetResponse get(String indexName, String typeName, String docId) {
		return client.prepareGet(indexName, typeName, docId)
						.get();
	}

/*
 * It allows to get the total number of documents stored in a given index.
 * @param		indexName		-	the name of the index
 * @return		the total number of documents stored in the index
 */
	public long getDocumentCount(String indexName) {
		SearchResponse searchResponse = client.prepareSearch(indexName)
				.setQuery(QueryBuilders.matchAllQuery())
				.get();

		return searchResponse.getHits().getTotalHits();
	}

/*
 * It allows to get the total number of documents stored in a given type of a given index.
 * @param		indexName		-	the name of the index
 * @param		typeName		-	the name of the type
 * @return		the total number of documents stored in the type of the the index
 */
	public long getDocumentCount(String indexName, String typeName) {
		SearchResponse searchResponse = client.prepareSearch(indexName)
													.setTypes(typeName)
													.setQuery(QueryBuilders.matchAllQuery())
													.get();

		return searchResponse.getHits().getTotalHits();
	}

/*
 * It allows to get the term vector for a given document. The field of the document from which you want to get the tv
 * has to have term vector property enables, otherwise it is not possible to retrieve it.
 * @param		indexName		-	the name of the index
 * @param		typeName		-	the name of the type
 * @param		docId			-	the id of the document
 * @param		dfs				-	true for distributed frequencies, false for shard statistics
 * @throws		IOException if cannnot perform getFields()
 * @return		the response containing the term vector
 */
	public TermVectorsResponse getTermVector(String indexName, String typeName, String docId, boolean dfs) {
		return client.prepareTermVectors()
				.setIndex(indexName)
				.setType(typeName)
				.setId(docId)
				.setTermStatistics(true)
				.setDfs(dfs)
				.get();
	}

/*
 * It allows to get the term vector for a given document. The field of the document from which you want to get the tv
 * has to have term vector property enables, otherwise it is not possible to retrieve it.
 * @param		indexName		-	the name of the index
 * @param		typeName		-	the name of the type
 * @param		docId			-	the id of the document
 * @param		fieldName		-	the name of the field
 * @param		dfs				-	true for distributed frequencies, false for shard statistics
 * @throws		IOException if cannnot perform getFields()
 * @return		the org.apache.lucene.index.Terms class containing the term vector
 */
	public Terms getTermVector(String indexName, String typeName, String docId, String fieldName, boolean dfs) throws IOException{
		TermVectorsResponse termVectorsResponse = client.prepareTermVectors()
														.setIndex(indexName)
														.setType(typeName)
														.setId(docId)
														.setTermStatistics(true)
														.setDfs(dfs)
														.get();

		return termVectorsResponse.getFields().terms(fieldName);
	}

/*
 * It performs a query string search on the specified index and type.
 * @param		indexName		-	the name of the index
 * @param		typeName		-	the name of the type
 * @param		fieldName		-	the field which has to be searched for the query string
 * @param		fieldValue		-	the query string
 * @param		maxResultSize	-	the cardinality of the result
 * @return		the response contains the documents with the highest score with respect to the query document
 */
	public SearchResponse queryStringSearch(String indexName, String typeName, String fieldName, String fieldValue, int maxResultSize) {
		return client.prepareSearch(indexName)
						.setTypes(typeName)
						.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
						.setQuery(QueryBuilders.queryStringQuery(fieldValue).field(fieldName))
						.setFrom(0)
						.setSize(maxResultSize)
						.setExplain(true)
						.execute()
						.actionGet();
	}

/* It delete a document from the index, given its id.
 * @param		indexName		-	the name of the index
 * @param		typeName		-	the name of the type
 * @param		docId			-	the id of the document that has to be deleted from the index
 */
	public DeleteResponse delete(String indexName, String typeName, String docId) {
		return client.prepareDelete(indexName, typeName, docId).get();
	}

/* It updates a given field of a document with a new value.
 * @param		indexName		-	the name of the index
 * @param		typeName		-	the name of the type
 * @param		docId			-	the id of the document that has to be updated
 * @param		fieldName		-	the name of the field that has to be updated
 * @param		fieldValue		-	the new value of the specified field
 * @throws		something goes wrong
 * @return		the response contains the result of the updating operation
 */
	public UpdateResponse update(String indexName, String typeName, String docId, String fieldName, String fieldValue) throws IOException, InterruptedException, ExecutionException {
		UpdateRequest updateRequest = new UpdateRequest(indexName, typeName, docId);
		
		updateRequest.doc(jsonBuilder()
		        			.startObject()
		        				.field(fieldName, fieldValue)
		        			.endObject());
		
		return client.update(updateRequest).get();
	}

/* It tries to update a given field of a document, if it exist; otherwise it creates the document.
 * @param		indexName		-	the name of the index
 * @param		typeName		-	the name of the type
 * @param		docId			-	the id of the document to update or to add
 * @param		jsonDoc			-	the JSON document that will be indexed, if the document doesn't exist
 * @param		fieldName		-	the name of the field that has to be updated, if the document exists
 * @param		fieldValue		-	the new value of the specified field, if the document exists
 * @throws		something goes wrong
 * @return		the response contains the result of the updating operation
 */
	public UpdateResponse upsert(String indexName, String typeName, String docId, String jsonDoc, String fieldName, String fieldValue) throws IOException, InterruptedException, ExecutionException {
		IndexRequest indexRequest = new IndexRequest(indexName, typeName, docId);
		indexRequest.source(jsonDoc);
		
		UpdateRequest updateRequest = new UpdateRequest(indexName, typeName, docId);
		updateRequest.doc(jsonBuilder()
    						.startObject()
    							.field(fieldName, fieldValue)
    						.endObject());
		updateRequest.upsert(indexRequest);
		
		return client.update(updateRequest).get();
	}
	




/* It allows to index several documents in a single request. So, indexing is more efficient and faster.
 * @param		indexName		-	the name of the index
 * @param		typeName		-	the name of the type
 * @param		idJsonDocMap	-	the hash map containing the pairs <document's id, JSON document> that
 * 									have to be indexed
 * @param		refresh			-	if true, the documents are immediately searchable; otherwise, you have to
 *									wait the next automatic refresh operation to be performed
 * @return		the response contains the result of the bulk operation
 */
	public BulkResponse bulkIndex(String indexName, String typeName, Map<String, String> idJsonDocMap, boolean refresh) {
		BulkRequestBuilder bulkRequestBuilder = client.prepareBulk()
														.setRefresh(refresh);

		for(String docId : idJsonDocMap.keySet())
			bulkRequestBuilder.add(client
										.prepareIndex(indexName, typeName, docId)
										.setSource(idJsonDocMap.get(docId)));

		BulkResponse  bulkResponse = bulkRequestBuilder.get();

		return bulkResponse;
	}

/* It allows to delete several documents in a single request.
 * @param		indexName		-	the name of the index
 * @param		typeName		-	the name of the type
 * @param		docsList		-	the list of documents' id to delete
 * @return		the response contains the result of the bulk operation
 */
	public BulkResponse bulkDelete(String indexName, String typeName, List<String> docsList) {
		BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();

		for(String docId : docsList)
			bulkRequestBuilder.add(client
									.prepareDelete(indexName, typeName, docId));

		return bulkRequestBuilder.get();
	}

/* It allows to update several documents in a single request.
 * @param		indexName		-	the name of the index
 * @param		typeName		-	the name of the type
 * @param		idJsonDocMap	-	the hash map containing the pairs <document's id, JSON document> that
 * 									have to be updated
 * @return		the response contains the result of the bulk operation
 */
	public BulkResponse bulkUpdate(String indexName, String typeName, Map<String, String> idJsonDocMap) {
		UpdateRequest updateRequest;
		BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();

		for(String docId : idJsonDocMap.keySet()) {
			updateRequest = new UpdateRequest(indexName, typeName, docId);
			updateRequest.doc(idJsonDocMap.get(docId));
			bulkRequestBuilder.add(updateRequest);
		}

		return bulkRequestBuilder.get();
	}

/* It allows to index, update and delete several documents in a single request.
 * @param		indexName			-	the name of the index
 * @param		typeName			-	the name of the type
 * @param		idJsonMapToIndex	-	the hash map containing the pairs <document's id, JSON document> that
 * 										have to be indexed
 * @param		idJsonMapToUpdate	-	the hash map containing the pairs <document's id, JSON document> that
 * 										have to be updated
 * @param		docsToDelete		-	the list of documents' id to delete
 * @return		the response contains the result of the bulk operation
 */
	public BulkResponse bulkOperation(String indexName, String typeName, Map<String, String> idJsonMapToIndex, Map<String, String> idJsonMapToUpdate, List<String> docsToDelete) {
		UpdateRequest updateRequest;
		BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();

		if(idJsonMapToIndex != null)
			for(String docId : idJsonMapToIndex.keySet())
				bulkRequestBuilder.add(client
						.prepareIndex(indexName, typeName, docId)
						.setSource(idJsonMapToIndex.get(docId)));

		if(idJsonMapToUpdate != null)
			for(String docId : idJsonMapToUpdate.keySet()) {
				updateRequest = new UpdateRequest(indexName, typeName, docId);
				updateRequest.doc(idJsonMapToUpdate.get(docId));
				bulkRequestBuilder.add(updateRequest);
			}

		if(docsToDelete != null)
			for(String docId : docsToDelete)
				bulkRequestBuilder.add(client
										.prepareDelete(indexName, typeName, docId));

		return bulkRequestBuilder.get();
	}





/*
 * It allows to refresh all the indices without waiting for the automatic refreshing.
 */
	public void forceRefresh() {
		client.admin().indices().prepareRefresh().get();
	}

/*
 * It allows to refresh the specified index without waiting for the automatic refreshing.
 * @param		indexName		-	the name of the index to refresh
 */
	public void forceRefresh(String indexName) {
		client.admin().indices().prepareRefresh(indexName).get();
	}
}
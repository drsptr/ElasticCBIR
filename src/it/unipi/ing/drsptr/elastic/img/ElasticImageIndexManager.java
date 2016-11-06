package it.unipi.ing.drsptr.elastic.img;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;

import it.unipi.ing.drsptr.elastic.img.tools.Fields;
import it.unipi.ing.drsptr.elastic.img.tools.JsonImageBuilder;
import it.unipi.ing.drsptr.elastic.txt.ElasticIndexManager;
import it.unipi.ing.drsptr.elastic.utilities.JsonDocParser;
import it.unipi.ing.drsptr.elastic.utilities.JsonDocParserFieldNotFoundException;
import it.unipi.ing.mim.deep.ImgDescriptor;
import it.unipi.ing.mim.deep.seq.SeqImageStorage;
import it.unipi.ing.mim.deep.tools.FeaturesStorage;

public class ElasticImageIndexManager {
	
	private ElasticIndexManager indexManager;
	private List<ImgDescriptor> imgDataset;
	private int qFactor;

	
	// Constructor - it must be called when the deep features already exist in 'storageFile'
	public ElasticImageIndexManager(File storageFile, int qFactor) throws IOException, ClassNotFoundException {
		indexManager = new ElasticIndexManager();
		this.qFactor = qFactor;
		imgDataset = FeaturesStorage.load(storageFile);
	}
	
	public ElasticImageIndexManager(String clusterName, File storageFile, int qFactor) throws IOException, ClassNotFoundException {
		indexManager = new ElasticIndexManager(clusterName);
		this.qFactor = qFactor;
		imgDataset = FeaturesStorage.load(storageFile);
	}
	
	
	// Constructor - it first extracts deep features from 'imgSrcFolder', then stores them in 'storageFile'
	public ElasticImageIndexManager(File imgSrcFolder, File storageFile, int qFactor) throws IOException {
		indexManager = new ElasticIndexManager();
		imgDataset = SeqImageStorage.extractFeatures(imgSrcFolder);
		FeaturesStorage.store(imgDataset, storageFile);
	}
	
	public ElasticImageIndexManager(String clusterName, File imgSrcFolder, File storageFile, int qFactor) throws IOException {
		indexManager = new ElasticIndexManager(clusterName);
		imgDataset = SeqImageStorage.extractFeatures(imgSrcFolder);
		FeaturesStorage.store(imgDataset, storageFile);
	}
	
	
	
	public void connectTo(InetAddress address, int port) {
		indexManager.connectTo(address, port);
	}
	
	
	public void connectTo(String hostname, int port) throws UnknownHostException {
		indexManager.connectTo(hostname, port);
	}

	
	public void disconnectFrom(InetAddress address, int port) {
		indexManager.disconnectFrom(address, port);
	}
	
	
	public void disconnectFrom(String hostname, int port) throws UnknownHostException {
		indexManager.disconnectFrom(InetAddress.getByName(hostname), port);
	}
	
	
	public void close() {
		indexManager.close();
	}
	
	
	
	public void createIndex(String indexName, Settings.Builder settings) {
		indexManager.createIndex(indexName, settings);
	}
	
	
	public void deleteIndex(String indexName) {
		indexManager.deleteIndex(indexName);
	}
	
	
	
	public void indexImgDataset(String indexName, String indexType) throws IOException, InterruptedException {
		int i, dataSize = imgDataset.size();
		
		for(i=0; i<dataSize; i++) {
			System.out.print("Indexing.. " + (int)Math.ceil(((double)i/(double)dataSize) * 100) + "%");
			indexManager.index(indexName, indexType, imgDataset.get(i).getId(), JsonImageBuilder.imageToJson(imgDataset.get(i), qFactor));
			System.out.print("\r");
		}
		System.out.print("Indexing.. " + (int)Math.ceil(((double)i/(double)dataSize) * 100) + "%");
	}

	public void bulkIndexImgDataset(String indexName, String indexType) throws IOException, InterruptedException {
		Map<String, String> idJsonDocMap = new HashMap<>();

		for(ImgDescriptor imgDesc : imgDataset)
			idJsonDocMap.put(imgDesc.getId(), JsonImageBuilder.imageToJson(imgDesc, qFactor));
		indexManager.bulkIndex(indexName, indexType, idJsonDocMap, true);
		System.out.print("Indexing.. DONE");
	}

	
	
	public IndexResponse index(String indexName, String indexType, ImgDescriptor imgDesc) throws IOException {
		return indexManager.index(indexName, indexType, imgDesc.getId(), JsonImageBuilder.imageToJson(imgDesc, qFactor));
	}
	
	
	public ImgDescriptor getImgDescriptor(String indexName, String indexType, String imgID) throws IOException, ClassNotFoundException, JsonDocParserFieldNotFoundException {
		GetResponse getRespQuery = indexManager.get(indexName, indexType, imgID);
		return ImgDescriptor.fromBytes(JsonDocParser.getBase64EncodedBinaryField(getRespQuery.getSourceAsString(), Fields.BIN));
	}
	
	// search using the already indexed image with name 'queryImgId' as input
	public List<ImgDescriptor> search(String indexName, String indexType, String queryImgId, int k) throws IOException, ClassNotFoundException, JsonDocParserFieldNotFoundException {
		List<ImgDescriptor> result = new ArrayList<ImgDescriptor>(k);
		ImgDescriptor imgDesc;
		
		GetResponse getRespQuery = indexManager.get(indexName, indexType, queryImgId);
		String imgTxt = JsonDocParser.getStringFieldValue(getRespQuery, Fields.IMG);
		
		SearchResponse searchResp = indexManager.search(indexName, indexType, Fields.IMG, imgTxt, k);
		SearchHit[] srcHits = searchResp.getHits().hits();
		
		for(int i=0; i<srcHits.length; i++) {
			imgDesc = ImgDescriptor.fromBytes(JsonDocParser.getBase64EncodedBinaryField(srcHits[i], Fields.BIN));
			imgDesc.setDist(srcHits[i].getScore());
			result.add(imgDesc);
		}
		
		return result;
	}
	
	
	
	public void forceRefresh() {
		indexManager.forceRefresh();
	}
	
	
	public void forceRefresh(String indexName) {
		indexManager.forceRefresh(indexName);
	}
}

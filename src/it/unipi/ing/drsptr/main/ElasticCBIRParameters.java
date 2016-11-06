package it.unipi.ing.drsptr.main;

import java.io.File;
import java.net.InetAddress;

import org.elasticsearch.common.settings.Settings;

public class ElasticCBIRParameters {

	// Addresses
	public static final InetAddress LOOPBACK_ADDRESS = InetAddress.getLoopbackAddress();

	
	// Elasticsearch settings
	public static final String INDEX_NAME = "cbir";
	public static final String INDEX_TYPE = "images";
	public static final int NUMBER_OF_SHARDS = 1;
	public static final int NUMBER_OF_REPLICAS = 0;
	public static final Settings.Builder INDEX_SETTINGS = Settings.builder()
																	.put("index.number_of_shards", ElasticCBIRParameters.NUMBER_OF_SHARDS)
																	.put("index.number_of_replicas", ElasticCBIRParameters.NUMBER_OF_REPLICAS);
	// Elasticsearch Mapping
	public static final	String INDEX_MAPPING = "";
	
	// Sample image
	public static final String SRC_IMG = "01d6ab62633cc865f54500be71a36cad.jpg";
	
	// Quantization factor
	public static final int Q = 30;
		
	//Image Source Folder
	public static final File SRC_FOLDER = new File("data/img");
		
	//Features Storage File
	public static final File STORAGE_FILE = new File("data/deep.seq.dat");

	//Lucene Index
	public static final String  LUCENE_PATH = "out/"  + "Lucene_Deep";
		
	//k-Nearest Neighbors
	public static final int K = 50;
		
	//HTML Output Parameters
	public static final  String BASE_URI = "file:///" + ElasticCBIRParameters.SRC_FOLDER.getAbsolutePath() + "/";
	public static final File RESULTS_HTML_LUCENE = new File("out/deep.lucene.html");
}

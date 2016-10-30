package it.unipi.ing.drsptr.main;

import java.io.File;
import java.net.InetAddress;

public class ElasticCBIRParameters {

	// Loopback address
	public static final InetAddress LOOPBACK_ADDRESS = InetAddress.getLoopbackAddress();

	// Elasticsearch index name
	public static final String INDEX_NAME = "cbir";

	// Elasticsearch index type
	public static final String INDEX_TYPE = "images";

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

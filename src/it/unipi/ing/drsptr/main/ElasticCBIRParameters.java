package it.unipi.ing.drsptr.main;

import java.io.File;
import java.net.InetAddress;
<<<<<<< HEAD
import java.util.*;
=======
import java.util.HashMap;
import java.util.Map;
>>>>>>> origin/master

import it.unipi.ing.drsptr.elastic.img.tools.Fields;
import org.elasticsearch.common.settings.Settings;

public class ElasticCBIRParameters {

	// Addresses
	public static final InetAddress LOOPBACK_ADDRESS = InetAddress.getLoopbackAddress();

	
	// Elasticsearch settings
	public static final String INDEX_NAME = "cbir";
	public static final String INDEX_TYPE = "yfcc100m";
	public static final int NUMBER_OF_SHARDS = 16;
	public static final int NUMBER_OF_REPLICAS = 0;
	public static final Settings INDEX_SETTINGS = Settings.builder()
																	.put("index.number_of_shards", ElasticCBIRParameters.NUMBER_OF_SHARDS)
<<<<<<< HEAD
																	.put("index.number_of_replicas", ElasticCBIRParameters.NUMBER_OF_REPLICAS)
															.build();
=======
																	.put("index.number_of_replicas", ElasticCBIRParameters.NUMBER_OF_REPLICAS);
>>>>>>> origin/master


	// Lucene imported index settings
	public static final String LUCENE_INDEX_PATH = "C://Users//Pietro//Desktop//DeepLuceneYFCC100M_Q30_merged";
<<<<<<< HEAD
	public static final String LUCENE_FIELDS_ID = "ID"; // Indexed (docs), Stored, Norms
	public static final String LUCENE_FIELDS_IMG = "DEEP"; // Indexed (docs, freqs), term Vector, Norms
	public static final String LUCENE_FIELDS_TAGS = "TXT"; // Indexed (docs, freqs), Stored, term Vector, Norms
	public static final String LUCENE_FIELDS_URI = "URI"; // Indexed (docs), Stored, Norms
	public static final List<String> LUCENE_FIELDS_STORED = Collections.unmodifiableList(	Arrays.asList(	LUCENE_FIELDS_ID,
																											LUCENE_FIELDS_TAGS,
																											LUCENE_FIELDS_URI
																						));
	public static final List<String> LUCENE_FIELDS_TV = Collections.unmodifiableList(	Arrays.asList(	LUCENE_FIELDS_IMG
																						));

=======
	public static final String LUCENE_FIELDS_ID = "ID";
	public static final String LUCENE_FIELDS_IMG = "DEEP";
	public static final String LUCENE_FIELDS_TAGS = "TXT";
	public static final String LUCENE_FIELDS_URI = "URI";
>>>>>>> origin/master


	// Elasticsearch Mapping
	public static final Map<String, Map<String, String>> MAPPING_FIELDS = new HashMap<String, Map<String, String>>(Fields.NUMBER_OF_FIELDS) {
		{
			Map<String, String> propIMG = new HashMap<String, String>() {
				{
					put("type", "string");
					put("index_options", "freqs");
					put("term_vector", "yes");
				}
			};

			Map<String, String> propTAGS = new HashMap<String, String>() {
				{
					put("type", "string");
					put("index_options", "freqs");
					put("store", "true");
					put("term_vector", "yes");
				}
			};

			Map<String, String> propURI = new HashMap<String, String>() {
				{
					put("type", "string");
					put("index_options", "docs");
					put("store", "true");
				}
			};

			put(Fields.IMG, propIMG);
			put(Fields.TAGS, propTAGS);
			put(Fields.URI, propURI);
		}
	};
	
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

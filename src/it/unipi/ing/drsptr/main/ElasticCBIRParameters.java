package it.unipi.ing.drsptr.main;

import java.io.File;
import java.net.InetAddress;
import java.util.*;

import it.unipi.ing.drsptr.elastic.img.tools.Fields;
import it.unipi.ing.drsptr.elastic.txt.ElasticIndexManager;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;

public class ElasticCBIRParameters {

	// Addresses
	public static final InetAddress LOOPBACK_ADDRESS = InetAddress.getLoopbackAddress();

	
	// Elasticsearch settings
	public static final String INDEX_NAME = "cbir";
	public static final String TYPE_NAME = "yfcc100m";
	public static final int NUMBER_OF_SHARDS = 16;
	public static final int NUMBER_OF_REPLICAS = 0;
	public static final Settings INDEX_SETTINGS = Settings.builder()
																.put("index.number_of_shards", ElasticCBIRParameters.NUMBER_OF_SHARDS)
																.put("index.number_of_replicas", ElasticCBIRParameters.NUMBER_OF_REPLICAS)
															.build();


	// Elasticsearch Mapping
	public static final Map<String, Map<String, String>> MAPPING_FIELDS = new HashMap<String, Map<String, String>>() {
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
					put("term_vector", "yes");
				}
			};

			Map<String, String> propURI = new HashMap<String, String>() {
				{
					put("type", "string");
					put("index_options", "docs");
				}
			};

			put(Fields.IMG, propIMG);
			put(Fields.TAGS, propTAGS);
			put(Fields.URI, propURI);
		}
	};



	// Elasticsearch BulkProcessor settings
	public static final Map<String, Object> BULK_PROCESSOR_SETTINGS = new HashMap<String, Object>() {
		{
			put(ElasticIndexManager.BULK_PROC_ACTIONS, new Integer(10000));
			put(ElasticIndexManager.BULK_PROC_SIZE, new ByteSizeValue(1, ByteSizeUnit.GB));
			put(ElasticIndexManager.BULK_PROC_REQS, 5);
		}
	};



	// Lucene imported index settings
	public static final String LUCENE_INDEX_PATH = "C://Users//Pietro//Desktop//DeepLuceneYFCC100M_Q30_merged";
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



	// Local dataset settings
		// Quantization factor
			public static final int Q = 30;
		//Image Source Folder
			public static final File SRC_FOLDER = new File("data/img");
		//Features Storage File
			public static final File STORAGE_FILE = new File("data/deep.seq.dat");



	// Search settings
		// Sample image
			public static final String SRC_IMG = "12413778503";
		//k-Nearest Neighbors
			public static final int K = 100;
		// Reduction factor
			public static final int Lq = 10;
		// Reordering factor
			public static final int Cr = 10;



	//HTML Output Parameters
	public static final File RESULTS_HTML = new File("out/results.html");
}

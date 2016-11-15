package it.unipi.ing.drsptr.elastic.img;

import java.io.File;
import java.io.IOException;
import java.util.*;

import it.unipi.ing.drsptr.elastic.img.tools.DeepFeatureEncoder;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.spans.SpanWeight;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.termvectors.TermVectorsRequestBuilder;
import org.elasticsearch.action.termvectors.TermVectorsResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.search.SearchHit;

import it.unipi.ing.drsptr.elastic.img.tools.Fields;
import it.unipi.ing.drsptr.elastic.img.tools.JsonImageBuilder;
import it.unipi.ing.drsptr.elastic.txt.ElasticIndexManager;
import it.unipi.ing.drsptr.elastic.utilities.JsonDocParser;
import it.unipi.ing.drsptr.elastic.utilities.JsonDocParserFieldNotFoundException;
import it.unipi.ing.mim.deep.ImgDescriptor;
import it.unipi.ing.mim.deep.seq.SeqImageStorage;
import it.unipi.ing.mim.deep.tools.FeaturesStorage;

/*
 * ElasticImageIndexManager extends the ElasticIndexManager class introducing additional functions that allows to manage
 * easily a local or a remote image dataset with Elasticsearch.
 * It allows to: 1) Perform all the operations available with the ElasticIndexManager class
 * 				 2) Index a local image dataset, extracting the deep features from the images of the dataset or loading
 * 				 	them from a file
 * 				 3) Perform a visual similarity search
 * 				 4) Perform a textual search on the images' tags
 * @author		Pietro De Rosa
 */
public class ElasticImageIndexManager extends ElasticIndexManager {

	private int qFactor;
	private List<ImgDescriptor> imgDataset; // for local dataset only





/*
 * Default constructor. It uses default settings.
 * @param		qFactor			-	the quantization factor used to obtain the encoded text for the features
 */
	public ElasticImageIndexManager(int qFactor)  {
		super();
		this.qFactor = qFactor;
	}

/*
 * This constructor uses the default settings but it allows to specify the name of the cluster you have to connect to.
 * @param		clustername		-	the name of the cluster you have to connect to
 * @param		qFactor			-	the quantization factor used to obtain the encoded text for the features
 */
	public ElasticImageIndexManager(String clusterName, int qFactor) {
		super(clusterName);
		this.qFactor = qFactor;
	}

/*
 * This constructor allows you specify custom settings for your client (cluster's name, ping timeout, etc).
 * @param		settings		-	the settings for your client node
 * @param		qFactor			-	the quantization factor used to obtain the encoded text for the features
 */
	public ElasticImageIndexManager(Settings settings, int qFactor) {
		super(settings);
		this.qFactor = qFactor;
	}





/*
 * It loads the ImgDescriptor objects from the specified file and stores them in the member variable.
 * @param		storageFile		-	the file where the ImgDescriptor objects have been previously stored
 * @throws		IOException, ClassNotFoundException if there are errors reading the file
 */
	public void loadImgDescriptorsFromFile(File storageFile) throws IOException, ClassNotFoundException {
		imgDataset = FeaturesStorage.load(storageFile);
	}

/*
 * It extracts the deep features for all the images contained in the folder, stores them in ImgDescriptor objects and
 * finally stores them in the output file.
 * @param		imgSrcFolder	-	the folder that contains all the images of the dataset
 * @param		storageFile		-	the file where the extracted deep features will be stored
 * @throws		IOException if something goes wrong while loading or storing the features
 */
	public void extractAndLoad(File imgSrcFolder, File storageFile) throws IOException {
		imgDataset = SeqImageStorage.extractFeatures(imgSrcFolder);
		FeaturesStorage.store(imgDataset, storageFile);
	}

/*
 * It allows to index the whole image dataset. It performs the indexing sending a single request to the node.
 * @param		indexName		-	the name of the index
 * @param		typeName		-	the name of the type
 * @throws		IOException, InterruptedException if something goes wrong during the creation of the JSON document
 */
	public void indexImgDataset(String indexName, String typeName) throws IOException, InterruptedException {
		Map<String, String> idJsonDocMap = new HashMap<>();

		for(ImgDescriptor imgDesc : imgDataset)
			idJsonDocMap.put(imgDesc.getId(), JsonImageBuilder.build(DeepFeatureEncoder.encode(imgDesc.getFeatures(), qFactor),
																		imgDesc.getTags(),
																		imgDesc.getUri()));
		bulkIndex(indexName, typeName, idJsonDocMap, true);
	}

	
	

	
/*
 * It performs a visual similarity search using an already indexed image as query.
 * @param		indexName		-	the name of the index
 * @param		typeName		-	the name of the type
 * @param		queryImgId		-	the id of the image used as query
 * @param		k				-	result's size
 * @throws		something goes wrong
 * @return		it returns a list containing the k most relevant images
 */
	public List<ImgDescriptor> visualSimilaritySearch(String indexName, String typeName, String queryImgId, int k) throws IOException, ClassNotFoundException, JsonDocParserFieldNotFoundException {
		List<ImgDescriptor> result = new ArrayList<ImgDescriptor>(k);
		ImgDescriptor imgDesc;
		
		GetResponse getRespQuery = get(indexName, typeName, queryImgId);
		String imgTxt = JsonDocParser.getStringFieldValue(getRespQuery, Fields.IMG);
		
		SearchResponse searchResp = super.queryStringSearch(indexName, typeName, Fields.IMG, imgTxt, k);
		SearchHit[] srcHits = searchResp.getHits().hits();
		
		for(int i=0; i<srcHits.length; i++) {
			imgDesc = new ImgDescriptor(srcHits[i].getId(), JsonDocParser.getStringFieldValue(srcHits[i], Fields.TAGS), JsonDocParser.getStringFieldValue(srcHits[i], Fields.URI));
			imgDesc.setDist(srcHits[i].getScore());
			result.add(imgDesc);
		}
		
		return result;
	}

/*
 * It performs a visual similarity search using an already indexed image as query with the query reduction mechanism.
 * The query reduction mechanism takes into account only the n most significant features of the image and executes the
 * query considering only those components. This is done to speed up the query execution time.
 * @param		indexName		-	the name of the index
 * @param		typeName		-	the name of the type
 * @param		queryImgId		-	the id of the image used as query
 * @param		n				-	query reduction's size
 * @param		k				-	result's size
 * @throws		something goes wrong
 * @return		it returns a list containing the k most relevant images
 */
	public List<ImgDescriptor> visualSimilaritySearchWithQueryReduction(String indexName, String typeName, String queryImgId, int n, int k) throws IOException, ClassNotFoundException, JsonDocParserFieldNotFoundException {
		List<ImgDescriptor> result = new ArrayList<ImgDescriptor>(k);
		ImgDescriptor imgDesc;

		String imgTxt = reduceQuery(indexName, typeName, queryImgId, n);

		SearchResponse searchResp = super.queryStringSearch(indexName, typeName, Fields.IMG, imgTxt, k);
		SearchHit[] srcHits = searchResp.getHits().hits();

		for(int i=0; i<srcHits.length; i++) {
			imgDesc = new ImgDescriptor(srcHits[i].getId(), JsonDocParser.getStringFieldValue(srcHits[i], Fields.TAGS), JsonDocParser.getStringFieldValue(srcHits[i], Fields.URI));
			imgDesc.setDist(srcHits[i].getScore());
			result.add(imgDesc);
		}

		return result;
	}

/*
 * It performs the query reduction.
 * @param		indexName		-	the name of the index
 * @param		typeName		-	the name of the type
 * @param		queryImgId		-	the id of the image used as query
 * @param		n				-	query reduction's size
 * @throws		something goes wrong
 * @return		it returns the encoded image text for the reducted query
 */
	private String reduceQuery(String indexName, String typeName, String queryImgId, int n) throws IOException {
		PostingsEnum postings = null;
		String reducedQuery = "";

		TermVectorsResponse termVectorsResponse = client.prepareTermVectors()
				.setIndex(indexName)
				.setType(typeName)
				.setId(queryImgId)
				.get();

		TermsEnum iterator = termVectorsResponse.getFields().terms(Fields.IMG).iterator();

		List<String> termVectorList = new ArrayList<>();
		while(iterator.next() != null) {
			postings = iterator.postings(postings, PostingsEnum.FREQS);
			termVectorList.add(postings.freq() + ";" + iterator.term().utf8ToString());
		}
		Collections.sort(termVectorList, Collections.reverseOrder());

		for (int i=0; i<n && i<termVectorList.size(); i++) {
			String[] freqTerm = termVectorList.get(i).split(";");
			reducedQuery += new String(new char[(int)Integer.parseInt(freqTerm[0])]).replace("\0", freqTerm[1] + " ");
		}

		return reducedQuery;
	}
}

package it.unipi.ing.drsptr.main;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unipi.ing.drsptr.elastic.img.ElasticImageIndexManager;
import it.unipi.ing.drsptr.elastic.img.tools.JsonImageBuilder;
import it.unipi.ing.drsptr.elastic.txt.ElasticIndexManager;
import it.unipi.ing.drsptr.elastic.utilities.JsonDocParserFieldNotFoundException;
<<<<<<< HEAD
import it.unipi.ing.drsptr.elastic.utilities.MappingBuilder;
import it.unipi.ing.drsptr.lucene.LuceneIndexReader;
=======
>>>>>>> origin/master
import it.unipi.ing.mim.deep.ImgDescriptor;
import it.unipi.ing.mim.deep.tools.Output;
import org.elasticsearch.action.bulk.BulkResponse;

public class ElasticCBIR {

<<<<<<< HEAD
	private static void importLuceneIndex(String lucenePath, List<String> storedFields, List<String> termVectorFields) throws IOException, InterruptedException {
		ElasticIndexManager indexManager = new ElasticIndexManager();
		indexManager.connectTo(ElasticCBIRParameters.LOOPBACK_ADDRESS, ElasticIndexManager.DEFAULT_PORT);
		indexManager.createIndex(ElasticCBIRParameters.INDEX_NAME, ElasticCBIRParameters.INDEX_SETTINGS);
		indexManager.putMapping(ElasticCBIRParameters.INDEX_NAME, ElasticCBIRParameters.INDEX_TYPE, MappingBuilder.build(ElasticCBIRParameters.MAPPING_FIELDS));
		LuceneIndexReader indexReader = new LuceneIndexReader(lucenePath, storedFields, termVectorFields);
		int docsToProcess = indexReader.getNumDocuments(), processedDocs;
		Map<String, String> idJsonMap = new HashMap<>();
		boolean failures = false;

		for(processedDocs = 0; processedDocs <= docsToProcess; processedDocs++) {
			System.out.print("Indexing.. " + processedDocs + "/" + docsToProcess + "\tFailures: " + failures);
			if(!idJsonMap.isEmpty() && (processedDocs % 300000 == 0 || processedDocs >= docsToProcess)) {
				BulkResponse bulkResponse = indexManager.bulkIndex(ElasticCBIRParameters.INDEX_NAME, ElasticCBIRParameters.INDEX_TYPE, idJsonMap, true);
				failures |= bulkResponse.hasFailures();
				idJsonMap.clear();
				if(processedDocs >= docsToProcess) break;
			}

			Map<String, Object> result = indexReader.readDocument(processedDocs);
			String docId = (String)result.get(ElasticCBIRParameters.LUCENE_FIELDS_ID);
			String tags = (String)result.get(ElasticCBIRParameters.LUCENE_FIELDS_TAGS);
			String uri = (String)result.get(ElasticCBIRParameters.LUCENE_FIELDS_URI);
			String img = LuceneIndexReader.getTextFromTermVector(( Map<String, Long>)result.get(ElasticCBIRParameters.LUCENE_FIELDS_IMG));
			idJsonMap.put(docId, JsonImageBuilder.build(img, tags, uri));

			System.out.print("\r");
		}
		indexManager.close();
=======
	private static void importLuceneIndex() throws IOException {

>>>>>>> origin/master
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, JsonDocParserFieldNotFoundException, InterruptedException {
		/*ElasticImageIndexManager esImgManager = new ElasticImageIndexManager(ElasticCBIRParameters.STORAGE_FILE, ElasticCBIRParameters.Q);

		esImgManager.connectTo(ElasticCBIRParameters.LOOPBACK_ADDRESS, ElasticIndexManager.DEFAULT_PORT);
		
		//esImgManager.deleteIndex(ElasticCBIRParameters.INDEX_NAME);

		esImgManager.bulkIndexImgDataset(ElasticCBIRParameters.INDEX_NAME, ElasticCBIRParameters.INDEX_TYPE);
		
		esImgManager.forceRefresh(ElasticCBIRParameters.INDEX_NAME);

		List<ImgDescriptor> resLucene = esImgManager.search(ElasticCBIRParameters.INDEX_NAME, ElasticCBIRParameters.INDEX_TYPE, ElasticCBIRParameters.SRC_IMG, ElasticCBIRParameters.K);
		Output.toHTML(resLucene, ElasticCBIRParameters.BASE_URI, ElasticCBIRParameters.RESULTS_HTML_LUCENE);

		esImgManager.close();*/
		importLuceneIndex(ElasticCBIRParameters.LUCENE_INDEX_PATH, ElasticCBIRParameters.LUCENE_FIELDS_STORED, ElasticCBIRParameters.LUCENE_FIELDS_TV);
	}
}
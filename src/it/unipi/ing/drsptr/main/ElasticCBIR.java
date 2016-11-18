package it.unipi.ing.drsptr.main;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unipi.ing.drsptr.elastic.img.ElasticImageIndexManager;
import it.unipi.ing.drsptr.elastic.img.tools.JsonImageBuilder;
import it.unipi.ing.drsptr.elastic.txt.ElasticIndexManager;
import it.unipi.ing.drsptr.elastic.utilities.JsonDocParserFieldNotFoundException;
import it.unipi.ing.drsptr.elastic.utilities.MappingBuilder;
import it.unipi.ing.drsptr.lucene.LuceneIndexReader;
import it.unipi.ing.mim.deep.ImgDescriptor;
import it.unipi.ing.mim.deep.tools.Output;
import org.elasticsearch.action.bulk.BulkResponse;

/*
 * ElasticCBIR is the main class.
 * @author		Pietro De Rosa
 */
public class ElasticCBIR {

	private static void importLuceneIndex(String lucenePath, List<String> storedFields, List<String> termVectorFields) throws IOException, InterruptedException {
		ElasticIndexManager indexManager = new ElasticIndexManager();
		indexManager.connectTo(ElasticCBIRParameters.LOOPBACK_ADDRESS, ElasticIndexManager.DEFAULT_PORT);
		indexManager.createIndex(ElasticCBIRParameters.INDEX_NAME, ElasticCBIRParameters.INDEX_SETTINGS);
		indexManager.putMapping(ElasticCBIRParameters.INDEX_NAME, ElasticCBIRParameters.TYPE_NAME, MappingBuilder.build(ElasticCBIRParameters.MAPPING_FIELDS));
		LuceneIndexReader indexReader = new LuceneIndexReader(lucenePath, storedFields, termVectorFields);
		int docsToProcess = 100000/*indexReader.getNumDocuments()*/, processedDocs;
		Map<String, String> idJsonMap = new HashMap<>();
		boolean failures = false;

		for(processedDocs = 0; processedDocs <= docsToProcess; processedDocs++) {
			System.out.print("Indexing.. " + processedDocs + "/" + docsToProcess + "\tFailures: " + failures);
			if(!idJsonMap.isEmpty() && (processedDocs % 10000 == 0 || processedDocs >= docsToProcess)) {
				BulkResponse bulkResponse = indexManager.bulkIndex(ElasticCBIRParameters.INDEX_NAME, ElasticCBIRParameters.TYPE_NAME, idJsonMap, true);
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
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, JsonDocParserFieldNotFoundException, InterruptedException {
		ElasticImageIndexManager esImgManager = new ElasticImageIndexManager(ElasticCBIRParameters.Q);

		esImgManager.connectTo(ElasticCBIRParameters.LOOPBACK_ADDRESS, ElasticIndexManager.DEFAULT_PORT);

		/*List<ImgDescriptor> result = esImgManager.visualSearch(ElasticCBIRParameters.INDEX_NAME,
																ElasticCBIRParameters.TYPE_NAME,
																ElasticCBIRParameters.SRC_IMG,
																ElasticCBIRParameters.K);*/

		List<ImgDescriptor> result = esImgManager.visualSearchQRReordered(ElasticCBIRParameters.INDEX_NAME,
																			ElasticCBIRParameters.TYPE_NAME,
																							ElasticCBIRParameters.SRC_IMG,
																							ElasticCBIRParameters.Lq,
																							ElasticCBIRParameters.K,
																							ElasticCBIRParameters.Cr);
		Output.toHTML(result, ElasticCBIRParameters.BASE_URI, ElasticCBIRParameters.RESULTS_HTML_LUCENE);

		esImgManager.close();
		//importLuceneIndex(ElasticCBIRParameters.LUCENE_INDEX_PATH, ElasticCBIRParameters.LUCENE_FIELDS_STORED, ElasticCBIRParameters.LUCENE_FIELDS_TV);
	}
}
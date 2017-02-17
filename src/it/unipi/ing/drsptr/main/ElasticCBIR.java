package it.unipi.ing.drsptr.main;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import it.unipi.ing.drsptr.elastic.img.ElasticImageIndexManager;
import it.unipi.ing.drsptr.elastic.img.tools.Fields;
import it.unipi.ing.drsptr.elastic.img.tools.JsonImageBuilder;
import it.unipi.ing.drsptr.elastic.txt.ElasticIndexManager;
import it.unipi.ing.drsptr.elastic.utilities.JsonDocParserFieldNotFoundException;
import it.unipi.ing.drsptr.elastic.utilities.MappingBuilder;
import it.unipi.ing.drsptr.lucene.LuceneIndexReader;
import it.unipi.ing.mim.deep.ImgDescriptor;
import it.unipi.ing.mim.deep.tools.Output;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.elasticsearch.action.bulk.BulkResponse;

/**
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
		int docsToProcess = indexReader.getNumAllDocuments(), processedDocs;
		Map<String, String> idJsonMap = new HashMap<>();

		for(processedDocs = 0; processedDocs <= 100000; processedDocs++) {
			if(!idJsonMap.isEmpty() && (processedDocs % 100000 == 0 || processedDocs >= docsToProcess)) {
				BulkResponse bulkResponse = indexManager.bulkIndex(ElasticCBIRParameters.INDEX_NAME, ElasticCBIRParameters.TYPE_NAME, idJsonMap, true);
				idJsonMap.clear();
				if(processedDocs >= docsToProcess) break;
				System.out.println((processedDocs - 100000) + "/" + processedDocs + "\tFailures: " + bulkResponse.hasFailures() + "\tDocs:" + indexManager.getDocumentCount(ElasticCBIRParameters.INDEX_NAME, ElasticCBIRParameters.TYPE_NAME));
			}

			Map<String, Object> result = indexReader.readDocument(processedDocs);

			if(result == null)	// checks if a deleted document has been returned
				continue;

			String docId = (String)result.get(ElasticCBIRParameters.LUCENE_FIELDS_ID);
			String tags = (String)result.get(ElasticCBIRParameters.LUCENE_FIELDS_TAGS);
			String uri = (String)result.get(ElasticCBIRParameters.LUCENE_FIELDS_URI);
			String img = LuceneIndexReader.getTextFromTermVector(( Map<String, Long>)result.get(ElasticCBIRParameters.LUCENE_FIELDS_IMG));
			idJsonMap.put(docId, JsonImageBuilder.build(img, tags, uri));
		}
		indexManager.close();
	}

	private static boolean importIndex(ElasticImageIndexManager imgIndexManager) throws IOException, InterruptedException {
		imgIndexManager.createIndex(ElasticCBIRParameters.INDEX_NAME, ElasticCBIRParameters.INDEX_SETTINGS);
		imgIndexManager.putMapping(ElasticCBIRParameters.INDEX_NAME,
									ElasticCBIRParameters.TYPE_NAME,
									MappingBuilder.build(ElasticCBIRParameters.MAPPING_FIELDS));
		LuceneIndexReader indexReader = new LuceneIndexReader(ElasticCBIRParameters.LUCENE_INDEX_PATH,
				ElasticCBIRParameters.LUCENE_FIELDS_STORED,
				ElasticCBIRParameters.LUCENE_FIELDS_TV);

		imgIndexManager.initBulkProcessor(ElasticCBIRParameters.BULK_PROCESSOR_SETTINGS);

		int docToProcess = 100000; //indexReader.getNumDocuments();

		for(int i=0; i<docToProcess; i++) {
			Map<String, Object> result = indexReader.readDocument(i);
			String docId = (String)result.get(ElasticCBIRParameters.LUCENE_FIELDS_ID);
			String tags = (String)result.get(ElasticCBIRParameters.LUCENE_FIELDS_TAGS);
			String uri = (String)result.get(ElasticCBIRParameters.LUCENE_FIELDS_URI);
			String img = LuceneIndexReader.getTextFromTermVector(( Map<String, Long>)result.get(ElasticCBIRParameters.LUCENE_FIELDS_IMG));
			imgIndexManager.indexDocBulkProcessor(ElasticCBIRParameters.INDEX_NAME,
					ElasticCBIRParameters.TYPE_NAME, docId, JsonImageBuilder.build(img, tags, uri));
		}

		return imgIndexManager.awaitCloseBulkProcessor(2, TimeUnit.MINUTES);
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, JsonDocParserFieldNotFoundException, InterruptedException {
		ElasticImageIndexManager esImgManager = new ElasticImageIndexManager(ElasticCBIRParameters.Q);

		esImgManager.connectTo(ElasticCBIRParameters.LOOPBACK_ADDRESS, ElasticIndexManager.DEFAULT_PORT);

		//importIndex(esImgManager);

		long startTime= System.currentTimeMillis();

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

		/*List<ImgDescriptor> result = esImgManager.visualSearchQR(ElasticCBIRParameters.INDEX_NAME,
				ElasticCBIRParameters.TYPE_NAME,
				ElasticCBIRParameters.SRC_IMG,
				ElasticCBIRParameters.Lq,
				ElasticCBIRParameters.K);*/

		System.out.println(((double)(System.currentTimeMillis() - startTime))/1000 + "s");
		Output.toHTML(result, ElasticCBIRParameters.RESULTS_HTML);

		esImgManager.close();
		//importLuceneIndex(ElasticCBIRParameters.LUCENE_INDEX_PATH, ElasticCBIRParameters.LUCENE_FIELDS_STORED, ElasticCBIRParameters.LUCENE_FIELDS_TV);

		//esImgManager.setReplicas(0);
	}
}
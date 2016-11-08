package it.unipi.ing.drsptr.main;

import java.io.IOException;
import java.util.List;

import it.unipi.ing.drsptr.elastic.img.ElasticImageIndexManager;
import it.unipi.ing.drsptr.elastic.txt.ElasticIndexManager;
import it.unipi.ing.drsptr.elastic.utilities.JsonDocParserFieldNotFoundException;
import it.unipi.ing.mim.deep.ImgDescriptor;
import it.unipi.ing.mim.deep.tools.Output;

public class ElasticCBIR {

	private static void importLuceneIndex() throws IOException {

	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, JsonDocParserFieldNotFoundException, InterruptedException {
		ElasticImageIndexManager esImgManager = new ElasticImageIndexManager(ElasticCBIRParameters.STORAGE_FILE, ElasticCBIRParameters.Q);

		esImgManager.connectTo(ElasticCBIRParameters.LOOPBACK_ADDRESS, ElasticIndexManager.DEFAULT_PORT);
		
		//esImgManager.deleteIndex(ElasticCBIRParameters.INDEX_NAME);

		esImgManager.bulkIndexImgDataset(ElasticCBIRParameters.INDEX_NAME, ElasticCBIRParameters.INDEX_TYPE);
		
		esImgManager.forceRefresh(ElasticCBIRParameters.INDEX_NAME);

		List<ImgDescriptor> resLucene = esImgManager.search(ElasticCBIRParameters.INDEX_NAME, ElasticCBIRParameters.INDEX_TYPE, ElasticCBIRParameters.SRC_IMG, ElasticCBIRParameters.K);
		Output.toHTML(resLucene, ElasticCBIRParameters.BASE_URI, ElasticCBIRParameters.RESULTS_HTML_LUCENE);

		esImgManager.close();
	}
}
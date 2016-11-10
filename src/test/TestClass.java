package test;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.net.InetAddress;

import org.elasticsearch.common.settings.Settings;

import it.unipi.ing.drsptr.elastic.img.tools.Fields;
import it.unipi.ing.drsptr.elastic.txt.ElasticIndexManager;
import it.unipi.ing.drsptr.elastic.utilities.MappingBuilder;
import it.unipi.ing.drsptr.main.ElasticCBIRParameters;

public class TestClass {

	public static void main(String[] args) throws IOException {
		ElasticIndexManager indexManager = new ElasticIndexManager();
		
		indexManager.connectTo(InetAddress.getLoopbackAddress(), 9300);
		
		indexManager.createIndex("test", ElasticCBIRParameters.INDEX_SETTINGS);

		indexManager.putMapping("test", "test", MappingBuilder.build(ElasticCBIRParameters.MAPPING_FIELDS));

		String jsonDoc = jsonBuilder()
					.startObject()
						.field(Fields.IMG, "f1 f1 f1 f2 f2 f3 f4 f5 f6 f6 f6 f7")
						.field(Fields.TAGS, "sunset sea beach")
						.field(Fields.URI, "http://www.google.it")
					.endObject()
				.string();
		indexManager.index("test", "test", "1", jsonDoc);
		
		indexManager.close();
	}

}

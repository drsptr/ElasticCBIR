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
		
<<<<<<< HEAD
		indexManager.createIndex("test", ElasticCBIRParameters.INDEX_SETTINGS);

		indexManager.putMapping("test", "test", MappingBuilder.build(ElasticCBIRParameters.MAPPING_FIELDS));

=======
		indexManager.createIndex("test", Settings.builder()
													.put("index.number_of_shards", 1)
													.put("index.number_of_replicas", 0));

		indexManager.putMapping("test", "test", MappingBuilder.build(ElasticCBIRParameters.MAPPING_FIELDS));
		/*
		Map<String, Map<String, String>> fields = new HashMap<>();
		for(int i=0; i<3; i++) {
			Map<String, String> properties = new HashMap<>();
			properties.put("type", "string");
			properties.put("term_vector", "yes");
			fields.put("field" + i, properties);
		}
		indexManager.putMapping("test", "test_type", MappingBuilder.build(fields));*/
		
>>>>>>> origin/master
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

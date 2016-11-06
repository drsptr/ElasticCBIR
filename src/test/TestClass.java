package test;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;

import it.unipi.ing.drsptr.elastic.img.tools.Fields;
import it.unipi.ing.drsptr.elastic.txt.ElasticIndexManager;
import it.unipi.ing.drsptr.elastic.utilities.MappingBuilder;

public class TestClass {

	public static void main(String[] args) throws IOException {
		ElasticIndexManager indexManager = new ElasticIndexManager();
		
		indexManager.connectTo(InetAddress.getLoopbackAddress(), 9300);
		
		indexManager.createIndex("test", Settings.builder()
													.put("index.number_of_shards", 1)
													.put("index.number_of_replicas", 0));
		
		
		Map<String, Map<String, String>> fields = new HashMap<>();
		for(int i=0; i<3; i++) {
			Map<String, String> properties = new HashMap<>();
			properties.put("type", "string");
			properties.put("term_vector", "yes");
			fields.put("field" + i, properties);
		}
		indexManager.putMapping("test", "test_type", MappingBuilder.build(fields));
		
		String jsonDoc = jsonBuilder()
				.startObject()
				.field("field0", "ciao")
				.field("field1", "hello")
				.field("field2", "hi")
				.endObject()
				.string();
		indexManager.index("test", "test", "1", jsonDoc);
		
		indexManager.close();
	}

}

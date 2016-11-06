package it.unipi.ing.drsptr.elastic.utilities;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.common.xcontent.XContentBuilder;


public class MappingBuilder {
	
	public static String build(Map<String, Map<String, String>> fields) throws IOException {
		
		XContentBuilder builder = jsonBuilder().startObject();
		
		builder.field("properties");
		builder.startObject();
		
		for(String field : fields.keySet())
			builder.field(field, fields.get(field));
			
		builder.endObject(); // properties
		builder.endObject(); //JSON
		
		return builder.string();
	}
}

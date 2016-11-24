package it.unipi.ing.drsptr.elastic.img.tools;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.common.xcontent.XContentBuilder;

import it.unipi.ing.mim.deep.ImgDescriptor;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * JsonImageBuilder is a class with only a static method that allows to build the JSON document, with the corresponding
 * fields, for a given image.
 * @author		Pietro De Rosa
 */
public class JsonImageBuilder {

/**
 * It allows to build the JSON document for a given image.
 * @param		imgField		-	the encoded text of the image
 * @param		tagsField		-	the text description of the image
 * @param		uriField		-	the uri of the image
 * @throws		IOException if some goes wrong during the creation of the JSON document
 * @return		the JSON document representing the image
 */
	public static String build(String imgField, String tagsField, String uriField) throws IOException {
		XContentBuilder builder = jsonBuilder()
									.startObject()
										.field(Fields.IMG, imgField)
										.field(Fields.TAGS, tagsField)
										.field(Fields.URI, uriField)
									.endObject();

		return builder.string();
	}
}

package it.unipi.ing.drsptr.elastic.img.tools;

import java.io.IOException;

import org.elasticsearch.common.xcontent.XContentBuilder;

import it.unipi.ing.mim.deep.ImgDescriptor;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class JsonImageBuilder {
	
	public static String imageToJson(ImgDescriptor imgDesc, int qFactor) throws IOException {
		String imgTxt = DeepFeatureEncoder.encode(imgDesc.getFeatures(), qFactor);
		XContentBuilder builder = jsonBuilder()
									.startObject()
									.field(Fields.IMG, imgTxt)
									.field(Fields.BIN, imgDesc.toBytes())
									.endObject();
		return builder.string();
	}
}

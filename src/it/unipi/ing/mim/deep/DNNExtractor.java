package it.unipi.ing.mim.deep;

import static org.bytedeco.javacpp.opencv_core.CV_32FC3;
import static org.bytedeco.javacpp.opencv_core.subtract;
import static org.bytedeco.javacpp.opencv_dnn.createCaffeImporter;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_UNCHANGED;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.resize;

import java.io.File;

import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_dnn.Blob;
import org.bytedeco.javacpp.opencv_dnn.Importer;
import org.bytedeco.javacpp.opencv_dnn.Net;

public class DNNExtractor {

	private Mat meanImg;
	private Net net;
	private Size imgSize;
	
	public DNNExtractor() {		
			//Create the importer of Caffe framework network
			Importer importer = createCaffeImporter(new File(Parameters.DEEP_PROTO).getAbsolutePath(), new File(Parameters.DEEP_MODEL).getAbsolutePath());
			
			//Initialize the network
			net = new Net();
			
			//Add loaded layers into the net and sets connections between them
			importer.populateNet(net);
	        importer.close();
	        
	        imgSize = new Size(Parameters.IMG_WIDTH, Parameters.IMG_HEIGHT);

			meanImg = imread(new File(Parameters.DEEP_MEAN_IMG).getAbsolutePath());
			meanImg.convertTo(meanImg, CV_32FC3);
			resize(meanImg, meanImg, imgSize);
	}

	public float[] extract(File image, String layer) {
		Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_UNCHANGED);
		return extract(img, layer);
	}

	public float[] extract(Mat img, String layer) {
		img.convertTo(img, CV_32FC3);
		resize(img, img, imgSize);
		if (meanImg != null) {
			subtract(img, meanImg, img);
		}
		
		// Convert Mat to dnn::Blob image batch
		Blob inputBlob = new Blob(img);

		// set the network input
		net.setBlob(".data", inputBlob);
		
		// compute output
		net.forward();

		// gather output of "fc7" layer
		Blob prob = net.getBlob(layer);

		FloatPointer fp = prob.ptrf();

		float[] features = new float[(int) prob.total()];
		fp.get(features);
		return features;
	}
}

package it.unipi.ing.mim.deep;

public class Parameters {
	
	//DEEP parameters
	public static final String DEEP_PROTO = "data/caffe/train_val.prototxt";
	public static final String DEEP_MODEL = "data/caffe/bvlc_reference_caffenet.caffemodel";
	public static final String DEEP_MEAN_IMG = "data/caffe/meanImage.png";
	
	public static final String DEEP_LAYER = "fc7";
	public static final int IMG_WIDTH = 227;
	public static final int IMG_HEIGHT = 227;
}

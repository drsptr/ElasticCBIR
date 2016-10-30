package it.unipi.ing.mim.deep.seq;

import it.unipi.ing.mim.deep.DNNExtractor;
import it.unipi.ing.mim.deep.ImgDescriptor;
import it.unipi.ing.mim.deep.Parameters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SeqImageStorage {

	public static List<ImgDescriptor> extractFeatures(File imgFolder){
		List<ImgDescriptor>  descs = new ArrayList<ImgDescriptor>();

		File[] files = imgFolder.listFiles();
		
		DNNExtractor extractor = new DNNExtractor();

		for (int i = 0; i < files.length; i++) {
			System.out.println(i + " - extracting " + files[i].getName());
			try {
				long time = -System.currentTimeMillis();
				float[] features = extractor.extract(files[i], Parameters.DEEP_LAYER);
				time += System.currentTimeMillis();
				System.out.println(time);
				descs.add(new ImgDescriptor(features, files[i].getName()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return descs;	
	}		
}

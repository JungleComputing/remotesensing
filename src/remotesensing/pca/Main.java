package remotesensing.pca;

import remotesensing.util.Image;
import remotesensing.util.Utils;

public class Main {

	public static void main(String [] args) throws Exception { 

		String headerIn = args[0];
		String dataIn = args[1];
		int components = Integer.parseInt(args[2]);
		
		String headerOut = args[3];
		String dataOut = args[4];
				
		long start = System.currentTimeMillis();
		
		Image image = Utils.readENVI(headerIn, dataIn);		

		long mid = System.currentTimeMillis();

		System.out.println("Reading took " + (mid-start)/1000.0 + " sec.");

		PCA pca = new PCA(image, components);
		pca.process();
		Image result = pca.getResult();
		
		long end = System.currentTimeMillis();

		System.out.println("PCA took " + (end-start)/1000.0 + " sec.");
		
		Utils.writeENVI(headerOut, dataOut, result);		
		
		/*
		PngEncoder enc = new PngEncoder(result);
		byte [] out = enc.pngEncode(false);
		
		FileOutputStream fout = new FileOutputStream("ppi.png");
		fout.write(out);
		fout.close();
		*/
	}
}

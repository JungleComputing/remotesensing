package remotesensing.ppi;

import java.io.FileOutputStream;

import remotesensing.util.Image;
import remotesensing.util.NumberRangeToRGBConversion;
import remotesensing.util.PngEncoder;
import remotesensing.util.S16Image;
import remotesensing.util.Utils;

public class Main {

	public static void main(String [] args) throws Exception { 

		String header = args[0];
		String data = args[1];
		int skewers = Integer.parseInt(args[2]);

		long start = System.currentTimeMillis();

		Image image = Utils.readENVI(header, data);

		PPI ppi = new PPI(image, skewers);		

		long mid = System.currentTimeMillis();

		System.out.println("Reading took " + (mid-start)/1000.0 + " sec.");

		ppi.run();

		long end = System.currentTimeMillis();

		System.out.println("PPI took " + (end-start)/1000.0 + " sec.");

		S16Image result = ppi.getResultImage();

		PngEncoder enc = new PngEncoder(result);
		byte [] out = enc.pngEncode(false, 
				new NumberRangeToRGBConversion(ppi.getResultMINCount(), 
						                       ppi.getResultMAXCount()));

		FileOutputStream fout = new FileOutputStream("ppi.png");
		fout.write(out);
		fout.close();
	}
}

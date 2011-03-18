package remotesensing.ppi;

import java.io.IOException;

import remotesensing.util.UnsignedShortImage;
import remotesensing.util.Utils;

public class Main {

	public static void main(String [] args) throws IOException { 
	
		String header = args[0];
		String data = args[1];
		int skewers = Integer.parseInt(args[2]);
		
		long start = System.currentTimeMillis();
		
		UnsignedShortImage image = (UnsignedShortImage) Utils.readENVI(header, data);
		
		PPI ppi = new PPI(image, skewers);		
		
		long mid = System.currentTimeMillis();
		
		System.out.println("Reading took " + (mid-start)/1000.0 + " sec.");
		
		ppi.run();
		
		long end = System.currentTimeMillis();
		
		System.out.println("PPI took " + (end-start)/1000.0 + " sec.");
		
		UnsignedShortImage result = ppi.getResultImage();
				
		
	}
}

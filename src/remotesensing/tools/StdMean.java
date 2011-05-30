package remotesensing.tools;

import remotesensing.util.Image;
import remotesensing.util.Utils;

public class StdMean {

	public static void main(String [] args) throws Exception { 
		
		String headerIn = args[0];
		String dataIn = args[1];
		
		Image image = Utils.readENVI(headerIn, dataIn);		

		System.out.println("Image resolution: " + image.samples + " samples, " + image.lines + " lines, " + image.bands + " bands.");
		
		double [] stddev = new double[image.bands];
		double [] mean   = new double[image.bands];
		
		Utils.bandwiseStandardDeviationAndMean(image, stddev, mean);
		
		System.out.println("Mean/standard deviation per band: ");
				
		for (int i=0;i<image.bands;i++) { 
			System.out.println("  " + i + " " + mean[i] + " " + stddev[i]);
		}
	}
	
}

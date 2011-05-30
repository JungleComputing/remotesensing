package remotesensing.tools;

import remotesensing.util.Image;
import remotesensing.util.Pixel;
import remotesensing.util.Utils;

public class ExtractPixel {

	public static void main(String [] args) throws Exception { 
		
		String headerIn = args[0];
		String dataIn = args[1];

		int pixel = Integer.parseInt(args[2]);

		Image image = Utils.readENVI(headerIn, dataIn);

		float [][] spectralCalibration = null;
		
		if (args.length > 3) { 
			spectralCalibration = Utils.readSpectralCalibration(args[3], image.bands);
		} else {
			
			spectralCalibration = new float[image.bands][4];
			
			for (int i=0;i<image.bands;i++) {
				spectralCalibration[i][0] = i;
			}
		}
		
		Pixel p = image.getPixel(pixel);

		for (int i=0;i<p.bands;i++) {
			System.out.println(spectralCalibration[i][0] + " " + p.getAsReal(i));
		}
	}
	
}

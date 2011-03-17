package remotesensing.ppi;

import remotesensing.util.UnsignedShortImage;
import remotesensing.util.UnsignedShortPixel;

public class PPI {

	private final UnsignedShortImage input;
	private final UnsignedShortPixel [] skewers;
	private final int [] result; 
	
	public PPI(UnsignedShortImage input, int skewers) {		
		// NOTE: assumes BSQ image storage.
		this(input, generateSkewers(skewers, input.getBands()));
    }
	
	public PPI(UnsignedShortImage input, UnsignedShortPixel [] skewers) {		
		// NOTE: assumes BSQ image storage. 		
		this.input = input;
		this.skewers = skewers;		
		this.result = new int[skewers.length*2];
    }

	private static UnsignedShortPixel [] generateSkewers(int skewers, int bands) {
		// TODO implement!
		return null;
	}	
	
	private void processSkewer(int index) { 
		
		final int pixels = input.getLines() * input.getSamples();
		final int bands = input.getBands();
		
		final short [] img = input.getData();
		final short [] pix = skewers[index].getData();		
		
		double peMAX = Double.MIN_VALUE;
		double peMIN = Double.MAX_VALUE;
		
		int indexMAX = 0;
		int indexMIN = 0;
		
		for (int i=0;i<pixels;i++) { 
			
			double pe = 0.0;
			
			for (int b=0;b<bands;b++) { 
				pe += pix[b] * img[i+pixels*b];												
			}
			
			if (pe > peMAX) { 
				peMAX = pe;
				indexMAX = i;
			}
			
			if (pe < peMIN) { 
				peMIN = pe;
				indexMIN = i;
			}
		}
		
		result[index*2] = indexMAX;
		result[index*2+1] = indexMIN;
	}
	
	public void run() { 
		
		
		for (int i=0;i<skewers.length;i++) { 
			processSkewer(i);
		}
		
		// Result now contains a set of candidate endmembers.  
		
		
		
		
	}
	
}

package remotesensing.ppi;

import java.lang.reflect.Array;
import java.util.Arrays;

import remotesensing.util.MersenneTwister;
import remotesensing.util.UnsignedShortImage;
import remotesensing.util.UnsignedShortPixel;

public class PPI {

	private final UnsignedShortImage input;
	private final UnsignedShortPixel [] skewers;
	
	private UnsignedShortImage resultImage;
	
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

	private static UnsignedShortPixel generateSkewer(MersenneTwister twister, int bands) {
		
		final short [] tmp = new short[bands];
		
		for (int i=0;i<bands;i++) { 
			// TODO: is this right ? Unsigned short should be between 0 and MAX_VALUE*2
			tmp[i] = (short) (twister.nextDouble() * Short.MAX_VALUE);
		}
		
		return new UnsignedShortPixel(tmp, bands);
	}
	
	private static UnsignedShortPixel [] generateSkewers(int skewers, int bands) {

		long start = System.currentTimeMillis();
		
		final UnsignedShortPixel [] tmp = new UnsignedShortPixel[skewers];
		final MersenneTwister twister = new MersenneTwister(42);
		
		
		for (int i=0;i<skewers;i++) { 
			tmp[i] = generateSkewer(twister, bands);
		}
		
		long end = System.currentTimeMillis();
		
		System.out.println("Generating skewers took : " + (end-start)/1000.0 + " sec.");
		
		return tmp;
	}	
	
	private void processSkewer(int index) { 
		
		long start = System.currentTimeMillis();
		
		final int pixels = input.getLines() * input.getSamples();
		final int bands = input.getBands();
		
		final short [] img = input.getData();
		final short [] pix = skewers[index].getData();		
		
		System.out.println("Skewer " + Arrays.toString(pix));
		
		double peMAX = Double.MIN_VALUE;
		double peMIN = Double.MAX_VALUE;
		
		int indexMAX = 0;
		int indexMIN = 0;
		
		for (int i=0;i<pixels;i++) { 
			
			double pe = 0.0;
			
			for (int b=0;b<bands;b++) { 
				// NOTE: don't forget the & 0xFFFF to ensure unsigned shorts!
				pe += (pix[b] & 0xFFFF) * (img[i+pixels*b] & 0xFFFF);												
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
		
		long end = System.currentTimeMillis();
		
		System.out.println("Skewer " + index + " " + indexMIN + " " + peMIN + " " + indexMAX + " " + peMAX + " took " + (end-start)/1000.0 + " sec. ");		
	}

	public UnsignedShortImage getResultImage() {
		return resultImage;
	}
	
	public void run() { 
		
		for (int i=0;i<skewers.length;i++) { 
			processSkewer(i);
		}

		short [] tmp = new short[input.getSamples() * input.getLines()]; 

		int max = 0;
		
		for (int i=0;i<result.length;i++) { 
			tmp[result[i]]++;
			
			if (tmp[result[i]] > max) { 
				max = tmp[result[i]];
			}
		}
	
		System.out.println("MAX: " + max);
		
		
		for (int i=0;i<input.getSamples() * input.getLines();i++) { 
			
			if (i % input.getSamples() == 0) { 
				System.out.println();
			}
			
			System.out.printf("%4d", tmp[i]);
		}
		
		resultImage = new UnsignedShortImage(tmp, input.getLines(), input.getSamples(), 1);
	}
	
}

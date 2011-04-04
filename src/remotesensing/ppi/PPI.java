package remotesensing.ppi;

import java.util.Arrays;

import remotesensing.util.Image;
import remotesensing.util.Image.DataType;
import remotesensing.util.MersenneTwister;
import remotesensing.util.Pixel;
import remotesensing.util.S16Image;

public class PPI {

	private final Image input;
	private final Pixel [] skewers;
	
	private S16Image resultImage;
	
	private int maxCount;
	private int minCount;
	
	public PPI(Image input, int skewers) {		
		this(input, generateSkewers(input.type, skewers, input.bands));
    }
	
	public PPI(Image input, Pixel [] skewers) {		
		// NOTE: assumes BSQ image storage. 		
		this.input = input;
		this.skewers = skewers;		
    }
	
	private static Pixel [] generateSkewers(DataType type, int skewers, int bands) {

		long start = System.currentTimeMillis();
		
		final Pixel [] tmp = new Pixel[skewers];
		final MersenneTwister twister = new MersenneTwister(42);
		
		for (int i=0;i<skewers;i++) {			
			tmp[i] = type.generateSkewer(twister, bands);
		}
		
		long end = System.currentTimeMillis();
		
		System.out.println("Generating skewers took : " + (end-start)/1000.0 + " sec.");
		
		return tmp;
	}	
	
	/*
	private void processSkewer(int index) { 
		
		long start = System.currentTimeMillis();
		
		final int pixels = input.lines * input.samples;
		final int bands = input.bands;
		
		
		
		
		
		
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
    */


	public S16Image getResultImage() {
		return resultImage;
	}
	
	public int getResultMAXCount() {
		return maxCount;
	}
	
	public int getResultMINCount() {
		return minCount;
	}
	
	public void run() { 
		
		int pixels = input.samples * input.lines;

		double [] max = new double[skewers.length]; 
		double [] min = new double[skewers.length];
		
		Arrays.fill(max, Double.MIN_VALUE);
		Arrays.fill(min, Double.MAX_VALUE);
		
		int [] maxIndex = new int[skewers.length];
		int [] minIndex = new int[skewers.length];		
		
		for (int i=0;i<pixels;i++) {
			
			Pixel p = input.getPixel(i);
			
			for (int j=0;j<skewers.length;j++) { 
			
				Pixel s = skewers[j];
				
			//	System.out.println(p + " . " + s);
				
				double value = p.dot(s);
				
				if (value > max[j]) { 
					max[j] = value;
					maxIndex[j] = i;
				}
				
				if (value < min[j]) { 
					min[j] = value;
					minIndex[j] = i;
				}
			}
		}

		resultImage = new S16Image(input.lines, input.samples, 1);
		short [] tmp = (short[]) resultImage.getData();
		
		maxCount = 0;
		minCount = Integer.MAX_VALUE;
		
		for (int i=0;i<maxIndex.length;i++) { 
			tmp[maxIndex[i]]++;
			
			if (tmp[maxIndex[i]] > maxCount) { 
				maxCount = tmp[maxIndex[i]];
			}
			
			tmp[minIndex[i]]++;
			
			if (tmp[minIndex[i]] < minCount) { 
				minCount = tmp[minIndex[i]];
			}
		}
	
		System.out.println("MAX: " + maxCount + " MIN: " + minCount);
		
		/*for (int i=0;i<input.samples * input.lines;i++) { 
			
			if (i % input.samples == 0) { 
				System.out.println();
			}
			
			System.out.printf("%4d", tmp[i]);
		}*/
	}
}

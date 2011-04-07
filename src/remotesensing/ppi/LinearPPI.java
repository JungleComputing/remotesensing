package remotesensing.ppi;

import java.util.Arrays;

import remotesensing.util.Image;
import remotesensing.util.Pixel;

public class LinearPPI extends PPI {

	public LinearPPI(Image input, int skewers) {		
		this(input, generateSkewers(input.type, skewers, input.bands));
	}
	
	public LinearPPI(Image input, Pixel [] skewers) {		
		super(input, skewers);
	}
			
	public void process() { 
		
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
		
		maxScores = getScores(maxIndex);
		minScores = getScores(minIndex);
	}
}

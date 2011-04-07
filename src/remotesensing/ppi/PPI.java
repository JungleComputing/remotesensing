package remotesensing.ppi;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import remotesensing.util.Image;
import remotesensing.util.Image.DataType;
import remotesensing.util.MersenneTwister;
import remotesensing.util.Pixel;

public abstract class PPI {

	protected final Image input;
	protected final Pixel [] skewers;
	
	protected List<Score> maxScores;
	protected List<Score> minScores;
	
	protected int maxCount;
	protected int minCount;
	
	public static class Score { 
		public final int pixel; 
		public final int count;
		
		public Score(int pixel, int count) { 
			this.pixel = pixel;
			this.count = count;
		}		
	}
	
	protected PPI(Image input, Pixel [] skewers) {		
		// NOTE: assumes BSQ image storage. 		
		this.input = input;
		this.skewers = skewers;		
    }
	
	protected static Pixel [] generateSkewers(DataType type, int skewers, int bands) {

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
	
	public List<Score> getMaxScores() {
		return maxScores;
	}

	public List<Score> getMinScores() {
		return minScores;
	}
	
	public int getResultMAXCount() {
		return maxCount;
	}
	
	public int getResultMINCount() {
		return minCount;
	}
	
	protected List<Score> getScores(int [] index) { 
	
		Arrays.sort(index);

		List<Score> result = new LinkedList<Score>();

		int last = index[0];
		int count = 1;
		
		for (int i=1;i<index.length;i++) { 
	
			if (last != index[i]) {
				
				//System.out.println("ADD " + last + " " + count);
				
				result.add(new Score(last, count));
				last = index[i];
				count = 1;
			} else { 
				count++;
			}
		}
		
		result.add(new Score(last, count));
	
		return result;
	}
		
	public abstract void process();
}

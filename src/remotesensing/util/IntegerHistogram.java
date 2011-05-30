package remotesensing.util;

public class IntegerHistogram implements Histogram {

	private final long [] histogram;	
	
	public final int bins;
	
	public final int smallestUsedBin;
	public final int largestUsedBin;
	
	public final long minValue;
	public final long maxValue;
	
	public final long smallestBinValue;
	public final long largestBinValue;
	
	private long [] cumulative;
	private long sum; 
	
	public IntegerHistogram(long [] histogram, 
			long minValue, long maxValue,
			int smallestUsedBin, int largestUsedBin, 
			long smallestBinValue, long largestBinValue) {
		
		super();
		this.histogram = histogram;
		this.bins = histogram.length;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.smallestUsedBin = smallestUsedBin;
		this.largestUsedBin = largestUsedBin;
		this.smallestBinValue = smallestBinValue;
		this.largestBinValue = largestBinValue;
	}
	
	public long get(int bin) { 
		return histogram[bin];
	}
	
	public void generateCumulativeDistribution() { 
		
		cumulative = new long[bins];
		sum = 0;
	        
		for (int i=0;i<histogram.length;i++) { 			
			sum += histogram[i];			
			cumulative[i] = sum;
		}
	}
	
	public LookupTable generateStretchedLUT(int maxOutputValue) { 
		
		if (cumulative == null) { 
			generateCumulativeDistribution();
		}
		
		double minSum = histogram[(int)smallestUsedBin];
		
		long [] lut = new long[bins]; 
		
		for (int i=0;i<lut.length;i++) { 
			lut[i] = (long) Math.round( ((cumulative[i]-minSum) / (sum-minSum)) * (maxOutputValue-1) );
		}
		
		/*
		for (int i=0;i<lut.length;i++) { 
			if (lut[i] != 0) { 
				System.out.println(i + " " + lut[i]);
			}
		}*/
		
		return new IntegerLookupTable(lut, minValue, maxValue, 0, maxOutputValue);		
	}			
}

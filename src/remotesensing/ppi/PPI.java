package remotesensing.ppi;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import remotesensing.util.Image;
import remotesensing.util.Image.DataType;
import remotesensing.util.MersenneTwister;
import remotesensing.util.Pixel;

public class PPI {

	private final Image input;
	private final Pixel [] skewers;
	
	private List<Score> maxScores;
	private List<Score> minScores;
	
	private int maxCount;
	private int minCount;
	
	public static class Score { 
		public final int pixel; 
		public final int count;
		
		public Score(int pixel, int count) { 
			this.pixel = pixel;
			this.count = count;
		}		
	}
	
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
	
	private List<Score> getScores(int [] index) { 
	
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
		
		/*
		ByteBuffer tmp = ByteBuffer.allocate(input.lines * input.samples * 4);
		IntBuffer itmp = tmp.asIntBuffer();
		
		
		resultImage = new ByteBufferImage(tmp, input.lines, input.samples, 1, 
				DataType.U32, Interleave.BSQ, ByteOrder.LSF, new int [] { 0, 0, 0}, "");
		
		maxCount = 0;
		minCount = Integer.MAX_VALUE;
		*/
	
		maxScores = getScores(maxIndex);
		minScores = getScores(minIndex);
		
		/*
		Arrays.sort(maxIndex);
		Arrays.sort(minIndex);
		
		
		
		maxScores = new LinkedList<Score>();
		minScores = new LinkedList<Score>();
		
		int lastMax = maxIndex[0];
		int lastMaxCount = 0;
	
		int lastMin = minIndex[0];
		int lastMinCount = 0;
		
		for (int i=0;i<maxIndex.length;i++) { 
	
			if (lastMax != maxIndex[i]) { 
				System.out.println("MAX: " + lastMax + " : " + lastMaxCount);
			
				if (maxCount < lastMaxCount) { 
					maxCount = lastMaxCount;
				}

				itmp.put(lastMax, lastMaxCount);
				
				lastMax = maxIndex[i];
				lastMaxCount = 1;
			} else { 
				lastMaxCount++;
			}
		}
		
		if (maxCount < lastMaxCount) { 
			maxCount = lastMaxCount;
		}

		itmp.put(lastMax, lastMaxCount);

		for (int i=0;i<minIndex.length;i++) { 
			
			if (lastMin != minIndex[i]) { 
				System.out.println("MIN: " + lastMin + " : " + lastMinCount);
			
				if (minCount < lastMinCount) { 
					minCount = lastMinCount;
				}

				itmp.put(lastMin, lastMinCount);
				
				lastMin = minIndex[i];
				lastMinCount = 1;
			} else { 
				lastMinCount++;
			}
		}
		
		
		if (minCount < lastMinCount) { 
			minCount = lastMinCount;
		}

		itmp.put(lastMin, lastMinCount);
		
		System.out.println("MAX: " + maxCount + " MIN: " + minCount);
		
		/*for (int i=0;i<input.samples * input.lines;i++) { 
			
			if (i % input.samples == 0) { 
				System.out.println();
			}
			
			System.out.printf("%4d", tmp[i]);
		}*/
	}
}

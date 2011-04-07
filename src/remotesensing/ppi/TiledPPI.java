package remotesensing.ppi;

import java.util.Arrays;

import remotesensing.util.Image;
import remotesensing.util.Pixel;

public class TiledPPI extends PPI {

	public static final int DEFAULT_TILE_SIZE = 64;
	
	private final int tileSize; 
	
	private double [] max; 
	private double [] min; 
		
	private int [] maxIndex;
	private int [] minIndex; 
	
	private final int pixels;
	private final int tiles;
	
	public TiledPPI(Image input, Pixel [] skewers, int tileSize) {		
		super(input, skewers);
	
		this.tileSize = tileSize;

		pixels = input.samples * input.lines;
		tiles = skewers.length / tileSize + (skewers.length % tileSize > 0 ? 1 : 0);

		max = new double[skewers.length]; 
		min = new double[skewers.length];
			
		maxIndex = new int[skewers.length];
		Arrays.fill(max, Double.MIN_VALUE);
		
		minIndex = new int[skewers.length];		
		Arrays.fill(min, Double.MAX_VALUE);
	}

	public TiledPPI(Image input, int skewers, int tileSize) {		
		this(input, generateSkewers(input.type, skewers, input.bands), tileSize);
	}

	public TiledPPI(Image input, int skewers) {		
		this(input, skewers, DEFAULT_TILE_SIZE);
    }
	
	public TiledPPI(Image input, Pixel [] skewers) {		
		this(input, skewers, DEFAULT_TILE_SIZE);
	}
	
	private void processTile(Pixel p, int index, int start, int end) { 
	
		for (int j=start;j<end;j++) { 
			
			Pixel s = skewers[j];
			
			double value = p.dot(s);
			
			if (value > max[j]) { 
				max[j] = value;
				maxIndex[j] = index;
			}
			
			if (value < min[j]) { 
				min[j] = value;
				minIndex[j] = index;
			}
		}
	}
	
	private void processTile(int start, int end) { 
	
		Pixel p = null;
		
		for (int i=0;i<pixels;i++) {
			p = input.getPixel(p, i);
			processTile(p, i, start, end);
		}
	}
			
	public void process() { 
		
		for (int i=0;i<tiles;i++) { 
			processTile(i*tileSize, i == tiles-1 ? skewers.length : (i+1) * tileSize);
		}
	
		maxScores = getScores(maxIndex);
		minScores = getScores(minIndex);
	}
		
}

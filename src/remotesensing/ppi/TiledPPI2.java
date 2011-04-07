package remotesensing.ppi;

import java.util.Arrays;

import remotesensing.util.Image;
import remotesensing.util.Pixel;

public class TiledPPI2 extends PPI {

	public static final int DEFAULT_TILE_SIZE = 64;

	private final int tileSize; 

	private double [] max; 
	private double [] min; 

	private int [] maxIndex;
	private int [] minIndex; 

	private final int pixels;
	private final int tiles;

	public TiledPPI2(Image input, Pixel [] skewers, int tileSize) {		
		super(input, skewers);

		this.tileSize = tileSize;

		pixels = input.samples * input.lines;
		tiles = pixels / tileSize + (pixels % tileSize > 0 ? 1 : 0);

		max = new double[skewers.length]; 
		min = new double[skewers.length];

		maxIndex = new int[skewers.length];
		Arrays.fill(max, Double.MIN_VALUE);

		minIndex = new int[skewers.length];		
		Arrays.fill(min, Double.MAX_VALUE);
	}

	public TiledPPI2(Image input, int skewers, int tileSize) {		
		this(input, generateSkewers(input.type, skewers, input.bands), tileSize);
	}

	public TiledPPI2(Image input, int skewers) {		
		this(input, skewers, DEFAULT_TILE_SIZE);
	}

	public TiledPPI2(Image input, Pixel [] skewers) {		
		this(input, skewers, DEFAULT_TILE_SIZE);
	}

	private void processTile(Pixel [] t, int start, int end, Pixel s, int skewer) { 

		double max = this.max[skewer];
		double min = this.min[skewer];
		
		int maxIndex = this.maxIndex[skewer];
		int minIndex = this.minIndex[skewer];

		int index = 0;
		
		for (int i=start;i<end;i++) { 
			
			Pixel p = t[index];
			
			double value = p.dot(s);

			if (value > max) { 
				max = value;
				maxIndex = start + index;
			}

			if (value < min) { 
				min = value;
				minIndex = start + index;
			}
		}
		
		this.max[skewer] = max;
		this.min[skewer] = min;
		
		this.maxIndex[skewer] = maxIndex;
		this.minIndex[skewer] = minIndex;	
	}
		
	private void processTile(Pixel [] t, int start, int end) { 
		
		for (int i=0;i<skewers.length;i++) { 
			processTile(t, start, end, skewers[i], i);
		}
	}

	private void getTile(Pixel [] t, int start, int end) { 
		
		int index = 0;
		
		for (int i=start;i<end;i++) { 
			t[index] = input.getPixel(t[index], i);
			index++;
		}
	}
	
	public void process() { 

		Pixel [] t = new Pixel[tileSize];
		
		for (int i=0;i<tiles;i++) { 
	
			int start = i * tileSize;
			int end = i == tiles-1 ? pixels : start + tileSize;
		
			getTile(t, start, end);
			processTile(t, start, end);
		}

		maxScores = getScores(maxIndex);
		minScores = getScores(minIndex);
	}

}

package remotesensing.pixels;

import remotesensing.util.Pixel;

public class U32Pixel extends IntegerPixel {

	private static final long MASK = 0xFFFFFFFFL;
	
	private final int [] data;
	
	public U32Pixel(int bands) {
		super(bands);
		this.data = new int[bands];
	}
	
	public U32Pixel(int [] data) {
		super(data.length);
		this.data = data;
	}
	
	@Override
	public Object getData() {
		return data;
	}

	@Override
	public double dot(Pixel other) {

		U32Pixel tmp = (U32Pixel) other;
		
		double dot = 0.0;
		
		for (int b=0;b<bands;b++) { 
			dot += (data[b] & MASK) * (tmp.data[b] & MASK);												
		}
		
		return dot;
	}

	@Override
	public long get(int band) {
		return (data[band] & MASK);
	}
	
	@Override
	public void put(long value, int band) {
		data[band] = (int) (value & MASK);
	}
}

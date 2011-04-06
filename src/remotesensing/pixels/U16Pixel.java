package remotesensing.pixels;

import remotesensing.util.Pixel;

public class U16Pixel extends IntegerPixel {
	
	private static final long MASK = 0xFFFFL;
	
	private final short [] data;

	public U16Pixel(int bands) {
		super(bands);
		this.data = new short[bands];
	}

	public U16Pixel(short [] data) {
		super(data.length);
		this.data = data;
	}
	
	@Override
	public Object getData() {
		return data;
	}

	@Override
	public double dot(Pixel other) {

		U16Pixel tmp = (U16Pixel) other;
		
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
		data[band] = (short) (value & MASK);
	}
	
}

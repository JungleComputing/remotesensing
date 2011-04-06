package remotesensing.pixels;

import remotesensing.util.Pixel;

public class U8Pixel extends IntegerPixel {

	private static final long MASK = 0xFFL;
	
	private final byte [] data;

	public U8Pixel(int bands) {
		super(bands);
		this.data = new byte[bands];
	}

	public U8Pixel(byte [] data) {
		super(data.length);
		this.data = data;
	}

	@Override
	public Object getData() {
		return data;
	}

	@Override
	public double dot(Pixel other) {

		U8Pixel tmp = (U8Pixel) other;

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
		data[band] = (byte) (value & MASK);
	}
}

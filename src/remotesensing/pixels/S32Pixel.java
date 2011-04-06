package remotesensing.pixels;

import remotesensing.util.Pixel;

public class S32Pixel extends IntegerPixel {
	
	private int [] data;
	
	public S32Pixel(int bands) {
		super(bands);
		this.data = new int[bands];
	}
	
	public S32Pixel(int [] data) {
		super(data.length);
		this.data = data;
	}
	
	@Override
	public Object getData() {
		return data;
	}

	@Override
	public double dot(Pixel other) {

		S32Pixel tmp = (S32Pixel) other;
		
		double dot = 0.0;
		
		for (int b=0;b<bands;b++) { 
			dot += data[b] * tmp.data[b];												
		}
		
		return dot;
	}

	@Override
	public long get(int band) {
		return data[band];
	}
	
	@Override
	public void put(long value, int band) {
		data[band] = (int) value;
	}
}

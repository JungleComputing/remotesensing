package remotesensing.pixels;

import remotesensing.util.Pixel;

public class F64Pixel extends RealPixel {

	private final double [] data;

	public F64Pixel(int bands) {
		super(bands);
		this.data = new double[bands];
	}

	public F64Pixel(double [] data) {
		super(data.length);
		this.data = data;
	}
	
	@Override
	public Object getData() {
		return data;
	}

	@Override
	public double dot(Pixel other) {

		F64Pixel tmp = (F64Pixel) other;
		
		double dot = 0.0;
		
		for (int b=0;b<bands;b++) { 
			dot += data[b] * tmp.data[b];												
		}
		
		return dot;
	}

	@Override
	public double get(int band) {
		return data[band];
	}
	
	@Override
	public void put(double value, int band) {
		data[band] = value;
	}
}

package remotesensing.pixels;

import remotesensing.util.Pixel;

public class F32Pixel extends RealPixel {

	private final float [] data;
	
	public F32Pixel(int bands) {
		super(bands);
		this.data = new float[bands];
	}
	
	public F32Pixel(float [] data) {
		super(data.length);
		this.data = data;
	}

	@Override
	public Object getData() {
		return data;
	}

	@Override
	public double dot(Pixel other) {

		F32Pixel tmp = (F32Pixel) other;
		
		double dot = 0.0;
		
		for (int b=0;b<bands;b++) { 
			dot += data[b] * tmp.data[b];												
		}
		
		return dot;
	}

	@Override
	public double value(int band) {
		return data[band];
	}
}

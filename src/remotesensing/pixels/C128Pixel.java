package remotesensing.pixels;

import remotesensing.util.Pixel;

public class C128Pixel extends ComplexRealPixel {

	private final double [] data;
	
	public C128Pixel(int bands) {
		super(bands);
		this.data = new double[2*bands];
	}
	
	public C128Pixel(double [] data) {
		super(data.length/2);
		this.data = data;
	}
	
	@Override
	public Object getData() {
		return data;
	}

	@Override
	public double dot(Pixel other) {
		throw new RuntimeException("C128Pixel.dot NOT implemented!");
	}

	@Override
	public double real(int band) {
		return data[2*band];
	}

	@Override
	public double imag(int band) {
		return data[2*band+1];
	}
}

package remotesensing.pixels;

import remotesensing.util.Pixel;

public abstract class ComplexRealPixel extends Pixel {

	protected ComplexRealPixel(int bands) {
		super(bands);
	}
	
	public abstract double real(int band);
	public abstract double imag(int band);
		
}

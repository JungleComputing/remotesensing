package remotesensing.pixels;

import remotesensing.util.Pixel;

public abstract class RealPixel extends Pixel {

	protected RealPixel(int bands) {
		super(bands);
	}

	public abstract double value(int band);	
}

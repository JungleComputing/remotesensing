package remotesensing.pixels;

import remotesensing.util.Pixel;

public abstract class IntegerPixel extends Pixel {

	protected IntegerPixel(int bands) {
		super(bands);
	}

	public abstract long get(int band);	
	
	public abstract void put(long value, int band);	
		
}



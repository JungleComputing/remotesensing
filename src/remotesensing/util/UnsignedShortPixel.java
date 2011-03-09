package remotesensing.util;

public class UnsignedShortPixel extends Pixel {

	private short [] pixel;
	
	public UnsignedShortPixel(int bands) { 
		super(bands);
		this.pixel = new short[bands];
	}
	
	public UnsignedShortPixel(short [] data, int bands) { 
		super(bands);
		this.pixel = data;
	}
	
	
}

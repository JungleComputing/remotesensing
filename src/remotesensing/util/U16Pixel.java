package remotesensing.util;

public class U16Pixel extends Pixel {

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
			dot += (data[b] & 0xFFFF) * (tmp.data[b] & 0xFFFF);												
		}
		
		return dot;
	}
	
}

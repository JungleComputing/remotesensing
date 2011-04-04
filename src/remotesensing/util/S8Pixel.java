package remotesensing.util;

public class S8Pixel extends Pixel {

	private final byte [] data;
	
	public S8Pixel(int bands) {
		super(bands);
		this.data = new byte[bands];
	}
	
	public S8Pixel(byte [] data) {
		super(data.length);
		this.data = data;
	}
	
	@Override
	public Object getData() {
		return data;
	}

	@Override
	public double dot(Pixel other) {

		S8Pixel tmp = (S8Pixel) other;
		
		double dot = 0.0;
		
		for (int b=0;b<bands;b++) { 
			dot += data[b] * tmp.data[b];												
		}
		
		return dot;
	}

}

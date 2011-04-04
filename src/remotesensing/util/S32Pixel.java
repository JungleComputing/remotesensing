package remotesensing.util;

public class S32Pixel extends Pixel {

	private final int [] data;
	
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
}

package remotesensing.util;

public class C64Pixel extends Pixel {

	private final float [] data;
	
	public C64Pixel(int bands) {
		super(bands);
		this.data = new float[2*bands];
	}
	
	public C64Pixel(float [] data) {
		super(data.length/2);
		this.data = data;
	}
	
	@Override
	public Object getData() {
		return data;
	}

	@Override
	public double dot(Pixel other) {
		throw new RuntimeException("C64Pixel.dot NOT implemented!");
	}
}

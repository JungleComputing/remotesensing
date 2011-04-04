package remotesensing.util;

public class U64Pixel extends Pixel {

	private final long [] data;

	public U64Pixel(int bands) {
		super(bands);
		this.data = new long[bands];
	}

	public U64Pixel(long [] data) {
		super(data.length);
		this.data = data;
	}

	@Override
	public Object getData() {
		return data;
	}

	@Override
	public double dot(Pixel other) {
		throw new RuntimeException("U64Pixel.dot NOT implemented!");
	}
}

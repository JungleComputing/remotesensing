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
	
	public short [] getData() { 
		return pixel;
	}
	
	public String toString() { 
		
		StringBuilder sb = new StringBuilder("[");
		
		for (int i=0;i<bands;i++) { 
			sb.append(pixel[i]);
			if (i != bands-1) { 
				sb.append(",");
			}
		}
		
		sb.append("]");
		
		return sb.toString();		
	}
	
}

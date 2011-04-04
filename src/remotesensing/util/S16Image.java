package remotesensing.util;

public class S16Image extends Image {

	private short [] data;

	public S16Image(short [] data, 
			int lines, int samples, int bands, 
		    Interleave interleave, ByteOrder order,
			int[] defaultbands, String description) {

		super(lines, samples, bands, DataType.S16, interleave, order, defaultbands, description);
		this.data = data;
	}
	
	public S16Image(short [] data, int lines, int samples, int bands) {
		this(data, lines, samples, bands, Interleave.BSQ, ByteOrder.MSF, new int [] { 0, 0, 0}, "");
	}
	
	public S16Image(int lines, int samples, int bands) {
		this(new short [lines*samples*bands], lines, samples, bands);
	}

	@Override
	public Object getData() { 
		// returns data as a single array of correct primitive type
		return data;
	}

	@Override
	protected Object getBandBSQ(int band) { 

		int len = samples*lines;    	
		int offset = band * len; 

		short [] target = new short[len];
		System.arraycopy(data, offset, target, 0, len);
		
		return target;
	}

	@Override
	protected Object getBandBIP(int band) {

		int len = samples*lines;    	
		int offset = band;

		short [] target = new short[len];

		for (int i=0;i<len;i++) { 
			target[i] = data[offset];
			offset += bands;
		}

		return target;    	
	}
	
	@Override
	protected Pixel getPixelBIP(Pixel p, int index) {     
		System.arraycopy(data, index*bands, (short [])p.getData(), 0, bands);
		return p;    	
	}

	@Override
	protected Pixel getPixelBSQ(Pixel p, int index) { 

		int size = samples * bands;

		short [] tmp = (short[]) p.getData();
		
		for (int i=0;i<bands;i++) { 
			tmp[i] = data[index + i*size];
		}

		return p;    	
	}
	
	public void getAsRGB(int [] target, int x, int y, int w, int h, Conversion conv) { 
		// HACK!
	 	NumberRangeToRGBConversion c = (NumberRangeToRGBConversion) conv;

    	int dstIndex = 0;

    	for (int j=0;j<h;j++) { 

    		int index = (y+j) * samples + x;

    		for (int i=0;i<w;i++) { 
    			target[dstIndex++] = c.convert(data[index]);
    			index++;
    		}
    	}
	}
}

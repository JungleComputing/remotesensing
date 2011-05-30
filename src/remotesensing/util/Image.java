package remotesensing.util;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import remotesensing.pixels.C128Pixel;
import remotesensing.pixels.C64Pixel;
import remotesensing.pixels.F32Pixel;
import remotesensing.pixels.F64Pixel;
import remotesensing.pixels.IntegerPixel;
import remotesensing.pixels.S16Pixel;
import remotesensing.pixels.S32Pixel;
import remotesensing.pixels.S64Pixel;
import remotesensing.pixels.S8Pixel;
import remotesensing.pixels.U16Pixel;
import remotesensing.pixels.U32Pixel;
import remotesensing.pixels.U64Pixel;
import remotesensing.pixels.U8Pixel;

public class Image {

	// The different data types supported by ENVI
	public enum DataType { 
		
		S8(1, 1, Byte.MIN_VALUE, Byte.MAX_VALUE, "signed 8-bit integer") { 
			Object createArray(int length) { 
				return new byte[length]; 
			} 
			
			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) { 
				source.limit(soff+slen).position(soff);		    	
		    	source.get((byte[]) target, toff, tlen);
			}
			
			void put(Object source, int soff, int slen, ByteBuffer target, int toff, int tlen) {
				target.limit(toff+tlen).position(toff);
		    	target.put((byte[]) source, soff, slen);
			}
			
			public Pixel createPixel(int bands) { 
				return new S8Pixel(bands); 				
			}
			
			public Pixel generateSkewer(MersenneTwister twister, int bands) { 
				
				byte [] tmp = new byte[bands]; 
				
				for (int i=0;i<bands;i++) { 
					tmp[i] = twister.nextByte();
				}
				
				return new S8Pixel(tmp);
			}
		},
		
		S16(2, 2, Short.MIN_VALUE, Short.MAX_VALUE, "signed 16-bit integer") {
			
			Object createArray(int length) { 
				return new short[length]; 
			} 

			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) {
				source.clear();
				ShortBuffer buf = source.asShortBuffer();
				buf.limit(soff+slen).position(soff);   	
		    	buf.get((short []) target, toff, tlen);		    	
		    }
			
			void put(Object source, int soff, int slen, ByteBuffer target, int toff, int tlen) {
				target.clear();
				ShortBuffer buf = target.asShortBuffer();
		    	buf.limit(toff+slen).position(toff);   	
		    	buf.put((short[]) source, soff, slen);
			}
			
			public Pixel createPixel(int bands) { 
				return new S16Pixel(bands); 				
			}
			
			public Pixel generateSkewer(MersenneTwister twister, int bands) { 
				
				short [] tmp = new short[bands]; 
				
				for (int i=0;i<bands;i++) { 
					tmp[i] = twister.nextShort();
				}
				
				return new S16Pixel(tmp);
			}
		},
			
		S32(3, 4, Integer.MIN_VALUE, Integer.MAX_VALUE, "signed 32-bit integer") { 
			Object createArray(int length) { 
				return new int[length]; 
			} 

			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) { 
				source.clear();
				IntBuffer buf = source.asIntBuffer();    	
		    	buf.limit(soff+slen).position(soff);   	
		    	buf.get((int []) target, toff, tlen);		    	
			}
			
			void put(Object source, int soff, int slen, ByteBuffer target, int toff, int tlen) {
				target.clear();
				IntBuffer buf = target.asIntBuffer();
		    	buf.limit(toff+tlen).position(toff);   	
		    	buf.put((int[]) source, soff, slen);
			}
			
			public Pixel createPixel(int bands) { 
				return new S32Pixel(bands); 				
			}
			
			public Pixel generateSkewer(MersenneTwister twister, int bands) { 
				
				int [] tmp = new int[bands]; 
				
				for (int i=0;i<bands;i++) { 
					tmp[i] = twister.nextInt();
				}
				
				return new S32Pixel(tmp);
			}
		},

		S64(14, 8, Long.MIN_VALUE, Long.MAX_VALUE, "signed 64-bit integer") { 
			Object createArray(int length) { 
				return new long[length]; 
			} 

			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) { 
				source.clear();
				LongBuffer buf = source.asLongBuffer();    	
		    	buf.limit(soff+slen).position(soff);   	
		    	buf.get((long []) target, toff, tlen);		    	
		    }

			void put(Object source, int soff, int slen, ByteBuffer target, int toff, int tlen) {
				target.clear();
				LongBuffer buf = target.asLongBuffer();
		    	buf.limit(toff+tlen).position(toff);   	
		    	buf.put((long[]) source, soff, slen);
			}
			
			public Pixel createPixel(int bands) { 
				return new S64Pixel(bands); 				
			}
			
			public Pixel generateSkewer(MersenneTwister twister, int bands) { 
				
				long [] tmp = new long[bands]; 
				
				for (int i=0;i<bands;i++) { 
					tmp[i] = twister.nextLong();
				}
				
				return new S64Pixel(tmp);
			}

		}, 

		U8(0, 1, 0, 0xFFL, "unsigned 8-bit integer") { 
		
			Object createArray(int length) { 
				return new byte[length]; 
			} 
			
			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) { 
				source.limit(soff+slen).position(soff);   	
		    	source.get((byte[]) target, toff, tlen);
			}

			void put(Object source, int soff, int slen, ByteBuffer target, int toff, int tlen) {
				target.limit(toff+tlen).position(toff);   	
		    	target.put((byte[]) source, soff, slen);
			}

			public Pixel createPixel(int bands) { 
				return new U8Pixel(bands); 				
			}
			
			public Pixel generateSkewer(MersenneTwister twister, int bands) { 
				
				byte [] tmp = new byte[bands]; 
				
				for (int i=0;i<bands;i++) { 
					tmp[i] = twister.nextByte();
				}
				
				return new U8Pixel(tmp);
			}
		},
		
		U16(12, 2, 0, 0xFFFFL, "unsigned 16-bit integer") { 
			Object createArray(int length) { 
				return new short[length]; 
			} 
			
			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) {
				source.clear();
				ShortBuffer buf = source.asShortBuffer();    	
		    	buf.limit(soff+slen).position(soff);   	
		    	buf.get((short []) target, toff, tlen);
		    }	
			
			void put(Object source, int soff, int slen, ByteBuffer target, int toff, int tlen) {
				target.clear();
				ShortBuffer buf = target.asShortBuffer();
		    	buf.limit(toff+tlen).position(toff);   	
		    	buf.put((short[]) source, soff, slen);
			}
			
			public Pixel createPixel(int bands) { 
				return new U16Pixel(bands); 				
			}
			
			public Pixel generateSkewer(MersenneTwister twister, int bands) { 
				
				short [] tmp = new short[bands]; 
				
				for (int i=0;i<bands;i++) { 
					tmp[i] = twister.nextShort();
				}
				
				return new U16Pixel(tmp);
			}

		},
		
		U32(13, 4, 0, 0xFFFFFFFFL, "unsigned 32-bit integer") { 
			Object createArray(int length) { 
				return new int[length];
			} 

			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) {
				source.clear();
				IntBuffer buf = source.asIntBuffer();    	
		    	buf.limit(soff+slen).position(soff);   	
		    	buf.get((int []) target, toff, tlen);
			}
			
			void put(Object source, int soff, int slen, ByteBuffer target, int toff, int tlen) {
				target.clear();
				IntBuffer buf = target.asIntBuffer();
		    	buf.limit(toff+tlen).position(toff);   	
		    	buf.put((int[]) source, soff, slen);
			}
			
			public Pixel createPixel(int bands) { 
				return new U32Pixel(bands); 				
			}
			
			public Pixel generateSkewer(MersenneTwister twister, int bands) { 
				
				int [] tmp = new int[bands]; 
				
				for (int i=0;i<bands;i++) { 
					tmp[i] = twister.nextInt();
				}
				
				return new U32Pixel(tmp);
			}

		},
		
		U64(15, 8, 0, 0xFFFFFFFFFFFFFFFFL, "unsigned 64-bit integer") { 
			Object createArray(int length) { 
				return new long[length]; 
			} 

			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) { 
				source.clear();
				LongBuffer buf = source.asLongBuffer();    	
		    	buf.limit(soff+slen).position(soff);   	
		    	buf.get((long []) target, toff, tlen);
		    }
			
			void put(Object source, int soff, int slen, ByteBuffer target, int toff, int tlen) {
				target.clear();
				LongBuffer buf = target.asLongBuffer();
		    	buf.limit(toff+tlen).position(toff);   	
		    	buf.put((long[]) source, soff, slen);
			}
			
			public Pixel createPixel(int bands) { 
				return new U64Pixel(bands); 				
			}
			
			public Pixel generateSkewer(MersenneTwister twister, int bands) { 
				
				long [] tmp = new long[bands]; 
				
				for (int i=0;i<bands;i++) { 
					tmp[i] = twister.nextLong();
				}
				
				return new U64Pixel(tmp);
			}
		},
		
		F32(4, 4, Float.MIN_VALUE, Float.MAX_VALUE, "32-bit floating point") { 
			Object createArray(int length) { 
				return new float[length]; 
			} 

			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) {				
				source.clear();
				FloatBuffer buf = source.asFloatBuffer();    	
		    	buf.limit(soff+slen).position(soff);   	
		    	buf.get((float []) target, toff, tlen);
		    }
			
			void put(Object source, int soff, int slen, ByteBuffer target, int toff, int tlen) {
				target.clear();
				FloatBuffer buf = target.asFloatBuffer();
		    	buf.limit(toff+tlen).position(toff);   	
		    	buf.put((float[]) source, soff, slen);
			}
			
			public Pixel createPixel(int bands) { 
				return new F32Pixel(bands); 				
			}
			
			public Pixel generateSkewer(MersenneTwister twister, int bands) { 
				
				float [] tmp = new float[bands]; 
				
				for (int i=0;i<bands;i++) { 
					tmp[i] = twister.nextFloat() - 0.5f;
				}
				
				return new F32Pixel(tmp);
			}

		},

		F64(5, 8, Double.MIN_VALUE, Double.MAX_VALUE, "64-bit floating point") { 
			Object createArray(int length) { 
				return new double[length]; 
			} 

			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) {
				source.clear();
				DoubleBuffer buf = source.asDoubleBuffer();    	
		    	buf.limit(soff+slen).position(soff);   	
		    	buf.get((double []) target, toff, tlen);
		    }
			
			void put(Object source, int soff, int slen, ByteBuffer target, int toff, int tlen) {
				target.clear();
				DoubleBuffer buf = target.asDoubleBuffer();
		    	buf.limit(toff+tlen).position(toff);   	
		    	buf.put((double[]) source, soff, slen);
			}
			
			public Pixel createPixel(int bands) { 
				return new F64Pixel(bands); 				
			}
			
			public Pixel generateSkewer(MersenneTwister twister, int bands) { 
				
				double [] tmp = new double[bands]; 
				
				for (int i=0;i<bands;i++) { 
					tmp[i] = twister.nextDouble() - 0.5;
				}
				
				return new F64Pixel(tmp);
			}

		},
		
		C64(6, 8, true, Float.MIN_VALUE, Float.MAX_VALUE, "64-bit complex floating point") { 
			Object createArray(int length) { 
				return new float[2*length]; 
			} 

			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) {
				source.clear();
				FloatBuffer buf = source.asFloatBuffer();    	
		    	buf.limit(soff+slen).position(soff);   	
		    	buf.get((float []) target, toff, tlen);
		    }
			
			void put(Object source, int soff, int slen, ByteBuffer target, int toff, int tlen) {
				target.clear();
				FloatBuffer buf = target.asFloatBuffer();
		    	buf.limit(toff+slen).position(toff);   	
		    	buf.put((float[]) source, soff, slen);
			}
			
			public Pixel createPixel(int bands) { 
				return new C64Pixel(bands); 				
			}
			
			public Pixel generateSkewer(MersenneTwister twister, int bands) { 
				
				float [] tmp = new float[bands*2]; 
				
				for (int i=0;i<bands*2;i++) { 
					tmp[i] = twister.nextFloat();
				}
				
				return new C64Pixel(tmp);
			}
		},
		
		C128(9, 16, true, Double.MIN_VALUE, Double.MAX_VALUE, "128-bit complex floating point") { 
			Object createArray(int length) { 
				return new double[2*length]; 
			} 
		
			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) { 
				source.clear();
				DoubleBuffer buf = source.asDoubleBuffer();    	
		    	buf.limit(soff+slen).position(soff);   	
		    	buf.get((double []) target, toff, tlen);
		    }
			
			void put(Object source, int soff, int slen, ByteBuffer target, int toff, int tlen) {
				target.clear();
				DoubleBuffer buf = target.asDoubleBuffer();
		    	buf.limit(toff+tlen).position(toff);   	
		    	buf.put((double[]) source, soff, slen);
			}
						
			public Pixel createPixel(int bands) { 
				return new C128Pixel(bands); 				
			}
			
			public Pixel generateSkewer(MersenneTwister twister, int bands) { 
				
				double [] tmp = new double[bands*2]; 
				
				for (int i=0;i<bands*2;i++) { 
					tmp[i] = twister.nextDouble();
				}
				
				return new C128Pixel(tmp);
			}
		};
			
		public final int number;
		public final int bytes;		
		
		public final boolean signed; 
		public final boolean real; 
		public final boolean complex; 
		
		public final long maxIntegerValue;
		public final long minIntegerValue;
		
		public final double maxRealValue;
		public final double minRealValue;
				
		public final String description;
	
		DataType(int number, int bytes, long minIntegerValue, long maxIntegerValue, String description) { 
			this.number = number;
			this.bytes = bytes;		
			this.signed = (minIntegerValue < 0);
			this.real = false;
			this.complex = false;
			this.minIntegerValue = minIntegerValue;
			this.maxIntegerValue = maxIntegerValue;
			this.description = description;
			
			minRealValue = maxRealValue = 0.0;
		}
		
		DataType(int number, int bytes, double minRealValue, double maxRealValue, String description) { 
			this(number, bytes, false, minRealValue, maxRealValue, description);
		}
		
		DataType(int number, int bytes, boolean complex, double minRealValue, double maxRealValue, String description) { 
			this.number = number;
			this.bytes = bytes;		
			this.signed = true;
			this.real = true;
			this.complex = complex;			
			this.minRealValue = minRealValue;
			this.maxRealValue = maxRealValue;
			this.description = description;
			
			minIntegerValue = maxIntegerValue = 0;
		}
				
		public abstract Pixel createPixel(int bands); 
		public abstract Pixel generateSkewer(MersenneTwister twister, int bands); 
		
		abstract Object createArray(int length); 
		
		abstract void get(ByteBuffer source, int toff, int tlen, Object target, int soff, int slen);
		abstract void put(Object target, int soff, int slen, ByteBuffer source, int toff, int tlen);
		
		
		void get(ByteBuffer source, int soff, Object target, int toff) { 
			get(source, soff, 1, target, toff, 1);
		}
				
		void get(ByteBuffer source, int soff, ByteBuffer target, int toff, int len) { 
		 	source.position(soff).limit(soff+len);
		 	target.position(toff).limit(toff+len);
		 	target.put(source);
	    }
		
		void get(ByteBuffer source, ByteBuffer target, int offset, int stride, int block, int repeat) { 
		 	
			target.rewind().limit(source.capacity());		
			
		 	for (int i=0;i<repeat;i++) { 
		 		source.position(offset+i*stride).limit(offset+block);		
		 		target.put(source);
		 	}
		}
		
		
		public static DataType convert(int value) throws Exception { 
			
			for (DataType tmp : DataType.values()) { 
				if (tmp.number == value) { 
					return tmp;
				}
			}
			
			throw new Exception("Unknown data type " + value);
		}
	}
	
	// The different interleavings supported
	public enum Interleave { 
		BSQ("bsq", "band sequential"), 
		BIP("bip", "band interleaved per pixel"), 
		BIL("bil", "band interleaver per line");
	
		public final String flag;
		public final String description;
		
		Interleave(String flag, String description) { 
			this.flag = flag;
			this.description = description;
		}
		
		public static Interleave convert(String value) throws Exception { 
			
			for (Interleave tmp : Interleave.values()) { 
				if (tmp.flag.equalsIgnoreCase(value)) { 
					return tmp;
				}
			}
			
			throw new Exception("Unknown interleave " + value);
		}	
	}
	
	// The different byte orders supported
	public enum ByteOrder { 
		LSF(0, "LSF"), 
		MSF(1, "MSF"); 
	
		public final int number;
		public final String description;
		
		ByteOrder(int number, String description) { 
			this.number = number;
			this.description = description;
		}
		
		public static ByteOrder convert(int value) throws Exception { 
			
			for (ByteOrder tmp : ByteOrder.values()) { 
				if (tmp.number == value) { 
					return tmp;
				}
			}
			
			throw new Exception("Unknown byte order " + value);
		}	
	}
	
	public final int lines;
    public final int samples;
    public final int bands;
    
	private int [] defaultbands;    
	private String description;
	
    public final DataType type;
    public final Interleave interleave;
    public final ByteOrder order;
    
	private final ByteBuffer data;
    
	public Image(ByteBuffer data, int lines, int samples, int bands, 
	        DataType type, Interleave interleave, ByteOrder order) { 
		this(data, lines, samples, bands, type, interleave, order, new int [] { 0,0,0 }, "");
	}
	
    public Image(ByteBuffer data, int lines, int samples, int bands, 
    		        DataType type, Interleave interleave, ByteOrder order, 
    		        int [] defaultbands, String description) { 
    	
    	this.lines = lines;
        this.samples = samples; 
        this.bands = bands;
        this.defaultbands = defaultbands == null ? new int [] { 0,0,0 } : defaultbands;
        this.description = description;
        this.type = type;
        this.interleave = interleave;
        this.order = order;
        
    	this.data = data;

		if (order == ByteOrder.LSF) { 
			data.order(java.nio.ByteOrder.LITTLE_ENDIAN);
		} else { 
			data.order(java.nio.ByteOrder.BIG_ENDIAN);
		}   
    }
   
    public Image(int lines, int samples, int bands, 
	        DataType type, Interleave interleave, ByteOrder order, 
	        int [] defaultbands, String description) { 
    	this(ByteBuffer.allocate(lines*samples*bands*type.bytes), 
    			lines, samples, bands, type, interleave, order, defaultbands, description);
    }
    
    public int [] getDefaultBands() {
        return defaultbands.clone();
    }

    public String getDescription() {
        return description;
    }
    
    public Object getData() {
    	// returns data as a single array of correct primitive type
		int len = samples*lines*bands;
		Object target = type.createArray(len);
		type.get(data, 0, len, target, 0, len);
		return target;
    }
  
    public void getRawData(int offset, byte [] target, int toffset, int tlen) { 
		// reads raw data 
		DataType.S8.get(data, offset, tlen, target, toffset, tlen);
	}
   
    private Image getBandBSQ(int band) { 
			
		int bytes = samples * lines * type.bytes;
		
		ByteBuffer tmp = ByteBuffer.allocate(bytes);
	
		if (order == ByteOrder.LSF) { 
			tmp.order(java.nio.ByteOrder.LITTLE_ENDIAN);
		} else { 
			tmp.order(java.nio.ByteOrder.BIG_ENDIAN);
		}      
		
		data.position(band * bytes).limit((band+1)*bytes);
		tmp.put(data);
		
		return new Image(tmp, lines, samples, 1, type, Interleave.BSQ, order, new int [] {0,0,0}, "");
	}

	private Image getBandBIP(int band) {

		int pixels    = samples * lines;
		int bytes     = pixels * type.bytes;
		int pixelsize = bands * type.bytes;
		
		ByteBuffer tmp = ByteBuffer.allocate(bytes);
		
		if (order == ByteOrder.LSF) { 
			tmp.order(java.nio.ByteOrder.LITTLE_ENDIAN);
		} else { 
			tmp.order(java.nio.ByteOrder.BIG_ENDIAN);
		}      
	
		for (int i=0;i<pixels;i++) { 
			int off = i * pixelsize + band * type.bytes;
			data.limit(off+type.bytes).position(off); 
			tmp.put(data);
		}
		
		return new Image(tmp, lines, samples, 1, type, Interleave.BSQ, order, new int [] {0,0,0}, "");
	}
	
	private Image getBandBIL(int band) {  
        throw new RuntimeException("getBandBIL NOT implemented!");
    }
    
    public Image getBand(int index) { 
    	// returns data as a single array of correct primitive type
    	int len = samples*lines;
    	
    	switch (interleave) { 
    	case BIL: return getBandBIL(index);     		     
    	case BIP: return getBandBIP(index);    		
    	case BSQ: return getBandBSQ(index);
    	default:
    		throw new RuntimeException("Failed to get band " + index + ": Unknown interleaving!");
    	}
    }
    
    private Pixel getPixelBIP(Pixel p, int index) {
		
		int offset = index * bands;
		type.get(data, offset, bands, p.getData(), 0, bands);
		return p;    	
	}

	private Pixel getPixelBSQ(Pixel p, int index) { 

		int size = samples * bands;

		for (int i=0;i<bands;i++) { 
			type.get(data, index + i*size, p.getData(), i);
		}
		
		return p;    	
	}

    private Pixel getPixelBIL(Pixel p, int index) { 
        throw new RuntimeException("getPixelBIL NOT implemented!");
    }

    public Pixel getPixel(int index) { 
    	return getPixel(null, index);
    }

    public Pixel getPixel(int line, int sample) { 
    	return getPixel(line*samples + sample);
    }
    
    public Pixel getPixel(Pixel pixel, int index) { 

    	if (pixel == null) { 
    		pixel = type.createPixel(bands);
    	}
    	
    	switch (interleave) { 
    	case BIL: return getPixelBIL(pixel, index);     		     
    	case BIP: return getPixelBIP(pixel, index);    		
    	case BSQ: return getPixelBSQ(pixel, index);
    	default:
    		throw new RuntimeException("Failed to get pixel " + index + ": Unknown interleaving!");
    	}
    }
    
    private void putPixelBSQ(Pixel p, int index) { 
    	throw new RuntimeException("putPixelBSQ NOT implemented!");
    }
    
    private void putPixelBIP(Pixel p, int index) {
    	type.put(p.getData(), 0, bands, data, index * bands, bands);
    }
    
    
    private void putPixelBIL(Pixel p, int index) { 
    	throw new RuntimeException("putPixelBIL NOT implemented!");
    }

    public void putPixel(Pixel pixel, int index) {

    	if (pixel == null) { 
    		throw new RuntimeException("Pixel is null!");
    	}
    	    	
    	if (pixel.bands != bands) { 
    		throw new RuntimeException("Pixel contains wrong number of bands!");
    	}

    	if (index < 0 || index > lines * samples) { 
    		throw new RuntimeException("Pixel index out of bounds!");
    	} 
    		
    	// TODO: check type!
    	
    	switch (interleave) { 
    	case BIL: 
    		putPixelBIL(pixel, index); 
    		break;    		
    	case BIP: 
    		putPixelBIP(pixel, index);
    		break;    		
    	case BSQ: 
    		putPixelBSQ(pixel, index);
    		break;    		
    	default:
    		throw new RuntimeException("Failed to put pixel " + index + ": Unknown interleaving!");
    	}
	}

    
	public ByteBuffer getDataReadOnly() {
		ByteBuffer tmp = data.asReadOnlyBuffer();
		tmp.position(0).limit(tmp.capacity());
		return tmp;
	}

	public ByteBuffer getDataReadOnly(int index, int len) {
		data.position(index).limit(index+len);
		ByteBuffer tmp = data.slice().asReadOnlyBuffer();
		tmp.position(0).limit(tmp.capacity());
		return tmp;
	}

	
	
   // public void getAsRGB(int [] target, int x, int y, int w, int h, Conversion c) { 
   //     System.out.println("EEP!");
   //  	throw new RuntimeException("getAsRGB NOT implemented!");
   // }
}

package remotesensing.util;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

public abstract class Image {

	// The different data types supported by ENVI
	public enum DataType { 
		S8(1, "8-bit byte") { 
			Object createArray(int length) { 
				return new byte[length]; 
			} 
			
			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) { 
				byte [] tmp = (byte[]) target;    	
		    	source.rewind().position(soff);   	
		    	source.get(tmp, toff, tlen);
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
		
		S16(2, "signed 16-bit integer") {
			
			Object createArray(int length) { 
				return new short[length]; 
			} 

			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) { 
		    	
				// System.out.println("Reading " + soff + " (" + slen + ") " + toff + "(" + tlen + ") shorts");
				
				short [] tmp = (short []) target;    	
		    	ShortBuffer buf = source.asShortBuffer();
		    	buf.position(soff);   	
		    	buf.get(tmp, toff, tlen);		    	
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
			
		S32(3, "signed 32-bit integer") { 
			Object createArray(int length) { 
				return new int[length]; 
			} 

			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) { 
		    	int [] tmp = (int []) target;    	
		    	IntBuffer buf = source.asIntBuffer();    	
		    	buf.rewind().position(soff);   	
		    	buf.get(tmp, toff, tlen);		    	
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

		S64(14, "signed 64-bit integer") { 
			Object createArray(int length) { 
				return new long[length]; 
			} 

			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) { 
		    	long [] tmp = (long []) target;    	
		    	LongBuffer buf = source.asLongBuffer();    	
		    	buf.rewind().position(soff);   	
		    	buf.get(tmp, toff, tlen);		    	
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

		U16(12, "unsigned 16-bit integer") { 
			Object createArray(int length) { 
				return new short[length]; 
			} 
			
			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) {
				short [] tmp = (short []) target;    	
		    	ShortBuffer buf = source.asShortBuffer();    	
		    	buf.rewind().position(soff);   	
		    	buf.get(tmp, toff, tlen);		    	
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
		
		U32(13, "unsigned 32-bit integer") { 
			Object createArray(int length) { 
				return new int[length];
			} 

			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) { 
		    	int [] tmp = (int []) target;    	
		    	IntBuffer buf = source.asIntBuffer();    	
		    	buf.rewind().position(soff);   	
		    	buf.get(tmp, toff, tlen);		    	
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
		
		U64(15, "unsigned 64-bit integer") { 
			Object createArray(int length) { 
				return new long[length]; 
			} 

			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) { 
		    	long [] tmp = (long []) target;    	
		    	LongBuffer buf = source.asLongBuffer();    	
		    	buf.rewind().position(soff);   	
		    	buf.get(tmp, toff, tlen);		    	
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
		
		F32(4, "32-bit floating point") { 
			Object createArray(int length) { 
				return new float[length]; 
			} 

			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) { 
		    	float [] tmp = (float []) target;    	
		    	FloatBuffer buf = source.asFloatBuffer();    	
		    	buf.rewind().position(soff);   	
		    	buf.get(tmp, toff, tlen);		    	
		    }
			
			public Pixel createPixel(int bands) { 
				return new F32Pixel(bands); 				
			}
			
			public Pixel generateSkewer(MersenneTwister twister, int bands) { 
				
				float [] tmp = new float[bands]; 
				
				for (int i=0;i<bands;i++) { 
					tmp[i] = twister.nextFloat();
				}
				
				return new F32Pixel(tmp);
			}

		},

		F64(5, "64-bit floating point") { 
			Object createArray(int length) { 
				return new double[length]; 
			} 

			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) { 
		    	double [] tmp = (double []) target;    	
		    	DoubleBuffer buf = source.asDoubleBuffer();    	
		    	buf.rewind().position(soff);   	
		    	buf.get(tmp, toff, tlen);		    	
		    }
			
			public Pixel createPixel(int bands) { 
				return new F64Pixel(bands); 				
			}
			
			public Pixel generateSkewer(MersenneTwister twister, int bands) { 
				
				double [] tmp = new double[bands]; 
				
				for (int i=0;i<bands;i++) { 
					tmp[i] = twister.nextDouble();
				}
				
				return new F64Pixel(tmp);
			}

		},
		
		C64(6, "64-bit complex floating point") { 
			Object createArray(int length) { 
				return new float[2*length]; 
			} 

			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) { 
		    	float [] tmp = (float []) target;    	
		    	FloatBuffer buf = source.asFloatBuffer();    	
		    	buf.rewind().position(soff);   	
		    	buf.get(tmp, toff, tlen);		    	
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
		
		C128(9, "128-bit complex floating point") { 
			Object createArray(int length) { 
				return new double[2*length]; 
			} 
		
			void get(ByteBuffer source, int soff, int slen, Object target, int toff, int tlen) { 
		    	double [] tmp = (double []) target;    	
		    	DoubleBuffer buf = source.asDoubleBuffer();    	
		    	buf.rewind().position(soff);   	
		    	buf.get(tmp, toff, tlen);		    	
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
		public final String description;
		
		DataType(int number, String description) { 
			this.number = number;
			this.description = description;
		}
		        
		public int number() { 
			return number; 
		}
		
		public abstract Pixel createPixel(int bands); 
		public abstract Pixel generateSkewer(MersenneTwister twister, int bands); 
		
		abstract Object createArray(int length); 
		abstract void get(ByteBuffer source, int toff, int tlen, Object target, int soff, int slen);
		
		
		void get(ByteBuffer source, int soff, Object target, int toff) { 
			
		//	System.out.println("Reading 1 element");
			
			get(source, soff, 1, target, toff, 1);
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
    
    protected Image(int lines, int samples, int bands, 
    		        DataType type, Interleave interleave, ByteOrder order, 
    		        int [] defaultbands, String description) { 
    	
    	this.lines = lines;
        this.samples = samples; 
        this.bands = bands;
        this.defaultbands = defaultbands;
        this.description = description;
        this.type = type;
        this.interleave = interleave;
        this.order = order;
    }
    
    public int [] getDefaultBands() {
        return defaultbands.clone();
    }

    public String getDescription() {
        return description;
    }
    
    public abstract Object getData(); 
    
    protected Object getBandBIL(int band) {  
        throw new RuntimeException("getBandBIL NOT implemented!");
    }
    
    protected Object getBandBSQ(int band) {  
        throw new RuntimeException("getBandBSQ NOT implemented!");
    }
    
    protected Object getBandBIP(int band) {  
        throw new RuntimeException("getBandBIP NOT implemented!");
    }
    
    public Object getBand(int index) { 
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
    
    protected Pixel getPixelBIL(Pixel p, int index) { 
        throw new RuntimeException("getPixelBIL NOT implemented!");
    }

    protected Pixel getPixelBSQ(Pixel p, int index) { 
        throw new RuntimeException("getPixelBSQ NOT implemented!");
    }

    protected Pixel getPixelBIP(Pixel p, int index) { 
        throw new RuntimeException("getPixelBIP NOT implemented!");
    }
       
    public Pixel getPixel(int index) { 
    	
    	Pixel p = type.createPixel(bands);
    	
    	switch (interleave) { 
    	case BIL: return getPixelBIL(p, index);     		     
    	case BIP: return getPixelBIP(p, index);    		
    	case BSQ: return getPixelBSQ(p, index);
    	default:
    		throw new RuntimeException("Failed to get pixel " + index + ": Unknown interleaving!");
    	}
    }

    public Pixel getPixel(Pixel pixel, int index) { 
    	
    	switch (interleave) { 
    	case BIL: return getPixelBIL(pixel, index);     		     
    	case BIP: return getPixelBIP(pixel, index);    		
    	case BSQ: return getPixelBSQ(pixel, index);
    	default:
    		throw new RuntimeException("Failed to get pixel " + index + ": Unknown interleaving!");
    	}
    }
    
    public Pixel getPixel(int x, int y) { 
    	return getPixel(y*samples + x);
    }
    
    public void getAsRGB(int [] target, int x, int y, int w, int h, Conversion c) { 
        System.out.println("EEP!");
    	throw new RuntimeException("getAsRGB NOT implemented!");
    }
}

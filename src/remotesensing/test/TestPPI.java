package remotesensing.test;

import remotesensing.util.MersenneTwister;

public class TestPPI {

	private static void computeSkewerBSQ(short [] data, short [] skewer, int pixels, int bands) { 
		
		long start = System.currentTimeMillis();
		
		for (int i=0;i<pixels;i++) { 
			
			double tmp = 0.0;
			
			for (int b=0;b<bands;b++) { 
				tmp += (skewer[b] & 0xFFFF) * (data[i+pixels*b] & 0xFFFF);				  
			}
		}
		
		long end = System.currentTimeMillis();

		System.out.println("BSQ took " + (end-start)/1000.0 + " sec.");		
	}
	
	private static void computeSkewerBIP(short [] data, short [] skewer, int pixels, int bands) { 
		
		long start = System.currentTimeMillis();
		
		for (int i=0;i<pixels;i++) { 
		
			double tmp = 0.0;
			
			for (int b=0;b<bands;b++) { 
				tmp += (skewer[b] & 0xFFFF) * (data[i*bands+b] & 0xFFFF);				  
			}
		}
		
		long end = System.currentTimeMillis();

		System.out.println("BIP took " + (end-start)/1000.0 + " sec.");
	}
	
	

	public static void main(String [] args) { 
		
		int lines = Integer.parseInt(args[0]);
		int samples = Integer.parseInt(args[1]);
		int bands = Integer.parseInt(args[2]);
		int skewers = Integer.parseInt(args[3]);
		
		short [] data = new short[lines*samples*bands]; 
		
		MersenneTwister twister = new MersenneTwister(42);
		
		for (int i=0;i<data.length;i++) { 
			data[i] = (short) (twister.nextDouble() * Short.MAX_VALUE);
		}
		
		short [][] skew = new short[skewers][bands];
	
		for (int i=0;i<skewers;i++) {
			for (int b=0;b<bands;b++) { 
				skew[i][b] = (short) (twister.nextDouble() * Short.MAX_VALUE);
			}
		}

		for (int i=0;i<skewers;i++) {
			computeSkewerBSQ(data, skew[i], lines*samples, bands);
			computeSkewerBIP(data, skew[i], lines*samples, bands);
		}
	}
	
	
}

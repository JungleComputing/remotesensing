package remotesensing.util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

public class GenerateImage {

	private static double [][] gausian;
	private static int gausianSize;
	private static int gausianMid;
	private static int max;
	
	private static void createGausian(double sigma) { 

		// This is a gaussian as described on http://en.wikipedia.org/wiki/Gaussian_blur
		// Note that we do NOT multiply the value by the first constant (1/(2*PI*sigma^2))
		// since we want the highest value to be one. 
		int size = (int) Math.ceil(sigma*6);
		
		if (size % 2 == 0) { 
			size++;
		}
		
		double c2 = 2*sigma*sigma;
		
		double [][] result = new double[size][size];
		
		int mid = size/2;
	
		for (int y=0;y<size;y++) { 
			for (int x=0;x<size;x++) { 
				double dx = mid-x;
				double dy = mid-y;
				result[y][x] = Math.pow(Math.E, -((dx*dx + dy*dy) / c2));

				if (dx == 0.0 && dy == 0.0) { 
					System.out.println("MID: " + result[y][x]);					
				}
				

			}
			
//			System.out.println();
		}
		
		gausianSize = size;
		gausianMid = mid;		
		gausian = result;
	}
		
	private static void putBlob(short [] image, int samples, int lines, int bands, int x, int y, int b) { 
		
		for (int j=0;j<gausianSize;j++) { 
			for (int i=0;i<gausianSize;i++) { 
				int xpos = x-gausianMid+i;
				int ypos = y-gausianMid+j;

				if (xpos > 0 && xpos < samples && ypos > 0 && ypos < lines) { 
					image[ypos*bands*samples + xpos*bands+b] = (short)(gausian[j][i] * max);
				}
			}
		}
	}
	
    public static void main(String [] args) throws Exception { 
    
        String name = args[0];
        int lines = Integer.parseInt(args[1]);
        int samples = Integer.parseInt(args[2]);
        int bands = Integer.parseInt(args[3]);
        
        int pixelsPerImage = lines*samples;
    
        short [] image = new short[lines*samples*bands];
        
        max = Short.MAX_VALUE;
        
        Arrays.fill(image, (short)0);
        
        PrintWriter header = new PrintWriter(new FileWriter(name + ".hdr"));
        
        header.println("lines = " + lines);
        header.println("samples = " + samples);
        header.println("bands = " + bands);
        header.println("interleave = bip");
        header.println("byte order = 1");
        header.println("description = { generated test image }");
        header.close();
        
        createGausian(Math.max(lines, samples));               
        //createGausian(0.84089642);
        
        Random r = new Random();
        
        for (int i=0;i<bands;i++) { 

        	int x = (int)(r.nextDouble() * samples);
        	int y = (int)(r.nextDouble() * lines);

        	putBlob(image, samples, lines, bands, x, y, i);
        }
        
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(name + ".bip"));
        
        for (int i=0;i<image.length;i++) { 
            
            short pixel = image[i];
            
            int b1 = pixel & 0xff;
            int b2 = (pixel >> 8) & 0xff;
            
            out.write(b2);
            out.write(b1);
        }
      
        out.close();
        
    }
}

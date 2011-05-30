package remotesensing.tools;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;

import remotesensing.pixels.IntegerPixel;
import remotesensing.pixels.U8Pixel;
import remotesensing.util.Image;
import remotesensing.util.Image.ByteOrder;
import remotesensing.util.Image.DataType;
import remotesensing.util.Image.Interleave;
import remotesensing.util.LookupTable;
import remotesensing.util.Pixel;
import remotesensing.util.PngEncoder;
import remotesensing.util.Utils;

public class DumpAsRGBPNG {

    private static Image convert(Image rband, Image gband, Image bband, LookupTable rlut, LookupTable glut, LookupTable blut) {

		// First create an image suitable for ARGB32 
		int pixels = rband.lines * rband.samples;
		
		ByteBuffer buf = ByteBuffer.allocate(pixels * 4);
		Image target = new Image(buf, rband.lines, rband.samples, 4, DataType.U8, Interleave.BIP, ByteOrder.LSF);
		
		Pixel rin = rband.type.createPixel(1);
		Pixel gin = gband.type.createPixel(1);
		Pixel bin = bband.type.createPixel(1);
		
		U8Pixel out = new U8Pixel(4);
		
		for (int i=0;i<pixels;i++) { 

			rband.getPixel(rin, i);
			gband.getPixel(gin, i);
			bband.getPixel(bin, i);
			
			out.put(256, 0);
			out.put(rlut.lookup(rin.getAsReal(0)), 1);
			out.put(glut.lookup(gin.getAsReal(0)), 2);
			out.put(blut.lookup(bin.getAsReal(0)), 3);
			
			target.putPixel(out, i);
		}
		
		return target;
	}

    public static void main(String [] args) { 

        try { 
            String header = args[0];
            String data = args[1];

            int r = Integer.parseInt(args[2]);
            int g = Integer.parseInt(args[3]);
            int b = Integer.parseInt(args[4]);
            
            String outfile = args[5];
            
            Image img = Utils.readENVI(header, data);
            
            if (img.type.complex || img.type == DataType.U64) { 
            	System.err.println("Unsupported image type!");
            	System.exit(1);
            }
            
            Image rband = img.getBand(r);
            Image gband = img.getBand(g);
            Image bband = img.getBand(b);
            
            LookupTable rlut = Utils.generateHistogram(rband, 65536, true).generateStretchedLUT(256);
            LookupTable glut = Utils.generateHistogram(gband, 65536, true).generateStretchedLUT(256);
            LookupTable blut = Utils.generateHistogram(bband, 65536, true).generateStretchedLUT(256);
            	            	
            Image tmp = convert(rband, gband, bband, rlut, glut, blut);
            	
            byte [] out = new PngEncoder(tmp).pngEncode(false);            	

            FileOutputStream fout = new FileOutputStream(outfile);
            fout.write(out);
            fout.close();            	

        } catch (Exception e) { 
            System.out.println("OOPS: ");
            e.printStackTrace();
        }
    }
}


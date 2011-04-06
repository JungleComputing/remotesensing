package remotesensing.tools;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;

import remotesensing.pixels.IntegerPixel;
import remotesensing.util.Histogram;
import remotesensing.util.Image;
import remotesensing.util.LookupTable;
import remotesensing.util.PngEncoder;
import remotesensing.util.Utils;
import remotesensing.util.Image.ByteOrder;
import remotesensing.util.Image.DataType;
import remotesensing.util.Image.Interleave;

public class DumpAsPNG {

	private static Image convert(Image img, LookupTable lut) {

		// First create an image suitable for ARGB32 
		int pixels = img.lines * img.samples;
		
		ByteBuffer buf = ByteBuffer.allocate(pixels * 4);
		Image target = new Image(buf, img.lines, img.samples, 4, DataType.U8, Interleave.BIP, ByteOrder.LSF);
		
		IntegerPixel in = (IntegerPixel) img.type.createPixel(1);
		IntegerPixel out = (IntegerPixel) DataType.U8.createPixel(4);
		
		for (int i=0;i<pixels;i++) { 

			img.getPixel(in, i);
			
			long value = lut.lookup(in.get(0));
			
			out.put(value, 0);
			out.put(value, 1);
			out.put(value, 2);
			out.put(value, 3);
			
			target.putPixel(out, i);
		}
		
		return target;
	}
	
    public static void main(String [] args) { 

        try { 
            String header = args[0];
            String data = args[1];

            Image img = Utils.readENVI(header, data);

            if (img.type.real || img.type.complex || img.type == DataType.U64) { 
            	System.err.println("Unsupported image type!");
            	System.exit(1);
            }
            
            for (int i=0;i<img.bands;i++) { 
            	Image tmp = img.getBand(i);
            
            	Histogram hist = Utils.generateHistogram(tmp, 65536, true);            	
            	LookupTable lut = hist.generateStretchedLUT(256);
            	            	            	
            	tmp = convert(tmp, lut);
            	
            	byte [] out = new PngEncoder(tmp).pngEncode(false);            	

            	FileOutputStream fout = new FileOutputStream("band-" + i + ".png");
            	fout.write(out);
            	fout.close();            	
            }

        } catch (Exception e) { 
            System.out.println("OOPS: ");
            e.printStackTrace();
        }
    }
}


package remotesensing.unmixing;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import remotesensing.atgp.ATGP;
import remotesensing.util.FloatImage;
import remotesensing.util.GrayScaleDoubleImage;
import remotesensing.util.GrayScaleDoubleToRGBConversion;
import remotesensing.util.PngEncoder;
import remotesensing.util.UnsignedShortImage;
import remotesensing.util.UnsignedShortPixel;

public class Main {

    private static int lines;
    private static int samples;
    private static int bands;
    private static short [] image;

    private static void readHeader(File header) throws IOException { 

        System.out.println("Reading header file");

        BufferedReader r = new BufferedReader(new FileReader(header));

        String l = r.readLine();

        while (l != null) { 

            if (l.startsWith("samples")) { 
                samples = Integer.parseInt(l.substring(l.indexOf("=") + 1).trim());
            } else if (l.startsWith("lines")) {
                lines = Integer.parseInt(l.substring(l.indexOf("=") + 1).trim());
            } else if (l.startsWith("bands")) {
                bands = Integer.parseInt(l.substring(l.indexOf("=") + 1).trim());
            } 

            l = r.readLine();
        }

        r.close();

        if (samples <= 0 || lines <= 0 || bands <= 0) { 
            throw new IOException("Incorrect header file");
        }

        System.out.println("Lines " + lines);
        System.out.println("Sample " + samples);
        System.out.println("Bands " + bands);

    }


    private static void readData(File data) throws IOException { 

        long size = (long)lines*samples*bands*2;

        System.out.println("Reading data file of " + size + " bytes");

        image = new short[(int)size];

        if (data.length() < size) { 
            throw new IOException("Data file too small!");
        }

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(data));

        int pos = 0;
        int index = 0;

        while (index < size) { 

            int a = bis.read();
            int b = bis.read();

            image[pos++] = (short) (((b & 0xff) << 8) | (a & 0xff));

            index += 2;

            if (index % (1024*1024) == 0) { 
                System.out.println("read " + ((index)/(1024*1024)) + " MB");
            }
        }

        bis.close();
    }

    private static UnsignedShortImage downmix(UnsignedShortImage img, int downmix) {
    	// FIXME: not implemented!
    	return img;
    }

    private static UnsignedShortPixel [] getEndMembers(UnsignedShortImage img, int count) {  	
    	ATGP atgp = new ATGP(img, count);
    	atgp.run();
    	return atgp.getEndMembers();
    }
    
    private static FloatImage inversion(UnsignedShortImage img, UnsignedShortPixel [] endmembers) {
   
    	
    	
    	
    	// FIXME: not implemented
    	return null;
    }
    
    public static void main(String [] args) { 

        try { 
            String header = args[0];
            String data = args[1];

            int endMemberCount = Integer.parseInt(args[2]);
            int downmix = Integer.parseInt(args[3]);
            
            readHeader(new File(header));
            readData(new File(data));
            
            UnsignedShortImage img = new UnsignedShortImage(image, lines, samples, bands);
            
            if (downmix > 1) { 
            	img = downmix(img, downmix);
            }
            
            UnsignedShortPixel [] endmembers = getEndMembers(img, endMemberCount);
            
            FloatImage result = inversion(img, endmembers);
            
            /*
            for (int i=0;i<planes;i++) { 
            	GrayScaleDoubleImage tmp = unmix.getResult(i);
            	PngEncoder enc = new PngEncoder(tmp);
            	byte [] out = enc.pngEncode(false, new GrayScaleDoubleToRGBConversion(0.0, 256.0));

            	FileOutputStream fout = new FileOutputStream("result." + i + ".png");
            	fout.write(out);
            	fout.close();
            }
            */
            
        } catch (Exception e) { 
            System.out.println("OOPS: ");
            e.printStackTrace();
        }
    }
}

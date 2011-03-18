package remotesensing.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class DumpAsRGBPNG {

    private static int lines;
    private static int samples;
    private static int bands;
        
    private static String interleave;
    
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
            } else if (l.startsWith("interleave")) { 
            	interleave = l.substring(l.indexOf("=") + 1).trim();
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
        System.out.println("Interleave " + interleave);
    }

    private static void readBandBip(File data, int fileBand, short [][] target, int targetBand, int [] histogram, int pixels) throws IOException {

    	System.out.println("Converting band " + fileBand);

        BufferedInputStream bis = 
            new BufferedInputStream(new FileInputStream(data));

        int skip = fileBand*2;
        
        while (skip > 0) { 
        	skip -= bis.skip(skip);
        }
        
        Arrays.fill(histogram, 0);
        
        int pos = 0;

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        
        int index = 0;
        
        while (index < pixels) { 

            int a = bis.read();
            int b = bis.read();

            // Shift pixels from -32K .. +32K to 0 .. 64K 
            int value = (((a << 8) | (b & 0xff))) + Short.MAX_VALUE;
          
            if (value >=0) { 
            	histogram[value]++;
            }
            
            target[targetBand][pos++] = (short) value;

            if (value > max) { 
                max = value;
            } 
            
            if (value < min) { 
                min = value;
            }
            
            skip = (bands-1)*2;
            
            while (skip > 0) { 
            	skip -= bis.skip(skip);
            }
            
            index++;
        }

        bis.close();        
    }
    
    private static void readDataBIP(File data, int rband, int gband, int bband) throws IOException { 

        long bandsize = (long)lines*samples*2;
        long totalsize = bandsize * bands;

        System.out.println("Reading data file of " + totalsize + " bytes");
        System.out.println("Each band contains " + bandsize + " bytes");

        short [][] image = new short[3][(int)bandsize];
        
        RGBUnsignedShortImage tmp = 
            new RGBUnsignedShortImage(image, lines, samples);

        PngEncoder enc = new PngEncoder(tmp);
    
        int [][] histograms = new int[3][65536];
        
        readBandBip(data, rband, image, 0, histograms[0], lines*samples);
        readBandBip(data, gband, image, 1, histograms[1], lines*samples);
        readBandBip(data, bband, image, 2, histograms[2], lines*samples);
        
        byte [] out = enc.pngEncode(false, new RGBUnsignedShortToRGBConversion(Utils.generateLUT(histograms[0]), 
        		                                                               Utils.generateLUT(histograms[1]), 
        		                                                               Utils.generateLUT(histograms[2])));

        FileOutputStream fout = new FileOutputStream("outLUT.png");
        fout.write(out);
        fout.close();
        /*
        int [] maxValues = new int[3];
        int [] minValues = new int[3];
        
        for (int i=0;i<3;i++) { 
        	minValues[i] = Utils.getMin(histograms[i]);
        	maxValues[i] = Utils.getMax(histograms[i]);
        }
        
        out = enc.pngEncode(false, new RGBUnsignedShortToRGBConversion(minValues, maxValues));

        fout = new FileOutputStream("outRANGE.png");
        fout.write(out);
        fout.close();
        */    
    }
    
    private static void readDataBSQ(File data, int rv, int gv, int bv) throws IOException { 

        long bandsize = (long)lines*samples*2;
        long totalsize = bandsize * 2;

        System.out.println("Reading data file of " + totalsize + " bytes");
        System.out.println("Each band contains " + bandsize + " bytes");

        short [] image = new short[(int)bandsize];

        GrayScaleUnsignedShortImage tmp = 
            new GrayScaleUnsignedShortImage(image, lines, samples);

        PngEncoder enc = new PngEncoder(tmp);

        BufferedInputStream bis = 
            new BufferedInputStream(new FileInputStream(data));

        int index = 0;
        int size = 0;

        int [] histogram = new int[65536];
        
        for (int i=0;i<bands;i++) { 
            System.out.println("Converting band " + i);

            Arrays.fill(histogram, 0);
            
            int pos = 0;

            size += bandsize;

            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            
            while (index < size) { 

                int a = bis.read();
                int b = bis.read();

                int value = (((b & 0xff) << 8) | (a & 0xff));
              
                histogram[value]++;
                
                image[pos++] = (short) value;

                if (value > max) { 
                    max = value;
                } 
                
                if (value < min) { 
                    min = value;
                }
                
                index += 2;
            }

            System.out.println("min " + min + " max " + max + " hist: ");
           
            /*
            int used = 0;
            
            for (int h=0;h<histogram.length;h++) {
                if (histogram[h] != 0) {
                    used++;
                    System.out.println(h + " " + histogram[h]);
                }
            }
            
            System.out.println("used " + used);
            */
            
            byte [] out = enc.pngEncode(false, new GrayScaleUnsignedShortToRGBConversion(histogram));

            FileOutputStream fout = new FileOutputStream("band-" + i + ".png");
            fout.write(out);
            fout.close();
        } 

        bis.close();
    }

    public static void main(String [] args) { 

        try { 
            String header = args[0];
            String data = args[1];

            int r = Integer.parseInt(args[2]);
            int g = Integer.parseInt(args[3]);
            int b = Integer.parseInt(args[4]);
            
            readHeader(new File(header));
    
            if (r > bands || g > bands || b > bands) { 
            	System.out.println("Bands out of range!");
            }
                        
            if (interleave.equalsIgnoreCase("bsq")) { 
            	readDataBSQ(new File(data), r, g, b);
            } else if (interleave.equalsIgnoreCase("bip")) {
            	readDataBIP(new File(data), r, g, b);
            } /*else if (interleave.equalsIgnoreCase("bil")) {
            	readDataBIL(new File(data));
            }*/ else { 
            	System.out.println("Unsupported interleave format: " + interleave);
            }

        } catch (Exception e) { 
            System.out.println("OOPS: ");
            e.printStackTrace();
        }
    }
}


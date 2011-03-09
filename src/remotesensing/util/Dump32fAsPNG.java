package remotesensing.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Dump32fAsPNG {

    private static int lines;
    private static int samples;
    private static int bands;

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

        long bandsize = (long)lines*samples*4;
        long totalsize = bandsize * bands;

        System.out.println("Reading data file of " + totalsize + " bytes");
        System.out.println("Each band contains " + bandsize + " bytes");

        float [] image = new float[(int)bandsize];

        GrayScaleDoubleImage tmp = 
            new GrayScaleDoubleImage(image, lines, samples);

        PngEncoder enc = new PngEncoder(tmp);

        BufferedInputStream bis = 
            new BufferedInputStream(new FileInputStream(data));

        int index = 0;
        int size = 0;

        for (int i=0;i<bands;i++) { 
            System.out.println("Converting band " + i);

            int pos = 0;

            size += bandsize;

            double min = Float.MAX_VALUE;
            double max = Float.MIN_VALUE;

            while (index < size) { 

                int d = bis.read();
                int c = bis.read();
                int b = bis.read();
                int a = bis.read();

                int value = (((a & 0xff) << 24) | ((b & 0xff) << 16) |
                        ((c & 0xff) << 8) | (d & 0xff));

                float pixel = Float.intBitsToFloat(value);

                image[pos++] = pixel;

                if (pixel > max) { 
                    max = pixel;
                } 

                if (pixel < min) { 
                    min = pixel;
                }

                index += 4;
            }

            System.out.println("Min " + min + " max " + max);

            double diff = max - min;

            double delta = diff / 1000.0f; 

            System.out.println("Delta " + delta);

            int [] hist = new int[1000];

            for (int p=0;p<image.length;p++) { 

                float value = image[p];

                float corr = (float) (value - min);

                // System.out.println("Value' " + value);
                // System.out.println("index " + (value / delta));

                int ind = (int) (corr / delta);

                if (ind < 0) { 
              //      System.out.println("EEP: " + ind + " " + value + " " + corr);
                } else { 
                    hist[ind ]++;
                }
            }

            for (int p=0;p<hist.length;p++) { 
                System.out.println(p + " " + hist[p]);
            }

          
            byte [] out = enc.pngEncode(false, new GrayScaleDoubleToRGBConversion(min, max));

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

            readHeader(new File(header));
            readData(new File(data));

        } catch (Exception e) { 
            System.out.println("OOPS: ");
            e.printStackTrace();
        }
    }
}


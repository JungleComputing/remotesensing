package remotesensing.atgp;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import remotesensing.pixels.U8Pixel;
import remotesensing.util.Image;
import remotesensing.util.Image.DataType;
import remotesensing.util.Pixel;
import remotesensing.util.PngEncoder;
import remotesensing.util.Image.ByteOrder;
import remotesensing.util.Image.Interleave;

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

    public static void main(String [] args) { 

        try { 
            String header = args[0];
            String data = args[1];

            readHeader(new File(header));
            readData(new File(data));

            ATGP atgp = new ATGP(image, lines, samples, bands, 30); 
            atgp.run();

            int [][] result = atgp.getResult();

            Image tmp = new Image(lines, samples, 4, DataType.U8, Interleave.BSQ, ByteOrder.LSF, null, "");

            U8Pixel p = new U8Pixel(4);
            p.put(255, 0);
            p.put(255, 1);
            p.put(255, 2);
            p.put(255, 3);
            
            for(int i=0;i<30;i++){
                System.out.printf("\nIteraction [%d]=> %d, %d", i, result[i][0], 
                        result[i][1]);
                
                tmp.putPixel(p, result[i][0] * samples + result[i][1]);
            }

            PngEncoder enc = new PngEncoder(tmp);
            byte [] out = enc.pngEncode(false);

            FileOutputStream fout = new FileOutputStream("result.png");
            fout.write(out);
            fout.close();
            
        } catch (Exception e) { 
            System.out.println("OOPS: ");
            e.printStackTrace();
        }

    }

}

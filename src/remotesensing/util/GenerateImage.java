package remotesensing.util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

public class GenerateImage {

    public static void main(String [] args) throws Exception { 
    
        String name = args[0];
        int lines = Integer.parseInt(args[1]);
        int samples = Integer.parseInt(args[2]);
        int bands = Integer.parseInt(args[3]);
        int pixels = Integer.parseInt(args[4]);
        
        int pixelsPerImage = lines*samples;
    
        short [] image = new short[lines*samples*bands];
        
        int avg = Short.MAX_VALUE / 2;
        int var = Short.MAX_VALUE / 4;
        
        Arrays.fill(image, (short)avg);
        
        Random r = new Random();
        
        PrintWriter header = new PrintWriter(new FileWriter(name + ".hdr"));
        
        header.println("lines = " + lines);
        header.println("samples = " + samples);
        header.println("bands = " + bands);
       
        header.println("description = {");
        header.println("generated test image with pixel values:");
        
        for (int i=0;i<pixels;i++) { 
            
            int line = r.nextInt(lines);
            int sample = r.nextInt(samples);
            
            int index = line * samples + sample;
            
            header.print(line + " x " + sample + " = [");
            
            for (int b=0;b<bands;b++) { 
                image[index + b*pixelsPerImage] += (short) ((r.nextDouble() - 0.5) * var);
                header.print(" " + image[index + b*pixelsPerImage]);
            }
        
            header.println("]");
        }

        header.println("}");
        header.close();
       
        
        /*
        ByteBuffer buf = ByteBuffer.allocate(lines*samples*bands*2);
        buf.asShortBuffer().put(image);
        
        FileOutputStream out = new FileOutputStream(name + ".bsq");
        FileChannel c = out.getChannel();
        c.write(buf);
        c.close();
        */
        
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(name + ".bsq"));
        
        for (int i=0;i<image.length;i++) { 
            
            short pixel = image[i];
            
            int b1 = pixel & 0xff;
            int b2 = (pixel >> 8) & 0xff;
            
            out.write(b1);
            out.write(b2);
        }
      
        out.close();
        
    }
}

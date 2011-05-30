package remotesensing.atgp;

import java.io.FileOutputStream;
import java.util.HashSet;

import remotesensing.pixels.U16Pixel;
import remotesensing.util.PngEncoder;

public class ATGP {

    // NOTE: data is band sequential, so the src array contains several images 
    // one after the other (instead of interleaving per pixel). 
    private final short [] src;

    private final int lines;
    private final int samples;
    private final int bands;
    private final int linesSamples;
    private final int endmembers;

    private final short [] pixel;
    //private final float [] brightness; 
    private final float [] lastValues; 

    private final int [][] result;
    
    private int posMaxBright;

    private HashSet<Integer> used = new HashSet<Integer>(); 
    
    public ATGP(short [] src, int lines, int samples, int bands, int endmembers) { 

        this.src = src;
        this.lines = lines;
        this.samples = samples;
        this.bands = bands;
        this.endmembers = endmembers;
        
        linesSamples = lines * samples;
       
        pixel = new short[bands];
        lastValues = new float[linesSamples];
        result = new int[endmembers][2];
    }

    /*
    public ATGP(UnsignedShortImage img, int endmembers) { 
    	this(img.getData(), img.getLines(), img.getSamples(), img.getBands(), endmembers);
    }
    */
    
    private double brightnessPixel(int index) {

        // This determines the brighness of a pixel, i.e., the length of the 
        // vector respresening the pixel. Note that the sqrt at the end is not
        // needed, as this does not influence the ordering of the vectors.

        double sum = 0.0f;

        for (int b=0;b<bands;b++) {

            double value = (src[index + b*linesSamples] & 0xffff);
            sum += value * value;
        } 

        return sum; // Math.sqrt(sum);
    } 

    private void brightness() {

        double current = 0.0;
        int pos = 0;

        for (int i=0;i<linesSamples;i++) { 
            double tmp = brightnessPixel(i);

            if (tmp > current) { 
                current = tmp;
                pos = i;

                System.out.println("New brightness " + current + " " + i + " " + src[i]);
            }
        }

        setPixel(pos);
    }

    private void setPixel(int pos) { 

        for (int b=0;b<bands;b++) { 
            pixel[b] = src[pos + b*linesSamples]; 
        }

//      System.out.println("set pix " + pos + " " + Arrays.toString(pixel));

        posMaxBright = pos;

        System.out.println("Pos " + posMaxBright);

    }

    /*
    private void maxBrightness() {

        double current = 0.0;
        int pos = 0;

        for (int i=0;i<linesSamples;i++) { 

            double tmp = brightness[i];

            if (tmp > current) {
                current = tmp;
                pos = i;
            }
        }

        setPixel(pos);
    } */

/*
    void maxDistance() { 

        double current = 0.0;
        int pos = 0;

        for (int i=0;i<linesSamples;i++) { 

            double tmp = lastValues[i];

            if (tmp > current) { 

                //        System.out.println("MD tmp > current " + tmp + " > " + current + " " + i);
                current = tmp;
                pos = i;
            }
        }

        lastValues[pos] = 0.0f;

        setPixel(pos);
    }
*/
    
    double spectralAngularDifference(int index) { 

        // SAD determines the angular difference between two vectors (read: 
        // pixels). The higher the number, the more different the pixels are. 
        //
        // SAD is defined as acos((v1 dot v2) / (lenght(v1) * length(v2))

        float value1, value2;
        float dot = 0.0f;
        float len1 = 0.0f;
        float len2 = 0.0f;

        for (int i=0;i<bands;i++) { 
            value1 = (float)(src[index+i*linesSamples] & 0xffff);
            value2 = (float)(pixel[i] & 0xffff);

            dot += value1*value2;
            len1 += value1*value1;
            len2 += value2*value2;
        }

        // NOTE: may produce NAN if len1 and/or len2 == 0!
        final double result = Math.acos(dot / Math.sqrt(len1 * len2));
        lastValues[index] += result;
        return result;
    }

    void atgp() { 

        double current = 0.0;
        int pos = 0;

        for (int i=0;i<linesSamples;i++) {             

            double tmp = spectralAngularDifference(i);

         //   System.out.println("" + tmp);
            
            if (tmp > current && !used.contains(i)) { 
                current = tmp;
                pos = i;
            }
        }
    
        used.add(pos);
        setPixel(pos);
    }

    /*
    void dumpBrightness() { 

        try { 
            GrayScaleDoubleImage tmp = new GrayScaleDoubleImage(brightness, lines, samples);
            PngEncoder encoder = new PngEncoder(tmp);
            byte [] out = encoder.pngEncode(false, new GrayScaleDoubleToRGBConversion(0, brightness[posMaxBright]));

            FileOutputStream fout = new FileOutputStream("brightness.png"); 
            fout.write(out);
            fout.flush(); 
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
     */
    /*

    void dumpLastValues(int t) { 

        try { 
            GrayScaleDoubleImage tmp = new GrayScaleDoubleImage(lastValues, lines, samples);
            PngEncoder encoder = new PngEncoder(tmp);
            byte [] out = encoder.pngEncode(false, new GrayScaleDoubleToRGBConversion(0, lastValues[posMaxBright]));

            FileOutputStream fout = new FileOutputStream("last" + t + ".png"); 
            fout.write(out);
            fout.flush(); 
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
*/

    public void run() { 

        brightness();

        //maxBrightness();
        //dumpBrightness();

        result[0][0] = posMaxBright / samples; // + 1;
        result[0][1] = posMaxBright % samples; // + 1;

        //      System.out.println("pos " + posMaxBright + " " + result[0][0] + " " + result[0][1]);

        for (int t=1;t<30;t++) {

            atgp();

            //        System.out.println("atgp  pixel " + Arrays.toString(pixel));
            //maxDistance();

          //  dumpLastValues(t);

            //      System.out.println("maxdist  pixel " + Arrays.toString(pixel));

            result[t][0] = posMaxBright / samples; // + 1;
            result[t][1] = posMaxBright % samples; // + 1;

            //System.out.println("pos " + posMaxBright + " " + result[t][0] + " " + result[t][1]);
        }               
    }

    public int [][] getResult() { 
        return result;        
    }
    
    private U16Pixel getPixel(int x, int y) { 
    	
    	short [] tmp = new short[bands];
    	
    	int offset = y * lines + x;
    	
    	for (int i=0;i<bands;i++) { 
    		tmp[i] = src[linesSamples * i + offset];
    	}
    	
    	return new U16Pixel(tmp);
    }
    
    public U16Pixel [] getEndMembers() {
    	
    	U16Pixel [] result = new U16Pixel[endmembers];
    	
    	for (int i=0;i<endmembers;i++) {
    		result[i] = getPixel(this.result[i][0], this.result[i][1]);
    	}
      
    	return result;        
    }
    
    

}

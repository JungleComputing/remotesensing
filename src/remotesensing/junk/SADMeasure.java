package remotesensing.junk;

public class SADMeasure extends DoubleMeasure {

    @Override
    public double apply(DoublePixel pixel1, DoublePixel pixel2) {

        final double [] p1 = pixel1.getData();
        final double [] p2 = pixel2.getData();

        double suma = 0.0;
        double suma1 = 0.0;
        double suma2 = 0.0;

        for (int i=0;i<p1.length;i++){
            suma  += p1[i]*p2[i];
            suma1 += p1[i]*p1[i];
            suma2 += p2[i]*p2[i];
        }
        
        final double con1 = Math.sqrt(suma1);
        final double con2 = Math.sqrt(suma2);

        return Math.acos(suma/(con1*con2));
    }

}

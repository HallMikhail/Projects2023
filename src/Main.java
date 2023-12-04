/*
To Do :
Define Good Defaults
Create Better Comments
Include multiple photos
*/

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.lang.Math;

public class Main {
    final static int ThresholdStandardDeviation=1;
    // Number of Standard Deviations, used to Calculate High Threshold
    // Outliers(x) are values which exceed the table below
    //SD = 1 : 68% < x
    //SD = 2 : 95% < x
    //SD = 3 : 99.5% < x
    final static double LowThreshRatio=.2;
    // Used to Calculate Low Threshold
    // Low_Thresh = LowThreshRatio * High Threshold
    public static void main(String[] args) {
        //Inputs
        final double StandardDev=1.2; //Gaussian Standard Deviation, Default : sqrt(2)

        final int radius=7; // (Small value, Fastest Operations : Accurate locally, Less Accurate Globally)
                      // (Middle value, Slower Operations: More Accurate locally, More Accurate Globally)
                      // (Large value, Slowest Operations : Less Accurate Locally, Accurate Globally)



        //Adjusting ThresholdStandardDeviation,LowThreshRatio,StandardDev,radius will lead to different outputs.



        //Constants

        double GaussExponent = 2 * StandardDev * StandardDev;
        double GaussFraction = 1 / (Math.sqrt((2*Math.PI)) * StandardDev);
        int[][] BW;
        int[][] BlurBW;
        int[][] EdgeArray;
        BufferedImage BuffInput = null;
        BufferedImage BuffOutput = null;


        //    Process
        // -> (Input RGB Image(.jpeg))
        // -> (BufferedImage) ImageIO.read
        // -> (GrayScale Array) GSArray()
        // -> (Transformations)
        // GaussianBlur()

        /* SobelH() ->
        // SobelV() ->
        // Suppression(Magnitude() , Direction() )
        // Hysteresis()
        */

        // -> Output


        String File = "Road1.jpg";
        String FileOutput = File.replace(".jpg","_EdgesOutput");
        FileOutput = FileOutput+".jpg";

        try {
        BuffInput = ImageIO.read(new File("./out/Images", File));
        } catch (IOException e)
        {System.out.println("Error: Input Failed to Read");}

        if(BuffInput != null){
            BW = GrayScale.ToArray(BuffInput);
            BlurBW = GaussianBlurGS(BW, radius, GaussFraction, GaussExponent);
            EdgeArray = Sobel.Sobel(BlurBW,ThresholdStandardDeviation,LowThreshRatio);
            BuffOutput = GrayScale.ToImg(EdgeArray);

        }else{System.out.println("Error: Input Image is Null");}

        if(BuffOutput != null) {
            try { //Needs to be improved.
                ImageIO.write(BuffOutput, "jpg", new File("./out./Images",FileOutput));
                System.out.println("Image was written to file.");
            } catch (Exception ex) {
                System.out.println("Error: Writing Output to File");
            }
        }else{System.out.println("Error: Writing Output is null");}
    }


    private static int[] intRGB(int bits) { //0xff Converts to Hexadecimal value (Base 16)
        int[] rgb = {(bits >> 16) & 0xff, (bits >> 8) & 0xff, bits & 0xff};

        for (int i = 0; i < 3; i++) {
            if (rgb[i] < 0) {
                rgb[i] = 0;
            } else if (rgb[i] > 255) {
                rgb[i] = 255;
            }
        }
        return rgb;
    }


    public static int[][] GaussianBlurGS(int[][] BW, int radius, double GaussFraction,double GaussExponential) {

        // @Mask (Holds Gaussian values), to Convolute the Current Pixel with (radius) pixels


        int height = BW.length;
        int width = BW[0].length;
        double norm = 0;
        double Gaussian;
        double[] mask = new double[2 * radius + 1];
        int[][] outBW = new int[height - 2 * radius][width - 2 * radius];

        //Create Gaussian kernel
        for (int x = -radius; x < radius + 1; x++) {
            Gaussian = (GaussFraction) * Math.exp(-((x * x) / GaussExponential));

            mask[x + radius] = Gaussian;
            norm += mask[x + radius];
        }

        //Horizontal Gaussian Convolution
        //Indexes Current pixel at 0, and takes the next (radius) Horizontal pixels to calculate a new "normally distributed" value.

        for (int r = radius; r < height - radius; r++) {
            for (int j = radius; j < width - radius; j++) {
                double sum = 0;

                for (int z = -radius; z < radius + 1; z++) {
                    sum += (mask[z + radius] * BW[r][j + z]);
                }

                //Normalize pixel after blur
                sum /= norm;
                outBW[r - radius][j - radius] = (int) Math.round(sum);
            }
        }

        //Vertical Gaussian Convolution

        //Due to Central Limit Theorem,
        //A 1D Horizontal Convolution -> 1D Vertical Convolution = 2D Gaussian Kernel
        //This is the only function that follows this.

        //Indexes Current pixel at 0, and takes the next (radius) Vertical pixels to calculate a new "normally distributed" value.
        height = outBW.length;
        width  = outBW[0].length;
        for (int r = radius; r < height - radius; r++) {
            for (int j = radius; j < width - radius; j++) {
                double sum = 0;

                for (int z = -radius; z < radius + 1; z++) {
                    sum += (mask[z + radius] * outBW[r + z][j]); //Uses Horizontal Grayscale as input
                }

                //Normalize pixel after blur
                sum /= norm;
                outBW[r - radius][j - radius] = (int) Math.round(sum);
            }
        }
        return outBW;
    }

/* public static int[][][] RGBArray(BufferedImage img) {
        int[][][] rgb = null;
        int height = img.getHeight();
        int width = img.getWidth();

        if (height > 0 && width > 0) {
            rgb = new int[height][width][3];

            for (int row = 0; row < height; row++) {
                for (int column = 0; column < width; column++) {
                    rgb[row][column] = intRGB(img.getRGB(column, row));
                }
            }
        }
        return rgb;
    } //Used this for outputting to Excel File.
*/

    //Grabs the [rr,gg,bb] value, encoded in each pixel.
    //Used in Testing for Grayscale Conversions
}



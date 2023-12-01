/*
To Do : Add Sobel(), Suppression() and Hysteresis() methods
Fix Output File (Readable in photo editor like GIMP), but left-clicking doesn't open it.

Make Directory File, for General User and not my system.
Add Methods to Classes for Better Readability
*/

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.lang.Math;

public class Main {
    public static void main(String[] args) {

        //Inputs
        double StandardDev=Math.sqrt(2); //To be input (Def sqrt(2) tho)

        int radius=7; // (Small value, Fastest Operations : Accurate locally, Less Accurate Globally)
                      // (Middle value, Slower Operations: More Accurate locally, More Accurate Globally)
                      // (Large value, Slowest Operations : Less Accurate Locally, Accurate Globally)
        //Constants

        double GaussExponent = 2 * StandardDev * StandardDev;
        double GaussFraction = 1 / (Math.sqrt((2*Math.PI)) * StandardDev);

        int[][] BW;
        int[][] BlurBW;
        BufferedImage BuffInput = null;
        BufferedImage BuffOutput = null;


        //    Process
        // -> (Input RGB Image(.jpeg))
        // -> (BufferedImage) ImageIO.read
        // -> (GrayScale Array) GSArray()
        // -> (Transformations)
        // GaussianBlur()

        /* SobelH() ->         (TO BE IMPLEMENTED)
        // SobelV() ->
        // Suppression( Magnitude() , Direction() )
        // Hysteresis()
        */

        // -> Output





//System.out.println(System.getProperty("user.dir"));

        try {
        BuffInput = ImageIO.read(new File("C:\\Users\\shiny\\IdeaProjects\\EdgeDetection_CS_201\\out\\Images", "Road1.jpg"));
        } catch (IOException e) {
        System.out.println("Error: Input Failed to Read");
        }

        //

        if(BuffInput != null){
            BW = GSArray(BuffInput);
            BlurBW = GaussianBlurGS(BW, radius, GaussFraction, GaussExponent);
            BuffOutput = GSImg(BlurBW);
        }else{System.out.println("Error: Input Image is Null");}

        if(BuffOutput != null) {
            try { //Needs to be improved.
                ImageIO.write(BuffOutput, "jpg", new File("GaussianBlurredGrayScale.jpg"));
                System.out.println("Image was written to file.");
            } catch (Exception ex) {
                System.out.println("Error: Writing Output to File");
            }
        }else{System.out.println("Error: Writing Output is null");}







//        RGBArray(Buffimg);
    }

    /*public static int[][][] RGBArray(BufferedImage img) {
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
        height =outBW.length;
        width = outBW[0].length;
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

    public static int[][] GSArray(BufferedImage Buffimg) {
        int[][] gs = null;
        int height = Buffimg.getHeight();
        int width = Buffimg.getWidth();

        if (height > 0 && width > 0) {
            gs = new int[height][width];

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int bits = Buffimg.getRGB(j, i);
                    //Grayscale Approximation R(0.2989)+G(0.587)+B(0.114)
                    //The weights model how the human eye perceives light information (Found from Psychological Testing)
                    double GrayValue = Math.round((((bits >> 16) & 0xff)*0.299 + ((bits >> 8) & 0xff)*0.587 + (bits & 0xff))*0.114);
                    gs[i][j] = (int) GrayValue;
                }
            }
        }
        return gs;
    }

    public static BufferedImage GSImg(int[][] BlurBW) {
        BufferedImage GrayScaleImg = null;
        int height = BlurBW.length;
        int width = BlurBW[0].length;

        if (height > 0 && width > 0) {
            GrayScaleImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    GrayScaleImg.setRGB(j, i, (BlurBW[i][j] << 16) | (BlurBW[i][j] << 8) | (BlurBW[i][j]));
                }
            }
        }
        return GrayScaleImg;
    }
}



import java.awt.image.BufferedImage;

public class GrayScale {


    //Converts BufferedImage into an int[][] Array.
    public static int[][] ToArray(BufferedImage Buffimg) {
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


    //Converts an int[][] array -> BufferedImage
    public static BufferedImage ToImg(int[][] BlurBW) {
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

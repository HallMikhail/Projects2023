public class Sobel {
    private static final int[][] MASK_H = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
    private static final int[][] MASK_V = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};

    private static int mean;
    private static int MagStandardDeviation;
    private static double[][] mag;
    private static int[][] dir;


    public static int[][] Sobel(int[][] GrayBlur, int ThresholdStandardDeviation, double Threshold_Ratio) {
        int[][] Dx = Horizontal(GrayBlur);
        int[][] Dy = Vertical(GrayBlur);
        mag = Magnitude(Dx, Dy);
        dir = Direction(Dx, Dy);
        Suppression(mag, dir);
        return Hysteresis(ThresholdStandardDeviation, Threshold_Ratio);
    }



    public static int[][] Horizontal(int[][] raw) {
        int[][] out = null;
        int height = raw.length;
        int width = raw[0].length;

        if (height > 2 && width > 2) {
            out = new int[height - 2][width - 2];

            for (int r = 1; r < height - 1; r++) {
                for (int c = 1; c < width - 1; c++) {
                    int sum = 0;

                    for (int kr = -1; kr < 2; kr++) {
                        for (int kc = -1; kc < 2; kc++) {
                            sum += (MASK_H[kr + 1][kc + 1] * raw[r + kr][c + kc]);
                        }
                    }

                    out[r - 1][c - 1] = sum;
                }
            }
        }

        return out;
    }

    public static int[][] Vertical(int[][] raw) {
        int[][] out = null;
        int height = raw.length;
        int width = raw[0].length;

        if (height > 2 || width > 2) {
            out = new int[height - 2][width - 2];

            for (int r = 1; r < height - 1; r++) {
                for (int c = 1; c < width - 1; c++) {
                    int sum = 0;

                    for (int kr = -1; kr < 2; kr++) {
                        for (int kc = -1; kc < 2; kc++) {
                            sum += (MASK_V[kr + 1][kc + 1] * raw[r + kr][c + kc]);
                        }
                    }

                    out[r - 1][c - 1] = sum;
                }
            }
        }

        return out;
    }


    private static double[][] Magnitude(int[][] Dx, int[][] Dy) {
        double sum = 0;
        double Magvar = 0;
        int height = Dx.length;
        int width = Dx[0].length;
        double pixelTotal = height * width;
        double[][] mag = new double[height][width];

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                mag[r][c] = Math.sqrt(Dx[r][c] * Dx[r][c] + Dy[r][c] * Dy[r][c]);

                sum += mag[r][c];
            }
        }
        mean = (int) Math.round(sum / pixelTotal);

        //Get variance
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                double diff = mag[r][c] - mean;

                Magvar += (diff * diff);
            }
        }
        MagStandardDeviation = (int) Math.sqrt(Magvar / pixelTotal);
        return mag;
    }

    private static int[][] Direction(int[][] Dx, int[][] Dy) {
        int height = Dx.length;
        int width = Dx[0].length;
        double piRad = 180 / Math.PI;
        int[][] dir = new int[height][width];

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                double angle = Math.atan2(Dy[r][c], Dx[r][c]) * piRad;    //Convert radians to degrees

                //Turns Negative Angles into Positive.
                if (angle < 0) {
                    angle += 360;
                }

                //Standardize the Angle to Quad 1,2
                if (angle <= 180) {
                    angle = angle - 180;
                }

                //Pixel Direction is rounded to (0,45,90,135)

                if (angle < 22.5 || angle >= 157.5) {
                    dir[r][c] = 0;
                }
                if (22.5 <= angle && angle < 67.5) {
                    dir[r][c] = 45;
                }
                if (67.5 <= angle && angle < 112.5) {
                    dir[r][c] = 90;
                }
                if (112.5 <= angle && angle < 157.5) {
                    dir[r][c] = 135;
                }
            }
        }
        return dir;
    }


    private static void Suppression(double[][] mag, int[][] dir) {
        int height = mag.length - 1;
        int width = mag[0].length - 1;

        for (int r = 1; r < height; r++) {
            for (int c = 1; c < width; c++) {
                double magnitude = mag[r][c];

                switch (dir[r][c]) {
                    case 0:
                        if (magnitude < mag[r][c - 1] && magnitude < mag[r][c + 1]) {
                            mag[r - 1][c - 1] = 0;
                        }
                        break;
                    case 45:
                        if (magnitude < mag[r - 1][c + 1] && magnitude < mag[r + 1][c - 1]) {
                            mag[r - 1][c - 1] = 0;
                        }
                        break;
                    case 90:
                        if (magnitude < mag[r - 1][c] && magnitude < mag[r + 1][c]) {
                            mag[r - 1][c - 1] = 0;
                        }
                        break;
                    case 135:
                        if (magnitude < mag[r - 1][c - 1] && magnitude < mag[r + 1][c + 1]) {
                            mag[r - 1][c - 1] = 0;
                        }
                        break;
                }
            }
        }
    }

    private static int[][] Hysteresis(int ThresholdStandardDeviation, double LowThreshRatio) {
        int height = mag.length - 1;
        int width = mag[0].length - 1;
        int[][] Edge = new int[height - 1][width - 1];
        double ThresholdH, ThresholdL;
        int white = 255, black = 0;
        //
        // Inverts the Colors
        // int white=0,black=255;


        ThresholdH = mean + (ThresholdStandardDeviation * MagStandardDeviation);
        //Magnitude >= HighThreshold is an Edge Pixel.

        //Magnitude < LowThresh not an Edge Pixel.

        // LowThresh < Magnitude < HighThreshold :
        // Checks if there are any multiple edges in the 3x3 area around the pixel.
        // Connected = True, if there is another edge / Connected = False, if there isn't another edge

        ThresholdL = ThresholdH * LowThreshRatio;

        boolean connected = false;
        for (int r = 1; r < height; r++) {
            for (int c = 1; c < width; c++) {
                double magnitude = mag[r][c];

                if (magnitude >= ThresholdH) {
                    Edge[r - 1][c - 1] = white;
                } //White = 255
                else if (magnitude < ThresholdL) {
                    Edge[r - 1][c - 1] = black;
                }  //Black = 0
                else {
                    connected = false;
                }

                for (int nr = -1; nr < 2; nr++) {
                    for (int nc = -1; nc < 2; nc++) {
                        if (mag[r + nr][c + nc] >= ThresholdH) {
                            connected = true;
                        }
                    }
                }
                if (connected) {
                    Edge[r - 1][c - 1] = white;
                } else {
                    Edge[r - 1][c - 1] = black;
                }
            }
        }
        return Edge;
    }
}


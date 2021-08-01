package jp.mydns.dego.zanzo.Util;

public class PixelHelper {

    // ---------------------------------------------------------------------------------------------
    // constant values
    // ---------------------------------------------------------------------------------------------
//    private static final String TAG = "PixelHelper";

    // ---------------------------------------------------------------------------------------------
    // public method
    // ---------------------------------------------------------------------------------------------

    /**
     * getR
     *
     * @param pixel pixel data
     * @return red element in pixel data
     */
    public static int getR(int pixel) {
        return (pixel & 0x00FF0000) >> 16;
    }

    /**
     * getG
     *
     * @param pixel pixel data
     * @return green element in pixel data
     */
    public static int getG(int pixel) {
        return (pixel & 0x0000FF00) >> 8;
    }

    /**
     * getB
     *
     * @param pixel pixel data
     * @return blue element in pixel data
     */
    public static int getB(int pixel) {
        return (pixel & 0x000000FF);
    }

//    /**
//     * getA
//     *
//     * @param pixel pixel data
//     * @return alpha element in pixel data
//     */
//    public static int getA(int pixel) {
//        return (pixel & 0xFF000000) >> 24;
//    }

    /**
     * average
     *
     * @param pixel1 pixel data 1
     * @param pixel2 pixel data 2
     * @return average pixel
     */
    public static int average(int pixel1, int pixel2) {
        int r = (PixelHelper.getR(pixel1) + PixelHelper.getR(pixel2)) / 2;
        int g = (PixelHelper.getG(pixel1) + PixelHelper.getG(pixel2)) / 2;
        int b = (PixelHelper.getB(pixel1) + PixelHelper.getB(pixel2)) / 2;

        return (0xFF000000) | ((int) r << 16) | ((int) g << 8) | ((int) b);
    }

//    /**
//     * average
//     *
//     * @param pixel1  pixel data 1
//     * @param weight1 weight 1
//     * @param pixel2  pixel data 2
//     * @param weight2 weight 2
//     * @return weighted average pixel
//     */
//    public static int average(int pixel1, int weight1, int pixel2, int weight2) {
//        int r1 = (pixel1 & 0x00FF0000) >> 16;
//        int g1 = (pixel1 & 0x0000FF00) >> 8;
//        int b1 = (pixel1 & 0x000000FF);
//
//        int r2 = (pixel2 & 0x00FF0000) >> 16;
//        int g2 = (pixel2 & 0x0000FF00) >> 8;
//        int b2 = (pixel2 & 0x000000FF);
//
//        int sum = weight1 + weight2;
//        int r = (r1 * weight1 + r2 * weight2) / sum;
//        int g = (g1 * weight1 + g2 * weight2) / sum;
//        int b = (b1 * weight1 + b2 * weight2) / sum;
//
//        return (0xFF000000) | ((int) r << 16) | ((int) g << 8) | ((int) b);
//    }

    /**
     * average
     *
     * @param basePixel base pixel
     * @param newPixel  new pixel
     * @param count     count
     * @return average pixel
     */
    public static int average(int basePixel, int newPixel, int count) {
        int baseR = (basePixel & 0x00FF0000) >> 16;
        int baseG = (basePixel & 0x0000FF00) >> 8;
        int baseB = (basePixel & 0x000000FF);

        int newR = (newPixel & 0x00FF0000) >> 16;
        int newG = (newPixel & 0x0000FF00) >> 8;
        int newB = (newPixel & 0x000000FF);

        int r = ((baseR * count + newR) / (count + 1));
        int g = ((baseG * count + newG) / (count + 1));
        int b = ((baseB * count + newB) / (count + 1));

        return (0xFF000000) | ((int) r << 16) | ((int) g << 8) | ((int) b);
    }

//    /**
//     * distanceSq
//     *
//     * @param pixel1 pixel data 1
//     * @param pixel2 pixel data 2
//     * @return distance
//     */
//    public static int distance(int pixel1, int pixel2) {
//        int r1 = (0x00FF0000 & pixel1) >> 16;
//        int g1 = (0x0000FF00 & pixel1) >> 8;
//        int b1 = (0x000000FF & pixel1);
//
//        int r2 = (0x00FF0000 & pixel2) >> 16;
//        int g2 = (0x0000FF00 & pixel2) >> 8;
//        int b2 = (0x000000FF & pixel2);
//
//        int distance = 0;
//        distance += (r1 - r2) * (r1 - r2);
//        distance += (g1 - g2) * (g1 - g2);
//        distance += (b1 - b2) * (b1 - b2);
//
//        return (int) Math.sqrt((double) distance);
//    }

    /**
     * distanceSq
     *
     * @param pixel1 pixel data 1
     * @param pixel2 pixel data 2
     * @return distance
     */
    public static int distanceSq(int pixel1, int pixel2) {
        int r1 = (0x00FF0000 & pixel1) >> 16;
        int g1 = (0x0000FF00 & pixel1) >> 8;
        int b1 = (0x000000FF & pixel1);

        int r2 = (0x00FF0000 & pixel2) >> 16;
        int g2 = (0x0000FF00 & pixel2) >> 8;
        int b2 = (0x000000FF & pixel2);

        int distance = 0;
        distance += (r1 - r2) * (r1 - r2);
        distance += (g1 - g2) * (g1 - g2);
        distance += (b1 - b2) * (b1 - b2);

        return distance;
    }

    // ---------------------------------------------------------------------------------------------
    // Private Method
    // ---------------------------------------------------------------------------------------------

}

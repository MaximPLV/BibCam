package org.example;

import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;

public class MyMat {
    public static boolean compare(Mat mat1, Mat mat2) {
        if (mat1.height() != mat2.height() || mat1.width() != mat2.width()) throw new RuntimeException("matrices have to be the same size!");

        long a = System.currentTimeMillis();

        for (int y = 0; y < mat1.height(); y++) {
            for (int x = 0; x < mat1.width(); x++) {
                int blue1 = (int) mat1.get(y, x)[0];
                int blue2 = (int) mat2.get(y, x)[0];
                int green1 = (int) mat1.get(y, x)[1];
                int green2 = (int) mat2.get(y, x)[1];
                int red1 = (int) mat1.get(y, x)[2];
                int red2 = (int) mat2.get(y, x)[2];
                if (!cmpInts(blue1, blue2) || !cmpInts(green1, green2) || !cmpInts(red1, red2)) return false;
            }
        }
        System.out.println(System.currentTimeMillis() - a); //ca. 100ms
        return true;
    }

    private static boolean cmpInts(int val1, int val2) {
        return val1 - val2 >= -20 && val1 - val2 <= 20;

    }

    public static boolean compareParallel(Mat mat1, Mat mat2) {
        if (mat1.height() != mat2.height() || mat1.width() != mat2.width()) throw new RuntimeException("matrices have to be the same size!");

        /*IntStream.range(0, mat1.height() * mat1.width())
                .parallel()
                .forEach(i -> {
                    try {
                        int y =
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });*/
        return false;
    }

    public static void store(Mat mat) {
        BufferedImage image = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < mat.height(); y++) {
            for (int x = 0; x < mat.width(); x++) {
                int blue = (int) mat.get(y, x)[0];
                int green = (int) mat.get(y, x)[1];
                int red = (int) mat.get(y, x)[2];
                int rgb = (red << 16) + (green << 8) + blue;
                image.setRGB(x, y, rgb);
            }
        }
        File outputFile = new File("src/main/resources/outputImage.png");
        try {
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package org.example;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;

public class CameraAccess {

    static {
        try {
            // Directly load the OpenCV native library
            System.load("C:\\Users\\maxim\\Downloads\\opencv\\build\\java\\x64\\opencv_java490.dll"); // Replace with the actual path
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        VideoCapture camera = new VideoCapture(0); // 0 for default camera

        if (!camera.isOpened()) {
            System.out.println("Error: Camera not accessible");
            return;
        }

        Mat frame = new Mat();
        while (true) {
            if (camera.read(frame)) {
                HighGui.imshow("Camera Feed", frame);
                if (HighGui.waitKey(10) == 27) { // Exit on 'ESC'
                    break;
                }
            }
        }

        camera.release();
        HighGui.destroyAllWindows();
    }
}


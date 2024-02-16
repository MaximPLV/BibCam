package org.example;
import org.example.MyMat;
import org.example.TelegramBot;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;

public class CameraAccess {



    static {
        try {
            // Directly load the OpenCV native library
            System.load(System.getProperty("user.dir") + "\\src\\main\\resources\\opencv_java490.dll"); // Replace with the actual path
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Full Screen Message");
        createJFrame(frame);

        TelegramBot bot = new TelegramBot();
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        VideoCapture camera = new VideoCapture(0); // 0 for default camera

        if (!camera.isOpened()) {
            System.out.println("Error: Camera not accessible");
            return;
        }

        Mat currentFrame = new Mat();
        Mat prevFrame = new Mat();

        System.out.println(camera.get(Videoio.CAP_PROP_FPS));

        System.out.println(camera.getBackendName());
        long startTime = System.currentTimeMillis();
        long prevTime1 = startTime;
        long prevTime2 = startTime;
        boolean colorToggle = false;
        boolean lockPC = false;

        while (true) {
            if (camera.read(currentFrame)) {
                System.out.println(Arrays.toString(currentFrame.get(200, 200)));

                HighGui.imshow("Camera Feed", currentFrame);
                if (HighGui.waitKey(10) == 27) { // Exit on 'ESC'
                    break;
                }
                Mat blurredCurrentFrame = new Mat();
                Imgproc.GaussianBlur(currentFrame, blurredCurrentFrame, new Size(81, 81), 0);

                if (bot.isSystemReady()) {
                    if (!prevFrame.empty() && !MyMat.compare(blurredCurrentFrame, prevFrame)) {
                        System.out.println("changed");
                        if (System.currentTimeMillis() - prevTime1 > 100) {
                            prevTime1 = System.currentTimeMillis();
                            Color color = colorToggle ? Color.BLUE : Color.RED;
                            frame.getContentPane().setBackground(color);
                            colorToggle = !colorToggle;
                        }
                        frame.setVisible(true);
                        if (System.currentTimeMillis() - prevTime2 > 5000) {
                            prevTime2 = System.currentTimeMillis();
                            MyMat.store(currentFrame);
                            bot.sendImageUploadingAFile("src/main/resources/outputImage.png");
                        }
                        lockPC = true;
                    }
                    if (lockPC && System.currentTimeMillis() - startTime > 10000)  {
                        try {
                            Runtime.getRuntime().exec("rundll32 user32.dll,LockWorkStation");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    frame.setVisible(false);
                }
                prevFrame = blurredCurrentFrame.clone();
            } else {
                throw new RuntimeException("Camera is not accessible!");
            }
        }

        camera.release();
        HighGui.destroyAllWindows();
    }

    private static void createJFrame(JFrame frame) {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Set full-screen
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);

        // Create a label with your message
        JLabel label = new JLabel("STOP", SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.BOLD, 800));
        label.setForeground(Color.WHITE);

        // Add label to frame
        frame.add(label);

        // Set the frame background color
        frame.getContentPane().setBackground(Color.RED);
    }
}


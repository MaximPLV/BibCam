package org.example;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CameraAccess {

    static {
        try {
            System.load(System.getProperty("user.dir") + "\\src\\main\\resources\\opencv_java490.dll");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        JFrame jFrame = createJFrame();

        TelegramBot bot = new TelegramBot();
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        VideoCapture camera = new VideoCapture(0); //0 for default camera

        if (!camera.isOpened()) {
            throw new RuntimeException("Camera is not accessible!");
        }


        System.out.println("Camera name: " + camera.getBackendName());
        System.out.println("Camera FPS: " + camera.get(Videoio.CAP_PROP_FPS));
        System.out.println("Camera res: " + camera.get(Videoio.CAP_PROP_FRAME_HEIGHT) + " * " + camera.get(Videoio.CAP_PROP_FRAME_WIDTH));

        Mat currentFrame = new Mat();
        Mat prevFrame = new Mat();
        String filename = "src/main/resources/output.mp4";
        int fourcc = VideoWriter.fourcc('X', '2', '6', '4'); //X264 codec
        double frameRate = 30;
        Size frameSize = new Size(camera.get(Videoio.CAP_PROP_FRAME_WIDTH), camera.get(Videoio.CAP_PROP_FRAME_HEIGHT));
        VideoWriter writer = new VideoWriter(filename, fourcc, frameRate, frameSize, true);

        boolean motionDetected = false;
        Color color = Color.BLUE;

        int loopCounter = 0;

        //less equals 30 iterations per second
        while (true) {
            if (camera.read(currentFrame)) {

                HighGui.imshow("Camera Feed", currentFrame);
                if (HighGui.waitKey(10) == 27) { // Exit on 'ESC'
                    break;
                }

                if (bot.isSystemReady()) {
                    Mat blurredCurrentFrame = new Mat();

                    if (!motionDetected) {
                        Imgproc.GaussianBlur(currentFrame, blurredCurrentFrame, new Size(81, 81), 0);

                        if (!prevFrame.empty() && !MyMat.compare(blurredCurrentFrame, prevFrame)) {
                            motionDetected = true;

                            if (!jFrame.isVisible()) {
                                jFrame.setVisible(true);
                            }
                        }
                    }

                    if (motionDetected) {
                        color = color.equals(Color.BLUE) ? Color.RED : Color.BLUE;
                        jFrame.getContentPane().setBackground(color);

                        writer.write(currentFrame);

                        //every 90 iterations ~ every 3 seconds
                        if (loopCounter % 90 == 0) {
                            executorService.submit(() -> bot.sendImage(currentFrame));
                        }

                        //every 300 iterations ~ every 10 seconds
                        if (loopCounter % 300 == 0) {
                            writer.release();
                            executorService.submit(() -> {
                                bot.sendVideo();
                                /*ProcessBuilder builder = new ProcessBuilder("rundll32", "user32.dll,LockWorkStation");
                                builder.inheritIO();
                                try {
                                    System.out.println("cmd");
                                    builder.start();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }*/
                            });
                        }
                    }
                    prevFrame = blurredCurrentFrame.clone();
                    loopCounter++;
                } else {
                    jFrame.setVisible(false);
                }
            } else {
                throw new RuntimeException("Camera is not accessible!");
            }
        }

        camera.release();
        HighGui.destroyAllWindows();
        executorService.shutdown();
    }

    private static JFrame createJFrame() {
        JFrame frame = new JFrame("Full Screen Message");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);

        JLabel label = new JLabel("STOP", SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.BOLD, 800));
        label.setForeground(Color.WHITE);

        frame.add(label);

        frame.getContentPane().setBackground(Color.RED);

        return frame;
    }
}


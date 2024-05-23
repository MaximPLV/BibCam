package org.example;


import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.Properties;

public class TelegramBot extends TelegramLongPollingBot {

    private static final String botToken;
    private static final String chatId;

    static {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("telegramBot.properties")){
            prop.load(input);
            botToken = prop.getProperty("botToken");
            chatId = prop.getProperty("chatId");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private volatile boolean systemReady = false;
    public TelegramBot() {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            if (update.getMessage().getText().equals("ok")) {
                systemReady = true;
                message.setText("System ready");
                System.out.println(systemReady);

            } else if (update.getMessage().getText().equals("stop")) {
                systemReady = false;
                message.setText("System stopped");
            } else {
                message.setText(update.getMessage().getChatId().toString()); //default message text: chatID
            }
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "BibCamBot";
    }

    public void sendImage(Mat image) {
        MatOfByte pixels = new MatOfByte();
        Imgcodecs.imencode(".png", image, pixels);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(pixels.toArray());

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(inputStream, "image.png"));
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendVideo(File videoFile) {
        SendVideo sendVideo = new SendVideo();
        sendVideo.setChatId(chatId);
        sendVideo.setVideo(new InputFile(videoFile));
        try {
            execute(sendVideo);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public boolean isSystemReady() {
        return systemReady;
    }
}

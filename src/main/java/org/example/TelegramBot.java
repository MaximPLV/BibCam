package org.example;


import org.opencv.highgui.HighGui;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.DeleteChatPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TelegramBot extends TelegramLongPollingBot {

    private final List<Integer> messages;
    private static final String chatId = "5594583255";
    private volatile boolean systemReady = false;
    public TelegramBot() {
        super("6537723548:AAGaA2uywvpZd37M8Hhq4dbHBBbkyHueND8");
        messages = new ArrayList<>();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
            message.setChatId(chatId);
            if (update.getMessage().getText().equals("ok")) {
                systemReady = true;
                message.setText("System ready");
                System.out.println(systemReady);

            } else if (update.getMessage().getText().equals("stop")) {
                systemReady = false;
                message.setText("System stopped");
            } else {
                messages.add(update.getMessage().getMessageId());
                message.setText(update.getMessage().getChatId().toString());
            }
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "BibCamBot";
    }

    public void sendImageUploadingAFile(String filePath) {
        // Create send method
        SendPhoto sendPhotoRequest = new SendPhoto();
        // Set destination chat id
        sendPhotoRequest.setChatId("5594583255");
        // Set the photo file as a new photo (You can also use InputStream with a constructor overload)
        sendPhotoRequest.setPhoto(new InputFile(new File(filePath)));
        try {
            // Execute the method
            execute(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public boolean isSystemReady() {
        return systemReady;
    }
}

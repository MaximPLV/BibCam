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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
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
    private final List<Integer> messages;
    private volatile boolean systemReady = false;
    public TelegramBot() {
        super(botToken);
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
                message.setText(update.getMessage().getChatId().toString()); //default message text: chatID
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
        sendPhotoRequest.setChatId(chatId);
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

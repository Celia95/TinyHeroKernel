package com.raincoatmoon.Core;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.raincoatmoon.Core.Command;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static String getExtension(String fileName) {
        Pattern p = Pattern.compile("(.+?)(\\.[^\\.]+?)$");
        Matcher m = p.matcher(fileName);
        if (m.find()) return m.group(2);
        return "";
    }

    public static String setExtension(String fileName, String ext) {
        return fileName.replaceAll("(\\.[^\\.]+?)$", ext);
    }

    public static String removeHTMLTags(String html) {
        return html.replaceAll("<\\s*/?\\s*br\\s*/?\\s*>", "\n")
                .replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
    }

    public static boolean validURL(String url) {
        UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
        return urlValidator.isValid(url);
    }

    public static Command processCommandFromMessage(Message message) {
        Command cmd = processCommand(message.from(), message.chat(), message.text());
        return cmd;
    }

    public static Command processCommandFromCallback(CallbackQuery callbackQuery) {
        Command cmd = processCommand(callbackQuery.from(), callbackQuery.message().chat(), callbackQuery.data());
        cmd.setBotMessage(callbackQuery.message());
        return cmd;
    }

    private static boolean isNumeric(String string) {
        return string.chars().allMatch(Character::isDigit);
    }

    public static Command processCommand(User user, Chat chat, String text) { //TODO: Set regex as static variables
        if (text != null) {
            Pattern p = Pattern.compile("\\/[a-zA-Z0-9]+(@[a-zA-Z]*)?");
            Matcher matcher = p.matcher(text);
            if (matcher.find() && matcher.start() == 0) {
                String cmd = text.substring(matcher.start(), matcher.end());
                String par = " ";
                if (matcher.end() + 1 < text.length()) {
                    par = text.substring(matcher.end() + 1, text.length());
                }
                String [] params = par.split("\\s+");
                cmd = cmd.replaceAll("\\@[a-zA-Z]*", "");
                Command command = new Command(user, chat, cmd, new ArrayList<>(Arrays.asList(params)));
                if (isNumeric(cmd.substring(1))) command.setVolatile(true);
                return command;
            }
        }
        return null;
    }
}

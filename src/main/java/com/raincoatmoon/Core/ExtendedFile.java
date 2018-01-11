package com.raincoatmoon.Core;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.User;

public class ExtendedFile {
    private String fullPath;
    private File file;
    private Document document;
    private String internalPath;
    private Chat chat;
    private User user;
    private String fileName;

    public ExtendedFile(String fullPath, File file, Document document, Chat chat, User user) {
        this.fullPath = fullPath;
        this.file = file;
        this.document = document;
        this.chat = chat;
        this.user = user;
    }

    public ExtendedFile(ExtendedFile extendedFile, String internalPath, String alias) {
        this.fullPath = extendedFile.fullPath;
        this.file = extendedFile.file;
        this.document = extendedFile.document;
        this.chat = extendedFile.chat;
        this.user = extendedFile.user;
        this.internalPath = internalPath;
        fileName = alias;
    }

    public String getFullPath() {
        return fullPath;
    }

    public File getFile() {
        return file;
    }

    /*public Document getDocument() {
        return document;
    }*/

    public String getInternalPath() {
        return internalPath;
    }

    public void setInternalPath(String internalPath) {
        this.internalPath = internalPath;
    }

    public Chat getChat() {
        return chat;
    }

    public User getUser() {
        return user;
    }

    public String getFileName() {
        return fileName != null? fileName: document.fileName();
    }
}

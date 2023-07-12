package com.hangout.hangout.domain.image.util;

public enum FileType {
    PNG("png"),
    JPEG("jpeg"),
    JPG("jpg"),
    GIF("gif");

    private final String extension;

    FileType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}

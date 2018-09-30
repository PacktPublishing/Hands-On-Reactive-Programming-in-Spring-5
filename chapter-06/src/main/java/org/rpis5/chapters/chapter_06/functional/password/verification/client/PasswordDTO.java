package org.rpis5.chapters.chapter_06.functional.password.verification.client;

public class PasswordDTO {
    private String raw;
    private String secured;


    PasswordDTO(String raw, String secured) {
        this.raw = raw;
        this.secured = secured;
    }

    public String getRaw() {
        return raw;
    }

    public String getSecured() {
        return secured;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public void setSecured(String secured) {
        this.secured = secured;
    }
}

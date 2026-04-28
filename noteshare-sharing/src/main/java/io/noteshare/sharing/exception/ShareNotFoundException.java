package io.noteshare.sharing.exception;

public class ShareNotFoundException extends RuntimeException {
    public ShareNotFoundException() {
        super("Share link not found");
    }
}

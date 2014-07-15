package org.simpleim.common.message;

public class ReceiveMessageNotification extends Notification {

    private User sender;

    private ChatMessage message;

    public User getSender() {
        return sender;
    }

    public ReceiveMessageNotification setSender(User sender) {
        this.sender = sender;
        return this;
    }

    public ChatMessage getMessage() {
        return message;
    }

    public ReceiveMessageNotification setMessage(ChatMessage message) {
        this.message = message;
        return this;
    }
}

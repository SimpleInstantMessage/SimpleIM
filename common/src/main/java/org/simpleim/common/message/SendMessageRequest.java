package org.simpleim.common.message;

public class SendMessageRequest extends Request {

    private User sender;

    private String[] targetsIds;

    private ChatMessage message;

    public User getSender() {
        return sender;
    }

    public SendMessageRequest setSender(User sender) {
        this.sender = sender;
        return this;
    }

    public ChatMessage getMessage() {
        return message;
    }

    public SendMessageRequest setMessage(ChatMessage message) {
        this.message = message;
        return this;
    }

    public String[] getTargetsIds() {
        return targetsIds;
    }

    public SendMessageRequest setTargetsIds(String[] targetsIds) {
        this.targetsIds = targetsIds;
        return this;
    }

}

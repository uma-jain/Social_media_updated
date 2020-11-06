package Utils;

public class MessageModel {
    private String messageText, messageTime, sendBy;

    public MessageModel(String messageText, String messageTime, String sendBy) {
        this.messageText = messageText;
        this.messageTime = messageTime;
        this.sendBy = sendBy;
    }

    public MessageModel()
    {

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getSendBy() {
        return sendBy;
    }

    public void setSendBy(String sendBy) {
        this.sendBy = sendBy;
    }
}

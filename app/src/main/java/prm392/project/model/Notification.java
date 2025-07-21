package prm392.project.model;

import com.google.gson.annotations.SerializedName;

public class Notification {

    @SerializedName("id")
    private int Id ;
    @SerializedName("userId")
    private int UserId;
    @SerializedName("title")
    private String Title;

    @SerializedName("message")
    private String Message ;
    @SerializedName("isRead")
     private boolean IsRead;
    @SerializedName("timestamp")
    private String Timestamp;

    public Notification(int id, int userId, String title, String message, boolean isRead) {
        Id = id;
        UserId = userId;
        Title = title;
        Message = message;
        IsRead = isRead;
    }


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }
    public int getUserId() {
        return UserId;
    }
    public void setUserId(int userId) {
        UserId = userId;
    }
    public String getTitle() {
        return Title;
    }
    public void setTitle(String title) {
        Title = title;
    }
    public String getMessage() {
        return Message;
    }
    public void setMessage(String message) {
        Message = message;
    }
    public boolean getIsRead() {
        return IsRead;
    }
    public void setIsRead(boolean isRead) {
        IsRead = isRead;
    }
    public String getTimestamp() {
        return Timestamp;
    }
    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }
}

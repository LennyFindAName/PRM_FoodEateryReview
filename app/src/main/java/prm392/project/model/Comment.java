package prm392.project.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
public class Comment {

    @SerializedName("commentId")
    private int commentId;

    @SerializedName("content")
    private String content;

    @SerializedName("commentDate")
    private String commentDate; // Use String for compatibility with ISO DateTime

    @SerializedName("commentLike")
    private Integer commentLike;

    @SerializedName("userId")
    private Integer userId;

    @SerializedName("username")
    private String username; // From c.User?.DisplayName

    // Empty constructor
    public Comment() {}

    // Getters and Setters
    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public Integer getCommentLike() {
        return commentLike;
    }

    public void setCommentLike(Integer commentLike) {
        this.commentLike = commentLike;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
package prm392.project.model.DTOs;

import java.util.List;
public class CommentRequest  {
    private String content;
    private int blogId;
    private int userId;

    public CommentRequest(String content, int blogId, int userId) {
        this.content = content;
        this.blogId = blogId;
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBlogId() {
        return blogId;
    }

    public void setBlogId(int blogId) {
        this.blogId = blogId;
    }
}
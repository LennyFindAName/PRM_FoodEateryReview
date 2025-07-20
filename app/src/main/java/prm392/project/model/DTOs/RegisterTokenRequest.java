package prm392.project.model.DTOs;

public class RegisterTokenRequest {
    private int userId;
    private String token;

    public RegisterTokenRequest(int userId, String token) {
        this.userId = userId;
        this.token = token;
    }
}
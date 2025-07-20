package prm392.project.inter;

import prm392.project.model.DTOs.RegisterTokenRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AppNotificationService {
    @POST("AppNotification/register-token")
    Call<Void> registerToken(@Body RegisterTokenRequest request);
}
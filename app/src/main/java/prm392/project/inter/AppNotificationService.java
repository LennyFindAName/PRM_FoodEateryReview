package prm392.project.inter;

import prm392.project.model.DTOs.RegisterTokenRequest;
import prm392.project.model.Notification;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import java.util.List;

public interface AppNotificationService {
    @POST("AppNotification/register-token")
    Call<Void> registerToken(@Body RegisterTokenRequest request);

    @GET("AppNotification/getNotifBy/{userId}")
    Call<List<Notification>> GetNotifBy( @Path("userId") int userId);

    @DELETE("AppNotification/deleteNotif/{id}")
    Call<Void> DeleteNotif(@Path("id") int id);
}
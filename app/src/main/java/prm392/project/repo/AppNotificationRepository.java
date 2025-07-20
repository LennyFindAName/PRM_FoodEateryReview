// File: app/src/main/java/prm392/project/repo/AppNotificationRepository.java
package prm392.project.repo;

import android.content.Context;
import prm392.project.factory.APIClient;
import prm392.project.inter.AppNotificationService;
import prm392.project.model.DTOs.RegisterTokenRequest;
import retrofit2.Call;
import retrofit2.Retrofit;

public class AppNotificationRepository {
    private AppNotificationService AppNotificationService;

    public AppNotificationRepository(Context context) {
        Retrofit retrofit = APIClient.getClient(context);
        AppNotificationService = retrofit.create(AppNotificationService.class);
    }

    public Call<Void> registerToken(RegisterTokenRequest request) {
        return AppNotificationService.registerToken(request);
    }
}
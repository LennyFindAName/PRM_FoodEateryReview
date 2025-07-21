package prm392.project.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import prm392.project.R;
import prm392.project.adapter.NotificationAdapter;
import prm392.project.inter.AppNotificationService;
import prm392.project.model.Notification;
import prm392.project.model.User;
import prm392.project.repo.UserRepository;
import prm392.project.utils.BottomNavHelper;
import prm392.project.factory.APIClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private AppNotificationService notificationService;
    private int currentUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerView = findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavHelper.setup(this, bottomNavigationView, R.id.nav_home);

        // Initialize notification service
        Retrofit retrofit = APIClient.getClient(this);
        notificationService = retrofit.create(AppNotificationService.class);

        // Get user and load notifications
        getUserAndLoadNotifications();
    }

    private void getUserAndLoadNotifications() {
        new Thread(() -> {
            UserRepository userRepository = new UserRepository(this);
            Response<User> userResponse;
            try {
                userResponse = userRepository.getUserProfile().execute();
                if (!userResponse.isSuccessful() || userResponse.body() == null) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Failed to get user", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                    return;
                }
                currentUserId = Integer.parseInt(userResponse.body().getUserID());
                runOnUiThread(() -> {
                    // Initialize adapter with delete callback
                    adapter = new NotificationAdapter(new ArrayList<>(), this::deleteNotification);
                    recyclerView.setAdapter(adapter);
                    loadNotifications(currentUserId);
                });
            } catch (IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Failed to get user", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }).start();
    }

    private void loadNotifications(int userId) {
        notificationService.GetNotifBy(userId).enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isEmpty()) {
                        Toast.makeText(NotificationActivity.this, "Không có thông báo", Toast.LENGTH_SHORT).show();
                    } else {
                        // Directly pass the list from API to adapter
                        adapter.setNotifications(response.body());
                    }
                } else {
                    Toast.makeText(NotificationActivity.this, "Không có thông báo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Toast.makeText(NotificationActivity.this, "Lỗi khi tải thông báo: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteNotification(Notification notification) {
        notificationService.DeleteNotif(notification.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(NotificationActivity.this, "Thông báo xóa thành công ", Toast.LENGTH_SHORT).show();
                    // Reload notifications after deletion
                    loadNotifications(currentUserId);
                } else {
                    Toast.makeText(NotificationActivity.this, "Lỗi khi xóa bài viết", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("NotificationActivity", "Delete API failed for notification ID: " + notification.getId() +
                        ", Error: " + t.getMessage());
                Toast.makeText(NotificationActivity.this, "Error deleting notification: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

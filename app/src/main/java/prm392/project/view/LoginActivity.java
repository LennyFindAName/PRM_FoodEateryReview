package prm392.project.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import prm392.project.R;
import prm392.project.factory.APIClient;
import prm392.project.inter.AuthService;
import prm392.project.model.DTOs.LoginRequest;
import prm392.project.model.DTOs.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.edtPassword);
        edtPassword = findViewById(R.id.edtPasswordConfirm);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> loginUser());

        //nagative register
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }

    private void loginUser() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ In thông tin tài khoản ra Logcat
        Log.d("LOGIN_DEBUG", "Username: " + username);
        Log.d("LOGIN_DEBUG", "Password: " + password);

        LoginRequest request = new LoginRequest(username, password);
        AuthService authService = APIClient.getClient(this).create(AuthService.class);
        Call<LoginResponse> call = authService.login(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                // ✅ In response code và body
                Log.d("LOGIN_DEBUG", "Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginData = response.body();

                    Log.d("LOGIN_DEBUG", "AccessToken: " + loginData.getAccessToken());
                    Log.d("LOGIN_DEBUG", "Username (BE response): " + loginData.getUsername());
                    Log.d("LOGIN_DEBUG", "RoleId: " + loginData.getRoleId());

                    // Lưu và chuyển màn
                    SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("access_token", loginData.getAccessToken());
                    editor.putString("username", loginData.getUsername());
                    editor.putInt("role_id", loginData.getRoleId());
                    editor.apply();

                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("LOGIN_DEBUG", "Login failed - Code: " + response.code());
                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LOGIN_DEBUG", "Network error: " + t.getMessage(), t);
                Toast.makeText(LoginActivity.this, "Login failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
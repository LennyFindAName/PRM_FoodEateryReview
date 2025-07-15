package prm392.project.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import prm392.project.R;
import prm392.project.inter.ChatService;
import prm392.project.model.ChatRequest;
import prm392.project.model.ChatResponse;
import prm392.project.model.ChatHistoryModel;
import prm392.project.model.User;
import prm392.project.repo.UserRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {
    private LinearLayout messagesContainer;
    private EditText messageInput;
    private UserRepository userRepository;
    private ChatService chatService;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chat);

        messagesContainer = findViewById(R.id.messages_container);
        messageInput = findViewById(R.id.message_input);
        Button sendButton = findViewById(R.id.send_button);
        userRepository = new UserRepository(this);
        loadUserProfile();
        // Xử lý sự kiện cho BottomNavigationView
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_chat);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(ChatActivity.this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(ChatActivity.this, CartListActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(ChatActivity.this, ProfileActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_create_blog) {
                startActivity(new Intent(ChatActivity.this, CreateBlogActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_chat) {
                // Đang ở màn chat, không cần chuyển
                return true;
            }
            return false;
        });

        // Sử dụng APIClient để tạo ChatService
        chatService = prm392.project.factory.APIClient.getClient(this).create(ChatService.class);

        Button backButton = findViewById(R.id.back_button);
        Button clearChatButton = findViewById(R.id.clear_chat_button);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(ChatActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        clearChatButton.setOnClickListener(view -> {
            chatService.clearChat().enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        messagesContainer.removeAllViews();
                        Toast.makeText(ChatActivity.this, "Chat history cleared", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ChatActivity.this, "Failed to clear chat history", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(ChatActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Handle send button click
        sendButton.setOnClickListener(view -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
                messageInput.setText("");
            }
        });
    }

    private void sendMessage(String message) {
        ChatRequest chatRequest = new ChatRequest(message);
        addLoadingIndicator(); // Thêm hiệu ứng loading
        chatService.sendMessage(chatRequest).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                removeLoadingIndicator(); // Xóa hiệu ứng loading
                if (response.isSuccessful() && response.body() != null) {
                    addMessage("You", message);
                    addMessage("AI", response.body().getResponse());
                } else {
                    Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                removeLoadingIndicator(); // Xóa hiệu ứng loading
                Toast.makeText(ChatActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMessage(String sender, String message) {
        boolean isUser = "You".equals(sender);
        String displayName = isUser && currentUser != null ? currentUser.getUsername() : "Foodper AI";

        // Container ngang cho avatar + nội dung
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(0, 16, 0, 16);
        row.setLayoutParams(rowParams);
        row.setGravity(isUser ? android.view.Gravity.END : android.view.Gravity.START | android.view.Gravity.BOTTOM);

        // Avatar
        android.widget.ImageView avatar = new android.widget.ImageView(this);
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(72, 72);
        avatarParams.gravity = android.view.Gravity.BOTTOM;
        avatarParams.setMargins(isUser ? 16 : 0, 0, isUser ? 0 : 16, 0);
        avatar.setLayoutParams(avatarParams);
        avatar.setScaleType(android.widget.ImageView.ScaleType.CENTER_CROP);
        avatar.setImageResource(isUser ? R.drawable.ic_avatar_user : R.drawable.ic_avatar_robot);

        // Nội dung dọc (tên + bong bóng)
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams contentParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        contentLayout.setLayoutParams(contentParams);
        contentLayout.setGravity(isUser ? android.view.Gravity.END : android.view.Gravity.START);

        // Tên người gửi
        TextView senderView = new TextView(this);
        senderView.setText(displayName);
        senderView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        senderView.setTextSize(12);
        senderView.setPadding(8, 0, 8, 2);
        senderView.setTextAlignment(isUser ? TextView.TEXT_ALIGNMENT_VIEW_END : TextView.TEXT_ALIGNMENT_VIEW_START);
        contentLayout.addView(senderView);

        // Bong bóng tin nhắn
        TextView messageView = new TextView(this);
        messageView.setText(parseMarkdownBold(message));
        messageView.setTextColor(getResources().getColor(android.R.color.black));
        messageView.setTextSize(16);
        messageView.setPadding(0, 0, 0, 0);
        messageView.setLineSpacing(0, 1.2f);
        messageView.setMaxWidth((int)(getResources().getDisplayMetrics().widthPixels * 0.7));
        messageView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        bubbleParams.setMargins(8, 0, 8, 0);
        bubbleParams.gravity = isUser ? android.view.Gravity.END : android.view.Gravity.START;
        messageView.setBackgroundResource(isUser ? R.drawable.user_message_bg : R.drawable.ai_message_bg);
        messageView.setLayoutParams(bubbleParams);
        contentLayout.addView(messageView);

        if (isUser) {
            row.addView(contentLayout);
            row.addView(avatar);
        } else {
            row.addView(avatar);
            row.addView(contentLayout);
        }
        messagesContainer.addView(row);
    }

    // Parse markdown bold: **text** thành in đậm và loại bỏ dấu **
    private android.text.Spannable parseMarkdownBold(String text) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\*\\*(.+?)\\*\\*");
        java.util.regex.Matcher matcher = pattern.matcher(text);
        android.text.SpannableStringBuilder builder = new android.text.SpannableStringBuilder();
        int lastEnd = 0;
        while (matcher.find()) {
            // Thêm đoạn trước **
            builder.append(text.substring(lastEnd, matcher.start()));
            // Đoạn cần in đậm
            String boldText = matcher.group(1);
            int start = builder.length();
            builder.append(boldText);
            builder.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), start, start + boldText.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            lastEnd = matcher.end();
        }
        // Thêm phần còn lại
        builder.append(text.substring(lastEnd));
        return builder;
    }

    // Hiệu ứng loading động cho bot
    private android.os.Handler loadingHandler;
    private Runnable loadingRunnable;
    private TextView loadingIndicatorView;
    private int dotCount = 0;
    private void addLoadingIndicator() {
        removeLoadingIndicator();
        loadingIndicatorView = new TextView(this);
        loadingIndicatorView.setText("AI is typing");
        loadingIndicatorView.setTextColor(getResources().getColor(android.R.color.darker_gray));
        loadingIndicatorView.setTextSize(14);
        loadingIndicatorView.setTag("loadingIndicator");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(16, 8, 16, 8);
        loadingIndicatorView.setLayoutParams(params);
        messagesContainer.addView(loadingIndicatorView);
        // Animation 3 chấm
        loadingHandler = new android.os.Handler();
        loadingRunnable = new Runnable() {
            @Override
            public void run() {
                dotCount = (dotCount + 1) % 4;
                String dots = new String(new char[dotCount]).replace("\0", ".");
                loadingIndicatorView.setText("AI is typing" + dots);
                loadingHandler.postDelayed(this, 500);
            }
        };
        loadingHandler.post(loadingRunnable);
    }
    private void removeLoadingIndicator() {
        if (loadingHandler != null && loadingRunnable != null) {
            loadingHandler.removeCallbacks(loadingRunnable);
        }
        if (loadingIndicatorView != null) {
            messagesContainer.removeView(loadingIndicatorView);
            loadingIndicatorView = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Không còn socket để disconnect
    }

    private void loadUserProfile() {
        userRepository.getUserProfile().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        User user = response.body();
                        // Set user data to views
                        currentUser = user;
                        // Load chat history after getting the user profile
                        loadChatHistory();
                    } else {
                        Log.e("ChatActivity", "Response body is null");
                        Toast.makeText(ChatActivity.this, "No user profile available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("ChatActivity", "Error: " + response.code() + " - " + response.errorBody());
                    Toast.makeText(ChatActivity.this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadChatHistory() {
        chatService.getChatHistory().enqueue(new Callback<List<ChatHistoryModel>>() {
            @Override
            public void onResponse(Call<List<ChatHistoryModel>> call, Response<List<ChatHistoryModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ChatHistoryModel> chatList = response.body();
                    for (ChatHistoryModel chat : chatList) {
                        addMessage("You", chat.getMessage());
                        addMessage("AI", chat.getResponse());
                    }
                } else {
                    Toast.makeText(ChatActivity.this, "Failed to load chat history", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ChatHistoryModel>> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package prm392.project.view;

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
// ...existing code...
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

        // Sử dụng APIClient để tạo ChatService
        chatService = prm392.project.factory.APIClient.getClient(this).create(ChatService.class);

        Button backButton = findViewById(R.id.back_button);
        Button clearChatButton = findViewById(R.id.clear_chat_button);
        backButton.setOnClickListener(view -> {
            finish(); // This will close the current activity and return to the previous one
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
        chatService.sendMessage(chatRequest).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    addMessage("You: " + message);
                    addMessage("AI: " + response.body().getResponse());
                } else {
                    Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMessage(String message) {
        // Tách loại message: "You: ..." hoặc "AI: ..."
        boolean isUser = message.startsWith("You: ");
        String displayText = isUser ? message.replaceFirst("You: ", "") : message.replaceFirst("AI: ", "");

        LinearLayout bubble = new LinearLayout(this);
        bubble.setOrientation(LinearLayout.HORIZONTAL);
        bubble.setPadding(16, 8, 16, 8);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 8, 8, 8);
        if (isUser) {
            params.gravity = android.view.Gravity.END;
            bubble.setBackgroundResource(R.drawable.user_message_bg);
        } else {
            params.gravity = android.view.Gravity.START;
            bubble.setBackgroundResource(R.drawable.ai_message_bg);
        }
        bubble.setLayoutParams(params);

        TextView messageView = new TextView(this);
        messageView.setText(displayText);
        messageView.setTextColor(getResources().getColor(android.R.color.black));
        messageView.setTextSize(16);
        bubble.addView(messageView);
        messagesContainer.addView(bubble);
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
                        addMessage("You: " + chat.getMessage());
                        addMessage("AI: " + chat.getResponse());
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

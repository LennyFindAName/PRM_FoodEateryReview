package prm392.project.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.text.DecimalFormat;

import prm392.project.R;
import prm392.project.model.Blog;
import prm392.project.repo.BlogRepository;
import prm392.project.model.OrderDetail;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class BlogDetailActivity extends AppCompatActivity {
    private ImageView blogImage;
    private TextView blogName, blogDescription, blogPrice, blogCalories;
    private Button btnAddToCart;
    private BlogRepository blogRepository;
    Blog tmpBlog = new Blog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_blog_detail);

        // Initialize views
        blogImage = findViewById(R.id.blogImage);
        blogName = findViewById(R.id.blogName);
        blogDescription = findViewById(R.id.blogDescription);
        blogPrice = findViewById(R.id.blogPrice);
        blogCalories = findViewById(R.id.blogCalories);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        blogRepository = new BlogRepository(this);

        String blogId = getIntent().getStringExtra("blog_id");
        Log.e("dangdeptrai", "dangdeptrai blogid: " + blogId);
        if (blogId != null && !blogId.isEmpty()) {
            getblogDetails(blogId);
        } else {
            Toast.makeText(this, "Invalid blog ID", Toast.LENGTH_SHORT).show();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    Intent intent = new Intent(BlogDetailActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else if (item.getItemId() == R.id.nav_cart) {
                    Intent intent = new Intent(BlogDetailActivity.this, CartListActivity.class);
                    startActivity(intent);
                    finish();
                } else if (item.getItemId() == R.id.nav_profile) {
                    Intent intent = new Intent(BlogDetailActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else if (item.getItemId() == R.id.nav_location) {
                    Intent intent = new Intent(BlogDetailActivity.this, GoogleMapsActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });

    }

    private void getblogDetails(String blogId) {
        if (blogId == null || blogId.isEmpty()) {
            Log.e("blogDetailActivity", "Invalid blog ID");
            Toast.makeText(this, "Invalid blog ID", Toast.LENGTH_SHORT).show();
            return;
        }

        blogRepository.getBlogDetails(blogId).enqueue(new Callback<Blog>() {
            @Override
            public void onResponse(Call<Blog> call, Response<Blog> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Blog blog = response.body();
                        tmpBlog = blog;
                        // Set data to views
                        blogName.setText(blog.getBlogTitle());
                        blogDescription.setText(blog.getBlogContent());
                        blogPrice.setText(blog.getBlogDate());
                        blogCalories.setText("Lượt like: " + blog.getBlogLike());

                        if (blog.getFirstImage() != null && !blog.getFirstImage().isEmpty()) {
                            try {
                                byte[] decodedString = Base64.decode(blog.getFirstImage(), Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                                if (decodedByte != null) {
                                    blogImage.setImageBitmap(decodedByte);
                                } else {
                                    blogImage.setImageResource(R.drawable.salah); // fallback image
                                }
                            } catch (IllegalArgumentException e) {
                                blogImage.setImageResource(R.drawable.salah);
                            }
                        } else {
                            blogImage.setImageResource(R.drawable.salah);
                        }

                    } else {
                        // Log the response body if it's null
                        Log.e("blogDetailActivity", "Response body is null");
                        Toast.makeText(BlogDetailActivity.this, "No blog details available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Log the HTTP status code and error body
                    Log.e("blogDetailActivity", "Error: " + response.code() + " - " + response.errorBody());
                    Toast.makeText(BlogDetailActivity.this, "Failed to load blog details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Blog> call, Throwable t) {
                Toast.makeText(BlogDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
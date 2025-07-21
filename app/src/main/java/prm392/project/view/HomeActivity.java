package prm392.project.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.AutoCompleteTextView;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import prm392.project.R;
import prm392.project.adapter.BlogAdapter;
import prm392.project.adapter.FoodAdapter;
import prm392.project.inter.OnCartUpdateListener;
import prm392.project.model.DTOs.BlogPagedResponse;
import prm392.project.repo.BlogRepository;
import prm392.project.inter.FoodService;
import prm392.project.inter.BlogService;

import prm392.project.model.Food;
import prm392.project.model.Blog;
import prm392.project.repo.UserRepository;
import prm392.project.utils.BottomNavHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements OnCartUpdateListener {

    GridView gridView;
    //FoodAdapter foodAdapter;

    BlogAdapter blogAdapter;
    //ArrayList<Food> foodList;

    ArrayList<Blog> blogList;
    ArrayList<Blog> allBlogList; // Store all blogs for filtering
    SwipeRefreshLayout swipeRefreshLayout;
    EditText searchEditText;
    AutoCompleteTextView filterFoodType, filterMealType, filterPriceRange;
    //FoodService foodService;
    BlogService blogService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HomeActivity", "onCreate: Activity is being created");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        blogList =  new ArrayList<>(); // Initialize the blogList
        allBlogList = new ArrayList<>(); // Initialize the allBlogList
        //foodList = new ArrayList<>(); // Initialize the foodList
        Log.d("HomeActivity", "Food list initialized");

        //foodService = FoodRepository.getFoodService(this); // Initialize foodService
        //foodAdapter = new FoodAdapter(this, foodList, this);

        //For blogs
        blogService = BlogRepository.getBlogService(this); // Initialize blogService
        blogAdapter  = new BlogAdapter(this, blogList);

        gridView = findViewById(R.id.foodListView);
        gridView.setAdapter(blogAdapter);

        Log.d("HomeActivity", "Food adapter set for GridView");


        ImageView menuIcon = findViewById(R.id.menu_icon);
        menuIcon.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(HomeActivity.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.option_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.logout) {
                        Log.d("HomeActivity", "Logout menu item clicked");
                        // Xóa token và chuyển về MainActivity
                        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.remove("access_token");
                        editor.apply();
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (item.getItemId() == R.id.orderHistory) {
                        Intent intent = new Intent(HomeActivity.this, OrderHistoryActivity.class);
                        startActivity(intent);
                    }
                    else if (item.getItemId() == R.id.chat){
                        // Mở ChatActivity, đổi tên hiển thị là Chat with AI
                        Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                        intent.putExtra("chatType", "AI");
                        startActivity(intent);
                    }
                    return false;
                }
            });
            popupMenu.show();
        });

        // Add notification icon click handler
        ImageView notificationIcon = findViewById(R.id.notification_icon);
        notificationIcon.setOnClickListener(v -> {
            Log.d("HomeActivity", "Notification icon clicked");
            Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        // Initialize search EditText and set up search functionality
        searchEditText = findViewById(R.id.search_edit_text);
        setupSearchFunctionality();

        // Initialize filter dropdowns
        filterFoodType = findViewById(R.id.filter_food_type);
        filterMealType = findViewById(R.id.filter_meal_type);
        filterPriceRange = findViewById(R.id.filter_price_range);
        setupFilterDropdowns();

        loadBlogData();
        Log.d("HomeActivity", "Food data loading started");

        // Handle pull-to-refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d("HomeActivity", "Pull-to-refresh triggered");
            refreshBlogData();  // Your method to refresh data
            swipeRefreshLayout.setRefreshing(false);  // Stop the refresh animation
            Log.d("HomeActivity", "Pull-to-refresh completed");
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavHelper.setup(this, bottomNavigationView, R.id.nav_home);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // if (!prm392.project.utils.JwtUtils.isTokenValid(this)) {
        //     Intent intent = new Intent(this, LoginActivity.class);
        //     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //     startActivity(intent);
        //     finish();
        // }
    }

    @Override
    public void onCartUpdated(int itemCount) {
        // CartSize functionality removed - no longer needed
    }

    // Method to load the initial data
    private void loadBlogData() {
        updateCartCountAtHome();
        Log.d("HomeActivity", "Loading food data...");
        //Call<List<Food>> call = foodService.getFoodList(1, 99999, "");
        Call<BlogPagedResponse> blogCall = blogService.getPagedBlogs (1, 99999);

        blogCall.enqueue(new Callback<BlogPagedResponse>() {
            @Override
            public void onResponse(Call<BlogPagedResponse> call, Response<BlogPagedResponse> response) {
                Log.d("HomeActivity", "Response received from blog service");
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("HomeActivity", "Blog data successfully loaded");
                    blogList.clear();
                    blogList.addAll(response.body().blogs);
                    allBlogList.clear(); // Clear the allBlogList
                    allBlogList.addAll(response.body().blogs); // Add all blogs to allBlogList
                    blogAdapter.notifyDataSetChanged();
                } else {
                    Log.d("HomeActivity", "Failed to load blog data: " + response.code());
                    Toast.makeText(HomeActivity.this, "Failed to load blogs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BlogPagedResponse> call, Throwable t) {
                Log.e("HomeActivity", "API error: " + t.getMessage());
                if (t instanceof java.net.SocketTimeoutException) {
                    Toast.makeText(HomeActivity.this, "Request timed out. Please try again.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        /* call.enqueue(new Callback<List<Food>>() {
            @Override
            public void onResponse(Call<List<Food>> call, Response<List<Food>> response) {
                Log.d("HomeActivity", "Response received from food service");
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("HomeActivity", "Food data successfully loaded");
                    foodList.clear();
                    foodList.addAll(response.body());
                    foodAdapter.notifyDataSetChanged();
                } else {
                    Log.d("HomeActivity", "Failed to load food data: " + response.code());
                    Toast.makeText(HomeActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Food>> call, Throwable t) {
                Log.e("HomeActivity", "API error: " + t.getMessage());
                if (t instanceof java.net.SocketTimeoutException) {
                    Toast.makeText(HomeActivity.this, "Request timed out. Please try again.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });*/




    }

    // Method to refresh data
    private void refreshBlogData() {
        Log.d("HomeActivity", "Refreshing food data...");
        //foodList.clear();  // Clear the existing list
        blogList.clear();  // Clear the existing blog list

        // Reset search bar and all filter dropdowns
        searchEditText.setText("");
        filterFoodType.setText("All", false);
        filterMealType.setText("All", false);
        filterPriceRange.setText("All", false);

        loadBlogData();    // Reload the data
       // foodAdapter.notifyDataSetChanged();  // Notify adapter of the data change
        blogAdapter.notifyDataSetChanged();  // Notify blog adapter of the data change

        Log.d("HomeActivity", "Food data refreshed and all filters reset");
    }


    private void updateCartCountAtHome() {
        // CartSize functionality removed - no longer needed
    }

    // Setup search functionality with TextWatcher
    private void setupSearchFunctionality() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Apply all filters including search as user types
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed for this implementation
            }
        });
    }

    // Filter blogs based on search query
    private void filterBlogs(String searchQuery) {
        Log.d("HomeActivity", "Filtering blogs with query: " + searchQuery);

        blogList.clear(); // Clear current displayed list

        if (searchQuery.isEmpty()) {
            // If search query is empty, show all blogs
            blogList.addAll(allBlogList);
        } else {
            // Filter blogs based on blog title (case-insensitive)
            for (Blog blog : allBlogList) {
                if (blog.getBlogTitle() != null &&
                    blog.getBlogTitle().toLowerCase().contains(searchQuery.toLowerCase())) {
                    blogList.add(blog);
                }
            }
        }

        // Notify adapter of data changes
        blogAdapter.notifyDataSetChanged();

        Log.d("HomeActivity", "Filtered results: " + blogList.size() + " blogs found");
    }

    // Setup filter dropdowns
    private void setupFilterDropdowns() {
        // Setup for food type filter with "All" option
        String[] mealTypeOptions = {"All", "Breakfast", "Brunch", "Lunch", "Dinner", "Late Night", "Drink"};
        String[] foodTypeOptions = {"All", "Vietnamese", "Chinese", "Korean", "American", "Europe"};

        ArrayAdapter<String> foodTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, foodTypeOptions);
        filterFoodType.setAdapter(foodTypeAdapter);

        // Setup for meal type filter with "All" option

        ArrayAdapter<String> mealTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mealTypeOptions);
        filterMealType.setAdapter(mealTypeAdapter);

        // Setup for price range filter with "All" option
        String[] priceRangeOptions = {"All", "$ (<50,000 vnd)", "$$ (50,001 - 200,000 vnd)", "$$$ (>200,000 vnd)"};
        ArrayAdapter<String> priceRangeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, priceRangeOptions);
        filterPriceRange.setAdapter(priceRangeAdapter);

        // Set up item click listeners for dropdowns
        filterFoodType.setOnItemClickListener((parent, view, position, id) -> {
            String selectedFoodType = (String) parent.getItemAtPosition(position);
            Log.d("HomeActivity", "Selected food type: " + selectedFoodType);
            applyFilters();
        });

        filterMealType.setOnItemClickListener((parent, view, position, id) -> {
            String selectedMealType = (String) parent.getItemAtPosition(position);
            Log.d("HomeActivity", "Selected meal type: " + selectedMealType);
            applyFilters();
        });

        filterPriceRange.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPriceRange = (String) parent.getItemAtPosition(position);
            Log.d("HomeActivity", "Selected price range: " + selectedPriceRange);
            applyFilters();
        });

        // Make dropdowns show dropdown on click
        filterFoodType.setOnClickListener(v -> filterFoodType.showDropDown());
        filterMealType.setOnClickListener(v -> filterMealType.showDropDown());
        filterPriceRange.setOnClickListener(v -> filterPriceRange.showDropDown());
    }

    // Apply all filters (search + dropdowns)
    private void applyFilters() {
        String searchQuery = searchEditText.getText().toString().trim();
        String selectedFoodType = filterFoodType.getText().toString();
        String selectedMealType = filterMealType.getText().toString();
        String selectedPriceRange = filterPriceRange.getText().toString();

        Log.d("HomeActivity", "Applying filters - Search: " + searchQuery +
              ", FoodType: " + selectedFoodType +
              ", MealType: " + selectedMealType +
              ", PriceRange: " + selectedPriceRange);

        blogList.clear(); // Clear current displayed list

        for (Blog blog : allBlogList) {
            boolean matchesSearch = true;
            boolean matchesFoodType = true;
            boolean matchesMealType = true;
            boolean matchesPriceRange = true;

            // Check search query
            if (!searchQuery.isEmpty()) {
                matchesSearch = blog.getBlogTitle() != null &&
                               blog.getBlogTitle().toLowerCase().contains(searchQuery.toLowerCase());
            }

            // Check food type filter with debug logging
            if (!selectedFoodType.isEmpty() && !selectedFoodType.equals("All")) {
                String foodTypeName = blog.getFoodTypeName();
                Log.d("HomeActivity", "Blog "
                        + blog.getBlogId() + " food type: "
                        + foodTypeName +
                      ", looking for: " + selectedFoodType);
                matchesFoodType = foodTypeName != null && foodTypeName.equals(selectedFoodType);
                Log.d("HomeActivity", "Food type match result: " + matchesFoodType);
            }

            // Check meal type filter
            if (!selectedMealType.isEmpty() && !selectedMealType.equals("All")) {
                String mealTypeName = blog.getMealTypeName();
                Log.d("HomeActivity", "Blog " + blog.getBlogId() + " meal type: " + mealTypeName +
                      ", looking for: " + selectedMealType);
                matchesMealType = mealTypeName != null && mealTypeName.equals(selectedMealType);
                Log.d("HomeActivity", "Meal type match result: " + matchesMealType);
            }

            // Check price range filter
            if (!selectedPriceRange.isEmpty() && !selectedPriceRange.equals("All")) {
                String priceRangeValue = blog.getPriceRangeValue();
                Log.d("HomeActivity", "Blog " + blog.getBlogId() + " price range: " + priceRangeValue +
                      ", looking for: " + selectedPriceRange);
                matchesPriceRange = priceRangeValue != null && priceRangeValue.equals(selectedPriceRange);
                Log.d("HomeActivity", "Price range match result: " + matchesPriceRange);
            }

            // Add blog if it matches all filters
            if (matchesSearch && matchesFoodType && matchesMealType && matchesPriceRange) {
                blogList.add(blog);
            }
        }

        // Notify adapter of data changes
        blogAdapter.notifyDataSetChanged();

        Log.d("HomeActivity", "Filtered results: " + blogList.size() + " blogs found");
    }
}

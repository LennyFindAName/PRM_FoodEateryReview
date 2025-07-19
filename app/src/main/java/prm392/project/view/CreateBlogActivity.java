// Java: app/src/main/java/prm392/project/view/CreateBlogActivity.java
package prm392.project.view;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import prm392.project.R;
import prm392.project.inter.UserService;
import prm392.project.model.DTOs.BlogRequest;
import prm392.project.model.User;
import prm392.project.repo.BlogRepository;
import prm392.project.repo.UserRepository;
import prm392.project.utils.BottomNavHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.ImageButton;

public class CreateBlogActivity extends AppCompatActivity {
    private EditText titleEditText, contentEditText, dateEditText, eateryAddressEditText, eateryLocationEditText,
            foodQualityEditText, environmentEditText, serviceEditText, pricingEditText, hygieneEditText, overallEditText;
    private AutoCompleteTextView foodTypesEditText, mealTypesEditText, priceRangeEditText;
    private Button submitButton;
    private BlogRepository blogRepository;
    private static final int PICK_BILL_IMAGE = 1;
    private static final int PICK_BLOG_IMAGE = 2;
    private String billImageBase64 = "";
    private ArrayList<String> blogImagesBase64 = new ArrayList<>();
    private ImageView imageViewBill;
    private LinearLayout layoutBlogImages;
    private ImageButton buttonSelectBillImage;
    private ImageButton buttonSelectBlogImages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_blog);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        BottomNavHelper.setup(this, bottomNavigationView, R.id.nav_create_blog);

        titleEditText = findViewById(R.id.editTextTitle);
        contentEditText = findViewById(R.id.editTextContent);
        eateryAddressEditText = findViewById(R.id.editTextEateryAddress);
        eateryLocationEditText = findViewById(R.id.editTextEateryLocation);
        foodQualityEditText = findViewById(R.id.editTextFoodQuality);
        environmentEditText = findViewById(R.id.editTextEnvironment);
        serviceEditText = findViewById(R.id.editTextService);
        pricingEditText = findViewById(R.id.editTextPricing);
        hygieneEditText = findViewById(R.id.editTextHygiene);
        overallEditText = findViewById(R.id.editTextOverall);
        foodTypesEditText = findViewById(R.id.editTextFoodTypes);
        mealTypesEditText = findViewById(R.id.editTextMealTypes);
        priceRangeEditText = findViewById(R.id.editTextPriceRange);
        submitButton = findViewById(R.id.buttonSubmit);

        buttonSelectBillImage = findViewById(R.id.buttonSelectBillImage);
        buttonSelectBlogImages = findViewById(R.id.buttonSelectBlogImages);
        imageViewBill = findViewById(R.id.imageViewBill);
        layoutBlogImages = findViewById(R.id.layoutBlogImages);

        // Set up TextWatchers for automatic average calculation
        setupRatingCalculation();

        // Set up dropdown options
        setupDropdownOptions();

        buttonSelectBillImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_BILL_IMAGE);
        });

        buttonSelectBlogImages.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, PICK_BLOG_IMAGE);
        });

        submitButton.setOnClickListener(v -> {
            // Disable button and show loading message
            submitButton.setEnabled(false);
            submitButton.setText("Bài viết của bạn đang được tạo ....");

            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    BlogRequest blogRequest = new BlogRequest();
                    BlogRepository blogRepository = new BlogRepository(this);

                    if (titleEditText.getText().toString().trim().length() > 100) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Title must be 100 characters or less", Toast.LENGTH_SHORT).show();
                            // Reset button state on error
                            submitButton.setEnabled(true);
                            submitButton.setText("Đăng bài");
                        });
                        return;
                    }

                    blogRequest.setBlogTitle(titleEditText.getText().toString().trim());
                    blogRequest.setBlogContent(contentEditText.getText().toString().trim());

                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String formattedDate = outputFormat.format(Calendar.getInstance().getTime());
                    blogRequest.setBlogDate(formattedDate);

                    blogRequest.setEateryAddressDetail(eateryAddressEditText.getText().toString().trim());
                    blogRequest.setEateryLocationDetail(eateryLocationEditText.getText().toString().trim());
                    blogRequest.setFoodQualityRate(parseInt(foodQualityEditText.getText().toString()));
                    blogRequest.setEnvironmentRate(parseInt(environmentEditText.getText().toString()));
                    blogRequest.setServiceRate(parseInt(serviceEditText.getText().toString()));
                    blogRequest.setPricingRate(parseInt(pricingEditText.getText().toString()));
                    blogRequest.setHygieneRate(parseInt(hygieneEditText.getText().toString()));
                    blogRequest.setBlogRate(parseDouble(overallEditText.getText().toString()));

                    // Set single-element lists for FoodTypes, MealTypes, and PriceRanges
                    ArrayList<String> foodTypesList = new ArrayList<>();
                    String foodType = foodTypesEditText.getText().toString().trim();
                    if (!foodType.isEmpty()) {
                        foodTypesList.add(foodType);
                    }
                    blogRequest.setFoodTypeNames(foodTypesList);

                    ArrayList<String> mealTypesList = new ArrayList<>();
                    String mealType = mealTypesEditText.getText().toString().trim();
                    if (!mealType.isEmpty()) {
                        mealTypesList.add(mealType);
                    }
                    blogRequest.setMealTypeNames(mealTypesList);

                    ArrayList<String> priceRangesList = new ArrayList<>();
                    String priceRange = priceRangeEditText.getText().toString().trim();
                    if (!priceRange.isEmpty()) {
                        priceRangesList.add(priceRange);
                    }
                    blogRequest.setPriceRanges(priceRangesList);

                    blogRequest.setBlogBillImageBase64(billImageBase64);
                    blogRequest.setBlogImagesBase64(blogImagesBase64);

                    blogRequest.setBlogLike(0);
                    blogRequest.setBlogStatus(0);
                    blogRequest.setLikeCount(0);
                    blogRequest.setHasLiked(false);

                    UserRepository userRepository = new UserRepository(this);
                    Response<User> userResponse = userRepository.getUserProfile().execute();
                    if (!userResponse.isSuccessful() || userResponse.body() == null) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Failed to get user", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                        return;
                    }

                    // Temporarily using a mock user ID
                    String userId = userResponse.body().getUserID();
                    blogRequest.setUserId(userId);

                    if (blogRequest.getUserId() == null ||
                            blogRequest.getBlogTitle().isEmpty() ||
                            blogRequest.getBlogContent().isEmpty() ||
                            blogRequest.getBlogBillImageBase64().isEmpty()){
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
                            // Reset button state on error
                            submitButton.setEnabled(true);
                            submitButton.setText("Đăng bài");
                        });
                        return;
                    }

                    Gson gson = new Gson();
                    String jsonPayload = gson.toJson(blogRequest);
                    System.out.println("BlogRequest JSON payload: " + jsonPayload);
                    android.util.Log.d("CreateBlogActivity", "BlogRequest JSON payload: " + jsonPayload);

                    Response<Void> blogResponse = blogRepository.createBlog(blogRequest).execute();
                    runOnUiThread(() -> {
                        if (blogResponse.isSuccessful()) {
                            Toast.makeText(this, "Blog created!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Failed to create blog", Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        // Reset button state on error
                        submitButton.setEnabled(true);
                        submitButton.setText("Đăng bài");
                    });
                }
            });
        });
    }

    private void setupDropdownOptions() {
        // Food Types dropdown options
        String[] foodTypeOptions = {"Vietnamese", "Chinese", "Korean", "American", "Europe"};
        ArrayAdapter<String> foodTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, foodTypeOptions);
        foodTypesEditText.setAdapter(foodTypeAdapter);
        foodTypesEditText.setOnClickListener(v -> foodTypesEditText.showDropDown());

        // Meal Types dropdown options
        String[] mealTypeOptions = {"Breakfast", "Brunch", "Lunch", "Dinner", "Late Night", "Drink"};
        ArrayAdapter<String> mealTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mealTypeOptions);
        mealTypesEditText.setAdapter(mealTypeAdapter);
        mealTypesEditText.setOnClickListener(v -> mealTypesEditText.showDropDown());

        // Price Range dropdown options
        String[] priceRangeOptions = {"$ (<50,000 vnd)", "$$ (50,001 - 200,000 vnd)", "$$$ (>200,000 vnd)"};
        ArrayAdapter<String> priceRangeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, priceRangeOptions);
        priceRangeEditText.setAdapter(priceRangeAdapter);
        priceRangeEditText.setOnClickListener(v -> priceRangeEditText.showDropDown());

        //
    }

    private void setupRatingCalculation() {
        TextWatcher ratingWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                calculateOverallRating();
            }
        };

        foodQualityEditText.addTextChangedListener(ratingWatcher);
        environmentEditText.addTextChangedListener(ratingWatcher);
        serviceEditText.addTextChangedListener(ratingWatcher);
        pricingEditText.addTextChangedListener(ratingWatcher);
        hygieneEditText.addTextChangedListener(ratingWatcher);
    }

    private void calculateOverallRating() {
        try {
            double total = 0;
            int count = 0;

            String foodQuality = foodQualityEditText.getText().toString().trim();
            if (!foodQuality.isEmpty()) {
                total += Double.parseDouble(foodQuality);
                count++;
            }

            String environment = environmentEditText.getText().toString().trim();
            if (!environment.isEmpty()) {
                total += Double.parseDouble(environment);
                count++;
            }

            String service = serviceEditText.getText().toString().trim();
            if (!service.isEmpty()) {
                total += Double.parseDouble(service);
                count++;
            }

            String pricing = pricingEditText.getText().toString().trim();
            if (!pricing.isEmpty()) {
                total += Double.parseDouble(pricing);
                count++;
            }

            String hygiene = hygieneEditText.getText().toString().trim();
            if (!hygiene.isEmpty()) {
                total += Double.parseDouble(hygiene);
                count++;
            }

            if (count > 0) {
                double average = total / count;
                // Round to 1 decimal place
                average = Math.round(average * 10.0) / 10.0;
                overallEditText.setText(String.valueOf(average));
            } else {
                overallEditText.setText("");
            }
        } catch (NumberFormatException e) {
            // Handle invalid number format
            overallEditText.setText("");
        }
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private ArrayList<String> splitList(String s) {
        if (s.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(s.split(",")));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_BILL_IMAGE && data.getData() != null) {
                Uri uri = data.getData();
                billImageBase64 = encodeImageToBase64(uri);
                imageViewBill.setImageURI(uri);
            } else if (requestCode == PICK_BLOG_IMAGE) {
                blogImagesBase64.clear();
                layoutBlogImages.removeAllViews();
                if (data.getClipData() != null) {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        blogImagesBase64.add(encodeImageToBase64(uri));
                        addImageToLayout(uri);
                    }
                } else if (data.getData() != null) {
                    Uri uri = data.getData();
                    blogImagesBase64.add(encodeImageToBase64(uri));
                    addImageToLayout(uri);
                }
            }
        }
    }

    private void addImageToLayout(Uri uri) {
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageURI(uri);
        layoutBlogImages.addView(imageView);
    }

    private String encodeImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            return android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
        } catch (Exception e) {
            return "";
        }
    }
}

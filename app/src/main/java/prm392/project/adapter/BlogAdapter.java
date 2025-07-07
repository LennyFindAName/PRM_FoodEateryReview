package prm392.project.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import prm392.project.R;
import prm392.project.model.Blog;
import prm392.project.view.FoodDetailActivity;

public class BlogAdapter extends BaseAdapter {
    private Context context;
    private List<Blog> blogList;


    public BlogAdapter(Context context, List<Blog> blogList) {
        this.context = context;
        this.blogList = blogList;
    }

    @Override
    public int getCount() {
        return blogList.size();
    }

    @Override
    public Object getItem(int i) {
        return blogList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.custom_food_card, viewGroup, false);
        }
        Blog blog = blogList.get(i);

        ImageView imageView = view.findViewById(R.id.foodImage);
        TextView nameView = view.findViewById(R.id.foodName);
        TextView priceView = view.findViewById(R.id.foodPrice);
        TextView descriptionView = view.findViewById(R.id.description);
        TextView calorieView = view.findViewById(R.id.foodCalorie);
        ImageButton addButton = view.findViewById(R.id.btnAddToCart);

        // Set blog data to the UI elements
        nameView.setText(blog.getBlogTitle() != null ? blog.getBlogTitle() : "No Title");

        // Format blog likes as "price" (since we're reusing food layout)
        String likesText = blog.getBlogLike() != null ? blog.getBlogLike() + " likes" : "0 likes";
        priceView.setText(likesText);

        // Show username in description field
        descriptionView.setText("By: " + (blog.getUsername() != null ? blog.getUsername() : "Unknown"));

        // Format date in "calorie" field using SimpleDateFormat (API level 24 compatible)
        String dateText = "No Date";
        if (blog.getBlogDate() != null && !blog.getBlogDate().isEmpty()) {
            try {
                // Parse the input date format "2025-07-01T00:00:00"
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

                Date date = inputFormat.parse(blog.getBlogDate());
                if (date != null) {
                    dateText = outputFormat.format(date);
                }
            } catch (ParseException e) {
                dateText = "Invalid Date";
            }
        }
        calorieView.setText(dateText);

        // Set default image (since blogs don't have images in current JSON)
        imageView.setImageResource(R.drawable.salah);

        // Handle click events
        imageView.setOnClickListener(v -> {
            // TODO: Create BlogDetailActivity instead of using FoodDetailActivity
            Intent intent = new Intent(context, FoodDetailActivity.class);
            intent.putExtra("blog_id", blog.getBlogId());
            context.startActivity(intent);
        });

        // Hide or modify the add button for blogs
        addButton.setVisibility(View.GONE); // Hide since it's not relevant for blogs

        return view;
    }
}

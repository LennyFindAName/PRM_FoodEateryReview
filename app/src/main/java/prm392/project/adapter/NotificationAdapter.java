package prm392.project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import prm392.project.R;
import prm392.project.model.Notification;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<Notification> notifications;
    private OnNotificationDeleteListener deleteListener;

    public interface OnNotificationDeleteListener {
        void onNotificationDelete(Notification notification);
    }

    public NotificationAdapter(List<Notification> notifications, OnNotificationDeleteListener deleteListener) {
        this.notifications = notifications;
        this.deleteListener = deleteListener;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.bind(notification, deleteListener);
    }

    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0;
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView messageTextView;
        private TextView timestampTextView;
        private View readIndicator;
        private ImageView deleteButton;
        private ImageView notificationBell;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewNotificationTitle);
            messageTextView = itemView.findViewById(R.id.textViewNotificationMessage);
            timestampTextView = itemView.findViewById(R.id.textViewNotificationTimestamp);
            readIndicator = itemView.findViewById(R.id.viewReadIndicator);
            deleteButton = itemView.findViewById(R.id.imageViewDeleteNotification);
            notificationBell = itemView.findViewById(R.id.imageViewNotificationBell);
        }

        public void bind(Notification notification, OnNotificationDeleteListener deleteListener) {
            titleTextView.setText(notification.getTitle());
            messageTextView.setText(notification.getMessage());

            // Set notification bell color based on title
            if ("Thất bại".equals(notification.getTitle())) {
                notificationBell.setColorFilter(0xFFFF0000); // Red color for failure
            } else {
                notificationBell.clearColorFilter(); // Default color for success or other titles
            }

            // Format and display timestamp
            String formattedTimestamp = formatTimestamp(notification.getTimestamp());
            timestampTextView.setText(formattedTimestamp);

            // Log the notification ID for debugging
            android.util.Log.d("NotificationAdapter", "Binding notification with ID: " + notification.getId() +
                              ", Title: " + notification.getTitle());

            // Show/hide read indicator based on IsRead status
            if (notification.getIsRead()) {
                readIndicator.setVisibility(View.GONE);
            } else {
                readIndicator.setVisibility(View.VISIBLE);
            }

            // Set up delete button click listener
            deleteButton.setOnClickListener(v -> {
                android.util.Log.d("NotificationAdapter", "Delete button clicked for notification ID: " + notification.getId());
                if (deleteListener != null) {
                    deleteListener.onNotificationDelete(notification);
                }
            });
        }

        private String formatTimestamp(String timestamp) {
            if (timestamp == null || timestamp.isEmpty()) {
                return "";
            }

            try {
                // Parse the ISO timestamp format: "2025-07-17T07:53:14.4646409"
                // Extract date and time parts
                String[] parts = timestamp.split("T");
                if (parts.length >= 2) {
                    String datePart = parts[0]; // "2025-07-17"
                    String timePart = parts[1].split("\\.")[0]; // "07:53:14" (remove milliseconds)

                    // Format as "Jul 17, 2025 07:53"
                    String[] dateComponents = datePart.split("-");
                    if (dateComponents.length == 3) {
                        String year = dateComponents[0];
                        String month = getMonthName(dateComponents[1]);
                        String day = dateComponents[2];
                        String time = timePart.substring(0, 5); // "07:53" (remove seconds)

                        return month + " " + day + ", " + year + " " + time;
                    }
                }

                // Fallback: return original timestamp
                return timestamp;

            } catch (Exception e) {
                // If parsing fails, return original timestamp
                return timestamp;
            }
        }

        private String getMonthName(String monthNumber) {
            switch (monthNumber) {
                case "01": return "Jan";
                case "02": return "Feb";
                case "03": return "Mar";
                case "04": return "Apr";
                case "05": return "May";
                case "06": return "Jun";
                case "07": return "Jul";
                case "08": return "Aug";
                case "09": return "Sep";
                case "10": return "Oct";
                case "11": return "Nov";
                case "12": return "Dec";
                default: return monthNumber;
            }
        }
    }
}

package tranhoang202204.gmail.com.newsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NewsViewAdapter extends RecyclerView.Adapter<NewsViewHolder> {
    private LayoutInflater mInflater;
    private List<News> newsList;
    private MainActivity mainActivity;

    private FirebaseHelper firebaseHelper;

    private final Set<Integer> animatedPositions = new HashSet<>();

    public NewsViewAdapter(MainActivity mainActivity, Context context, List<News> newsList) {
        this.mInflater = LayoutInflater.from(context);
        this.newsList = newsList;
        this.mainActivity = mainActivity;
        this.firebaseHelper = new FirebaseHelper();
    }

    public void update(List<News> newsList){
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View customView = mInflater.inflate(R.layout.new_layout, parent, false);
        return new NewsViewHolder(customView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News currentNews = newsList.get(position);

        holder.getTxtTitle().setText(currentNews.getTitle());
        holder.getTxtDescription().setText(currentNews.getDescription());
        holder.getTxtTag().setText(new Category().getCategoryForTag(currentNews.getTag()));
        String dateDiff = TimeDifference.getTimeDifference(currentNews.getDate());
        holder.getTxtDate().setText(dateDiff);
        //holder.getTxtComment().setText(currentNews.getComment());
        if ("true".equals(currentNews.getBookmarked())) {
            holder.getImvBookmark().setImageResource(R.drawable.bookmark);
        } else {
            holder.getImvBookmark().setImageResource(R.drawable.bookmark_non_filled);
        }
        Picasso.get().load(currentNews.getImageUrl()).into(holder.getImageView());

        holder.itemView.clearAnimation(); // Xóa animation cũ trước khi áp dụng mới
        if (!animatedPositions.contains(position)) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.anim_recyclerviewnews);
            holder.itemView.startAnimation(animation);
            animatedPositions.add(position);
        }

        holder.itemView.setOnClickListener(v -> {
            if (!NetworkUtils.isNetworkAvailable(mInflater.getContext())){
                Toast.makeText(mInflater.getContext(), "Internet disabled to read detail news", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy thông tin người dùng
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();

                // Lưu vào lịch sử
                firebaseHelper.addToHistory(currentNews); // Lưu tin tức vào lịch sử
            }

            // Lấy context từ holder.itemView
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(mainActivity, DetailActivity.class);
            intent.putExtra("link", currentNews.getLink());
            mainActivity.startActivity(intent);
            mainActivity.overridePendingTransition(R.anim.anim_in_activity, R.anim.anim_out_activity);
        });

        holder.getImvBookmark().setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser == null) {
                // Hiển thị AlertDialog yêu cầu đăng nhập
                new AlertDialog.Builder(mainActivity)
                        .setTitle("Chưa đăng nhập")
                        .setMessage("Bạn cần đăng nhập để sử dụng tính năng này")
                        .setPositiveButton("Đăng nhập", (dialog, which) -> {
                            // Chuyển hướng đến LoginActivity
                            Intent loginIntent = new Intent(mainActivity, LoginActivity.class);
                            mainActivity.startActivity(loginIntent);
                        })
                        .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                String userId = currentUser.getUid(); // Lấy UID người dùng đăng nhập

                if ("true".equals(currentNews.getBookmarked())) {
                    // Nếu đã bookmark, thì xóa
                    firebaseHelper.removeBookmark(currentNews.getId());
                    holder.getImvBookmark().setImageResource(R.drawable.bookmark_non_filled);
                    Toast.makeText(mInflater.getContext(), "Đã xóa khỏi bookmark", Toast.LENGTH_SHORT).show();
                    currentNews.setBookmarked("false");
                } else {
                    // Nếu chưa bookmark, thì thêm
                    firebaseHelper.addBookmark(currentNews);
                    holder.getImvBookmark().setImageResource(R.drawable.bookmark);
                    Toast.makeText(mInflater.getContext(), "Đã thêm vào bookmark", Toast.LENGTH_SHORT).show();
                    currentNews.setBookmarked("true");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }
}

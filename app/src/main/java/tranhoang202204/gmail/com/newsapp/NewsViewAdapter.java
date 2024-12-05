package tranhoang202204.gmail.com.newsapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class NewsViewAdapter extends RecyclerView.Adapter<NewsViewHolder> {
    private LayoutInflater mInflater;
    private List<News> newsList;
    private MainActivity mainActivity;

    public NewsViewAdapter(MainActivity mainActivity, Context context, List<News> newsList) {
        this.mInflater = LayoutInflater.from(context);
        this.newsList = newsList;
        this.mainActivity = mainActivity;
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
        holder.getTxtDate().setText(currentNews.getDate());
        //holder.getTxtComment().setText(currentNews.getComment());
        if ("true".equals(currentNews.getBookmarked())) {
            holder.getImvBookmark().setImageResource(R.drawable.bookmark);
        } else {
            holder.getImvBookmark().setImageResource(R.drawable.bookmark_non_filled);
        }
        Picasso.get().load(currentNews.getImageUrl()).into(holder.getImageView());

        holder.itemView.setOnClickListener(v -> {
            // Lấy context từ holder.itemView
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(mainActivity, DetailActivity.class);
            intent.putExtra("link", currentNews.getLink());
            mainActivity.startActivity(intent);
        });

        holder.getImvBookmark().setOnClickListener(v -> {
            String currentBookmarkStatus = currentNews.getBookmarked();
            String newBookmarkStatus = currentNews.toggleBookmark(currentBookmarkStatus);

            new FirebaseHelper().updateBookmark(currentNews.getTitle(), newBookmarkStatus);


            // Thay đổi hình ảnh bookmark theo trạng thái mới
            if ("true".equals(newBookmarkStatus)) {
                holder.getImvBookmark().setImageResource(R.drawable.bookmark);
                Toast.makeText(mInflater.getContext(), "Da them vao bookmark: ", Toast.LENGTH_SHORT).show();
            } else {
                holder.getImvBookmark().setImageResource(R.drawable.bookmark_non_filled);
                Toast.makeText(mInflater.getContext(), "Da xoa khoi bookmark: ", Toast.LENGTH_SHORT).show();
            }

            // Kiểm tra trạng thái Bottom Navigation
//            if ("bookmark".equals(mainActivity.bottomNavigationTab)) {
//                // Cập nhật danh sách và adapter nếu đang ở tab bookmark
//                new FirebaseHelper().getBookmarkedNews(newsList, mainActivity.newsAdapter); // Hàm cập nhật danh sách từ Firebase
//                Toast.makeText(mainActivity, "on bookmark!!!", Toast.LENGTH_SHORT).show();
//            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }
}

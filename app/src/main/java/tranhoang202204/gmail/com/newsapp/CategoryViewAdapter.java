package tranhoang202204.gmail.com.newsapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryViewAdapter extends RecyclerView.Adapter<CategoryViewHolder> {
    private LayoutInflater mInflater;
    private List<String> categoryList;
    private int selectedPosition = 0;
    private Context context;

    private List<News> newsList;
    private NewsViewAdapter newsAdapter;

    public CategoryViewAdapter(Context context, List<String> categoryList, List<News> newsList, NewsViewAdapter newsAdapter) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.categoryList = categoryList;

        this.newsList = newsList;
        this.newsAdapter = newsAdapter;
    }

    public void update(List<String> categoryList){
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View customView = mInflater.inflate(R.layout.category_layout, parent, false);
        return new CategoryViewHolder(customView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String currentCategory = categoryList.get(position);
        holder.getTxtCategory().setText(currentCategory);

        // Áp dụng màu nền cho item được chọn
        if (selectedPosition == position) {
            holder.getTxtCategory().setBackgroundColor(Color.BLACK);
            holder.getTxtCategory().setTextColor(Color.WHITE);
        } else {
            holder.getTxtCategory().setBackgroundColor(Color.LTGRAY);
            holder.getTxtCategory().setTextColor(Color.BLACK);
        }

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            // Đặt lại vị trí chọn và notify adapter
            selectedPosition = position;
            notifyDataSetChanged();

            if (context instanceof MainActivity) {
                // Khi click, gọi fetchNews với tag tương ứng
                Category category = new Category();
                String tag = category.getTagForCategory(currentCategory);
                new FirebaseHelper().getNewsByTag(tag, newsList, newsAdapter);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}

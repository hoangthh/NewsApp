package tranhoang202204.gmail.com.newsapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class NewsViewAdapter extends RecyclerView.Adapter<NewsViewHolder> {
    private LayoutInflater mInflater;
    private List<News> newsList;

    public NewsViewAdapter(Context context, List<News> newsList) {
        this.mInflater = LayoutInflater.from(context);
        this.newsList = newsList;
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
        HashMap<String, String> tagUTF8 = new HashMap();

        tagUTF8.put("tin-moi-nhat", "Tin mới nhất");
        tagUTF8.put("the-gioi", "Thế giới");
        tagUTF8.put("thoi-su", "Thời sự");
        tagUTF8.put("giai-tri", "Giải trí");
        tagUTF8.put("the-thao", "Thể thao");
        tagUTF8.put("phap-luat", "Pháp luật");
        tagUTF8.put("giao-duc", "Giáo dục");


        News currentNews = newsList.get(position);
        holder.getTxtTitle().setText(currentNews.getTitle());
        holder.getTxtDescription().setText(currentNews.getDescription());
        holder.getTxtTag().setText(tagUTF8.get(currentNews.getTag()));
        holder.getTxtDate().setText(currentNews.getDate());
        //holder.getTxtComment().setText(currentNews.getComment());
        Picasso.get().load(currentNews.getImageUrl()).into(holder.getImageView());
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }
}

package tranhoang202204.gmail.com.newsapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdminNewsViewAdapter extends RecyclerView.Adapter<AdminNewsViewHolder> {
    private LayoutInflater mInflater;
    private List<News> newsList;
    private AdminHomeActivity adminHomeActivity;

    private FirebaseHelper firebaseHelper;
    private Context context;

    private final Set<Integer> animatedPositions = new HashSet<>();

    public AdminNewsViewAdapter(AdminHomeActivity adminHomeActivity, Context context, List<News> newsList) {
        this.mInflater = LayoutInflater.from(context);
        this.newsList = newsList;
        this.context = context;
        this.adminHomeActivity = adminHomeActivity;
        this.firebaseHelper = new FirebaseHelper();
    }


    @NonNull
    @Override
    public AdminNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View customView = mInflater.inflate(R.layout.news_admin_layout, parent, false);
        return new AdminNewsViewHolder(customView, this);
    }

    public void update(List<News> newsList){
        this.newsList = newsList;
    }

    @Override
    public void onBindViewHolder(@NonNull AdminNewsViewHolder holder, int position) {
        News currentNews = newsList.get(position);

        holder.getTxtTitle().setText(currentNews.getTitle());
        String dateDiff = TimeDifference.getTimeDifference(currentNews.getDate());
        holder.getTxtDate().setText(dateDiff);
        Picasso.get().load(currentNews.getImageUrl()).into(holder.getImvImage());

        holder.itemView.clearAnimation(); // Xóa animation cũ trước khi áp dụng mới
        if (!animatedPositions.contains(position)) {
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.anim_recyclerviewnews);
            holder.itemView.startAnimation(animation);
            animatedPositions.add(position);
        }

        holder.getImbEdit().setOnClickListener(v -> {
            if (!NetworkUtils.isNetworkAvailable(mInflater.getContext())) {
                Toast.makeText(holder.itemView.getContext(), "Vui lòng kết nối mạng để chỉnh sửa", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!currentNews.getLink().isEmpty()) {
                Toast.makeText(adminHomeActivity, "Không thể chỉnh sửa bài viết này", Toast.LENGTH_SHORT).show();
                return;
            }

            // Truyền đối tượng currentNews qua Intent
            Intent intent = new Intent(adminHomeActivity, AdminAddNewsActivity.class);
            intent.putExtra("newsItem", currentNews);
            adminHomeActivity.startActivity(intent);
            adminHomeActivity.overridePendingTransition(R.anim.anim_in_activity, R.anim.anim_out_activity);
        });

        holder.getImbDelete().setOnClickListener(v -> {
            if (!NetworkUtils.isNetworkAvailable(mInflater.getContext())) {
                Toast.makeText(holder.itemView.getContext(), "Vui lòng kết nối mạng để xóa tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Hiển thị xác nhận xóa
            new AlertDialog.Builder(adminHomeActivity)
                    .setTitle("Xóa tin tức")
                    .setMessage("Bạn có chắc chắn muốn xóa tin tức này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        firebaseHelper.deleteNews(currentNews.getId(), new FirebaseHelper.NewsDeleteListener() {
                            @Override
                            public void onNewsDeleteComplete(boolean isSuccess) {
                                if (isSuccess) {
                                    Toast.makeText(adminHomeActivity, "Xóa tin tức thành công", Toast.LENGTH_SHORT).show();
                                    // Xóa tin tức khỏi danh sách và cập nhật RecyclerView
                                    newsList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, newsList.size());
                                } else {
                                    Toast.makeText(adminHomeActivity, "Xóa tin tức thất bại", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        holder.itemView.setOnClickListener(v -> {
            if (v.getId() == R.id.ivEdit || v.getId() == R.id.ivDelete) {
                // Ngăn sự kiện item click bắt sự kiện từ view con
                return;
            }

            if (!NetworkUtils.isNetworkAvailable(mInflater.getContext())){
                Toast.makeText(mInflater.getContext(), "Vui lòng kết nối mạng để đọc tin chi tiết", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentNews.getLink().isEmpty()) return;
            // Lấy context từ holder.itemView
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(adminHomeActivity, DetailActivity.class);
            intent.putExtra("link", currentNews.getLink());
            adminHomeActivity.startActivity(intent);
            adminHomeActivity.overridePendingTransition(R.anim.anim_in_activity, R.anim.anim_out_activity);
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }
}

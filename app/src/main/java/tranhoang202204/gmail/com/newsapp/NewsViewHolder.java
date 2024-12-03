package tranhoang202204.gmail.com.newsapp;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewsViewHolder extends RecyclerView.ViewHolder{
    private TextView txtTitle, txtDescription, txtTag, txtDate, txtComment;
    private ImageView imvImage;
    private NewsViewAdapter adapter;

    public NewsViewHolder(@NonNull View itemView, NewsViewAdapter adapter) {
        super(itemView);
        txtTitle = itemView.findViewById(R.id.tvTitle);
        txtDescription = itemView.findViewById(R.id.tvDescription);
        txtTag = itemView.findViewById(R.id.tvTag);
        txtDate = itemView.findViewById(R.id.tvDate);
        txtComment = itemView.findViewById(R.id.tvComment);
        imvImage = itemView.findViewById(R.id.ivImage);
        this.adapter = adapter;
    }

    public TextView getTxtTitle() {
        return txtTitle;
    }

    public TextView getTxtDescription() {
        return txtDescription;
    }

    public TextView getTxtTag() {
        return txtTag;
    }

    public TextView getTxtDate() {
        return txtDate;
    }

    public TextView getTxtComment() {
        return txtComment;
    }

    public ImageView getImageView() {
        return imvImage;
    }
}

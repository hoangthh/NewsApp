package tranhoang202204.gmail.com.newsapp;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdminNewsViewHolder extends RecyclerView.ViewHolder{
    private TextView txtTitle, txtDate;
    private ImageView imvImage;
    private ImageButton imbEdit, imbDelete;
    AdminNewsViewAdapter adapter;

    public AdminNewsViewHolder(@NonNull View itemView, AdminNewsViewAdapter adapter) {
        super(itemView);
        txtTitle = itemView.findViewById(R.id.tvTitle);
        txtDate = itemView.findViewById(R.id.tvDate);
        imvImage = itemView.findViewById(R.id.ivImage);
        imbEdit = itemView.findViewById(R.id.ivEdit);
        imbDelete = itemView.findViewById(R.id.ivDelete);
        this.adapter = adapter;
    }

    public TextView getTxtDate() {
        return txtDate;
    }

    public ImageView getImvImage() {
        return imvImage;
    }

    public TextView getTxtTitle() {
        return txtTitle;
    }

    public ImageButton getImbEdit() {
        return imbEdit;
    }

    public ImageButton getImbDelete() {
        return imbDelete;
    }
}

package tranhoang202204.gmail.com.newsapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryViewHolder extends RecyclerView.ViewHolder{
    private TextView txtCategory;
    private CategoryViewAdapter adapter;

    public CategoryViewHolder(@NonNull View itemView, CategoryViewAdapter adapter) {
        super(itemView);
        txtCategory = itemView.findViewById(R.id.tvCategory);
        this.adapter = adapter;
    }

    public TextView getTxtCategory() {
        return txtCategory;
    }

    public void setTxtCategory(TextView txtCategory) {
        this.txtCategory = txtCategory;
    }
}

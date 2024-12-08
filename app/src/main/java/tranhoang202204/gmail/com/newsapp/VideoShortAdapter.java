package tranhoang202204.gmail.com.newsapp;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class VideoShortAdapter extends RecyclerView.Adapter<VideoShortAdapter.VideoViewHolder> {

    List<VideoShort> videoList;

    public VideoShortAdapter(List<VideoShort> videoList) {
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.each_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.setVideoData(videoList.get(position));
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        TextView title, desc;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.videoView);
            title = itemView.findViewById(R.id.video_title);
            desc = itemView.findViewById(R.id.video_desc);
        }

        public void setVideoData(VideoShort videoShort) {
            title.setText(videoShort.getTitle());
            desc.setText(videoShort.getDesc());

            // Lấy đường dẫn file trong cache
            File cacheFile = new File(itemView.getContext().getCacheDir(), videoShort.getFileName());
            if (cacheFile.exists()) {
                // Nếu file đã có trong cache
                videoView.setVideoPath(cacheFile.getAbsolutePath());
            } else {
                // Nếu chưa có, hiển thị thông báo hoặc tải lại
                videoView.setVideoPath(videoShort.getVideoUrl()); // Dự phòng trong khi tải
            }

            videoView.setOnPreparedListener(mp -> {
                mp.start();
                float videoRatio = mp.getVideoWidth() / (float) mp.getVideoHeight();
                float screenRatio = videoView.getWidth() / (float) videoView.getHeight();
                float scale = videoRatio / screenRatio;

                if (scale >= 1f) {
                    videoView.setScaleX(scale);
                } else {
                    videoView.setScaleY(1f / scale);
                }
            });

            videoView.setOnCompletionListener(MediaPlayer::start);

            // Thêm chức năng pause và resume khi nhấn vào màn hình
            videoView.setOnClickListener(v -> {
                if (videoView.isPlaying()) {
                    videoView.pause();
                } else {
                    videoView.start();
                }
            });
        }
    }

}
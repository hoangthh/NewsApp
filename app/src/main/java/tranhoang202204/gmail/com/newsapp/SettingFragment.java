package tranhoang202204.gmail.com.newsapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {
    TextView tvLogin, tvDisplayName, tvEmail;
    ImageView imvAvatar;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        tvLogin = view.findViewById(R.id.tvLogin);
        tvDisplayName = view.findViewById(R.id.tvDisplayName);
        tvEmail = view.findViewById(R.id.tvEmail);
        imvAvatar = view.findViewById(R.id.imvAvatar);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Người dùng đã đăng nhập
            tvLogin.setVisibility(View.GONE); // Ẩn TextView Đăng nhập

            // Hiển thị thông tin người dùng
            tvDisplayName.setText(currentUser.getDisplayName());
            tvEmail.setText(currentUser.getEmail());

            // Tải ảnh đại diện của người dùng (nếu có) bằng Picasso
            if (currentUser.getPhotoUrl() != null) {
                Picasso.get().load(currentUser.getPhotoUrl()).into(imvAvatar);
            }

        } else {
            // Người dùng chưa đăng nhập
            tvLogin.setVisibility(View.VISIBLE); // Hiển thị lại TextView Đăng nhập
            tvDisplayName.setVisibility(View.GONE); // Ẩn các thông tin người dùng
            tvEmail.setVisibility(View.GONE);
            imvAvatar.setVisibility(View.GONE);
        }

        // Gán sự kiện click
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);

            // Hành động khi click vào TextView
            Toast.makeText(getContext(), "Đăng nhập Google được nhấn", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
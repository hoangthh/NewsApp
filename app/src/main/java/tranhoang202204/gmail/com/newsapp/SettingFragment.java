package tranhoang202204.gmail.com.newsapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
    TextView tvLogin, tvLogout, tvBookmark, tvHistory, tvAdmin, tvProfile;

    private FirebaseHelper firebaseHelper; // Thêm FirebaseHelper

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

        // Khởi tạo FirebaseHelper
        firebaseHelper = new FirebaseHelper();
        firebaseHelper.initGoogleSignInClient(getActivity());

        tvLogin = view.findViewById(R.id.tvLogin);

        tvProfile = view.findViewById(R.id.tvProfile);
        tvBookmark = view.findViewById(R.id.tvBookmark);
        tvHistory = view.findViewById(R.id.tvHistory);

        tvLogout = view.findViewById(R.id.tvLogout);

        tvAdmin= view.findViewById(R.id.tvAdmin);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        tvProfile.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ProfileActivity.class));
            getActivity().overridePendingTransition(R.anim.anim_in_activity, R.anim.anim_out_activity);
        });

        tvAdmin.setOnClickListener(v -> {
            final EditText input = new EditText(getActivity());
            input.setHint("Nhập mật khẩu");
            input.setInputType(InputType.TYPE_CLASS_TEXT); // Loại input là text
            new AlertDialog.Builder(getActivity())
                    .setTitle("Chức năng Admin")
                    .setMessage("Bạn cần nhập mật khẩu của admin để đăng nhập")
                    .setView(input)
                    .setPositiveButton("Đăng nhập", (dialog, which) -> {
                        // Lấy dữ liệu từ EditText
                        String inputData = input.getText().toString();
                        if (inputData.isEmpty()) {
                            Toast.makeText(getActivity(), "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!inputData.equals("admin")) {
                            Toast.makeText(getActivity(), "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // Chuyển hướng đến AdminHomeActivity
                        Intent intent = new Intent(getActivity(), AdminHomeActivity.class);
                        this.startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.anim_in_activity, R.anim.anim_out_activity);
                    })
                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        if (currentUser != null) {
            // Người dùng đã đăng nhập
            tvLogin.setVisibility(View.GONE); // Ẩn TextView Đăng nhập
            tvLogout.setVisibility(View.VISIBLE);
            tvProfile.setVisibility(View.VISIBLE);

            tvBookmark.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("viewType", "bookmark"); // Gửi loại dữ liệu là 'history'
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_in_activity, R.anim.anim_out_activity);
            });

            tvHistory.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("viewType", "history"); // Gửi loại dữ liệu là 'history'
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.anim_in_activity, R.anim.anim_out_activity);
            });

            // Đăng xuất khi click vào tvLogout
            tvLogout.setVisibility(View.VISIBLE);
            tvLogout.setOnClickListener(v -> {
                firebaseHelper.signOut(getActivity(), new FirebaseHelper.SignOutCallback() {
                    @Override
                    public void onSignOutComplete() {
                        // Khi đăng xuất thành công
                        tvLogin.setVisibility(View.VISIBLE); // Hiển thị lại TextView Đăng nhập
                        tvBookmark.setVisibility(View.GONE);
                        tvHistory.setVisibility(View.GONE);
                        tvLogout.setVisibility(View.GONE);
                        tvProfile.setVisibility(View.GONE);

                        // Hiển thị thông báo
                        Toast.makeText(getContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                    }
                });
            });

        } else {
            // Người dùng chưa đăng nhập
            tvLogin.setVisibility(View.VISIBLE); // Hiển thị lại TextView Đăng nhập
            tvBookmark.setVisibility(View.GONE);
            tvHistory.setVisibility(View.GONE);
            tvLogout.setVisibility(View.GONE);
            tvProfile.setVisibility(View.GONE);
        }

        // Gán sự kiện click
        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            this.getActivity().startActivity(intent);
            this.getActivity().overridePendingTransition(R.anim.anim_in_activity, R.anim.anim_out_activity);
        });

        return view;
    }
}
package com.oldmen.imagegallery.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.oldmen.imagegallery.GlideApp;
import com.oldmen.imagegallery.Interface.FragmentChangeListener;
import com.oldmen.imagegallery.Model.Hit;
import com.oldmen.imagegallery.Model.ImageModel;
import com.oldmen.imagegallery.R;
import com.oldmen.imagegallery.Utils.Constants;
import com.oldmen.imagegallery.Utils.ZoomOutPageTransformer;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PagerFragment extends Fragment {

    @BindView(R.id.view_pager_fragment)
    ViewPager mViewPager;
    @BindView(R.id.footer_pager_fragment)
    LinearLayout mFooter;
    @BindView(R.id.btn_rename_footer)
    LinearLayout mBtnRenameFooter;
    @BindView(R.id.btn_info_footer)
    LinearLayout mBtnInfoFooter;
    @BindView(R.id.btn_download_footer)
    LinearLayout mBtnDownloadFooter;

    private int mCurrentPosition;
    private boolean mIsFooterHidden;
    private boolean isMainActivity;

    private Unbinder unbinder;
    private Context mContext;
    private ArrayList<ImageModel> mImgModel;
    private ArrayList<Hit> mHits;
    private PagerFragmentListener mPagerListener;
    private PagerFragmentAdapter mPagerAdapter;
    private String mFolderTitle;
    private FragmentChangeListener mFragmentListener;

    public PagerFragment() {
    }

    public static PagerFragment newInstance(int mCurrentPosition, String folderTitle, ArrayList<ImageModel> mImgModel) {
        PagerFragment fragment = new PagerFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARGUMENT_CURRENT_POSITION_PAGER, mCurrentPosition);
        args.putParcelableArrayList(Constants.ARGUMENT_IMAGE_MODEL_PAGER, mImgModel);
        args.putString(Constants.ARGUMENT_CURRENT_FOLDER_TITLE, folderTitle);
        fragment.setArguments(args);
        return fragment;
    }

    public static PagerFragment newInstance(int mCurrentPosition, ArrayList<Hit> mImgModel) {
        PagerFragment fragment = new PagerFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARGUMENT_CURRENT_POSITION_PAGER, mCurrentPosition);
        args.putParcelableArrayList(Constants.ARGUMENT_IMAGE_MODEL_PAGER, mImgModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            if (getArguments().containsKey(Constants.ARGUMENT_CURRENT_FOLDER_TITLE)) {
                mContext.registerReceiver(new PagerReceiver(), new IntentFilter(Constants.FILTER_PAGER_RECEIVER));
                mCurrentPosition = getArguments().getInt(Constants.ARGUMENT_CURRENT_POSITION_PAGER);
                mImgModel = getArguments().getParcelableArrayList(Constants.ARGUMENT_IMAGE_MODEL_PAGER);
                mFolderTitle = getArguments().getString(Constants.ARGUMENT_CURRENT_FOLDER_TITLE);
                isMainActivity = true;
            } else {
                mCurrentPosition = getArguments().getInt(Constants.ARGUMENT_CURRENT_POSITION_PAGER);
                mHits = getArguments().getParcelableArrayList(Constants.ARGUMENT_IMAGE_MODEL_PAGER);
                isMainActivity = false;
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pager, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mFragmentListener != null) {
            if (isMainActivity)
                mFragmentListener.onFragmentChanged(mImgModel.get(mCurrentPosition).getTitle());
            else
                mFragmentListener.onFragmentChanged(mHits.get(mCurrentPosition).getmTags());
        }
        mPagerAdapter = new PagerFragmentAdapter();
        mViewPager.setOffscreenPageLimit(6);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mCurrentPosition);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mFragmentListener != null) {
                    if (isMainActivity)
                        mFragmentListener.onFragmentChanged(mImgModel.get(position).getTitle());
                    else
                        mFragmentListener.onFragmentChanged(mHits.get(position).getmTags());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        initFooter();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        if (context instanceof PagerFragmentListener)
            mPagerListener = (PagerFragmentListener) context;
        if (context instanceof FragmentChangeListener)
            mFragmentListener = (FragmentChangeListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPagerListener.onImageClicked(false);
        mPagerListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initFooter() {
        if (isMainActivity) {
            mBtnDownloadFooter.setVisibility(View.GONE);
            mBtnInfoFooter.setOnClickListener(view -> {
                SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORM_FACTOR, Locale.getDefault());
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                dialogBuilder.setMessage(String.format(mContext.getString(R.string.image_dialog_info),
                        mFolderTitle,
                        mImgModel.get(mViewPager.getCurrentItem()).getTitle(),
                        dateFormat.format(Long.valueOf(mImgModel.get(mViewPager.getCurrentItem()).getDate())),
                        Float.valueOf(mImgModel.get(mViewPager.getCurrentItem()).getSize()) / 1048576))
                        .create().show();
            });
            mBtnRenameFooter.setOnClickListener(view -> {
                View editDialogView = LayoutInflater.from(mContext).inflate(R.layout.rename_image_dialog, null);
                EditText editTxt = editDialogView.findViewById(R.id.edit_txt_rename_dialog);
                editTxt.setHint(mImgModel.get(mViewPager.getCurrentItem()).getTitle());
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                dialogBuilder.setTitle(mContext.getString(R.string.rename))
                        .setView(editDialogView)
                        .setPositiveButton(mContext.getString(R.string.rename),
                                (dialogInterface, i) -> {
                                    if (editTxt.getText().toString().trim().isEmpty())
                                        Toast.makeText(mContext, "Please, type file name first!", Toast.LENGTH_SHORT).show();
                                    else
                                        mPagerListener.onImageRename(mViewPager.getCurrentItem(),
                                                mFolderTitle, editTxt.getText().toString());
                                })
                        .setNegativeButton(mContext.getString(R.string.cancel), null)
                        .create().show();
            });
        } else {
            mBtnDownloadFooter.setVisibility(View.VISIBLE);
            mBtnInfoFooter.setVisibility(View.GONE);
            mBtnRenameFooter.setVisibility(View.GONE);
            mBtnDownloadFooter.setOnClickListener(view -> {
                Hit hit = mHits.get(mViewPager.getCurrentItem());
                Glide.with(mContext).asBitmap().load(hit.getmWebformatURL())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                saveImage(resource, hit.getmTags());
                            }
                        });
            });
        }

    }

    private void saveImage(Bitmap finalBitmap, String name) {
        int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            File myDir = new File(Environment.getExternalStorageDirectory().toString() + "/pixabay_images");
            if (!myDir.exists()) {
                myDir.mkdirs();
                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(myDir)));
            }
            String imgName = name.trim() + ".jpg";
            File file = new File(myDir, imgName);
            if (file.exists())
                file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                Toast.makeText(mContext, "Image saved", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mContext, "Image wasn't saved", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface PagerFragmentListener {
        void onImageClicked(boolean mIsFooterHidden);

        void onImageRename(int position, String folderName, String newFileName);
    }

    public class PagerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras() != null && isMainActivity) {
                mCurrentPosition = intent.getIntExtra(Constants.EXTRAS_IMAGE_POSITION, 0);
                mImgModel.get(mCurrentPosition).setTitle(intent.getStringExtra(Constants.EXTRAS_NEW_IMAGE_NAME));
                mPagerAdapter.notifyDataSetChanged();
                if (mImgModel.get(mCurrentPosition).getTitle() != null && mFragmentListener != null)
                    mFragmentListener.onFragmentChanged(mImgModel.get(mCurrentPosition).getTitle());
            }
        }
    }

    class PagerFragmentAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (isMainActivity)
                return mImgModel.size();
            else
                return mHits.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.pager_item, container, false);

            PhotoView imgView = view.findViewById(R.id.img_pager_item);
            if (isMainActivity)
                loadLocalImgByGlide(mImgModel.get(position).getPath(), imgView);
            else
                loadWebImgByGlide(mHits.get(position).getmWebformatURL(), imgView);
            imgView.setOnClickListener(view1 -> {
                if (mIsFooterHidden) {
                    mFooter.animate().translationY(0).setDuration(200);
                    mViewPager.setBackgroundColor(mContext.getResources().getColor(R.color.primaryTextColor));
                    mIsFooterHidden = false;
                    mPagerListener.onImageClicked(mIsFooterHidden);
                } else {
                    mFooter.animate().translationY(mFooter.getHeight()).setDuration(200);
                    mViewPager.setBackgroundColor(mContext.getResources().getColor(R.color.color_black));
                    mIsFooterHidden = true;
                    mPagerListener.onImageClicked(mIsFooterHidden);
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((LinearLayout) object);
        }

        private void loadLocalImgByGlide(String path, ImageView view) {
            GlideApp.with(mContext)
                    .load("file://" + path)
                    .fitCenter()
                    .into(view);
        }

        private void loadWebImgByGlide(String path, ImageView view) {
            GlideApp.with(mContext)
                    .load(path)
                    .fitCenter()
                    .into(view);
        }
    }
}

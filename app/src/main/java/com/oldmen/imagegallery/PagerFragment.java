package com.oldmen.imagegallery;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PagerFragment extends Fragment {

    @BindView(R.id.view_pager_fragment)
    ViewPager mViewPager;
    @BindView(R.id.footer_pager_fragment)
    LinearLayout mFooter;

    private Unbinder unbinder;
    private Context mContext;
    private int mCurrentPosition;
    private ArrayList<ImageModel> mImgModel;

    public PagerFragment() {
    }

    public static PagerFragment newInstance(int mCurrentPosition, ArrayList<ImageModel> mImgModel) {
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
            mCurrentPosition = getArguments().getInt(Constants.ARGUMENT_CURRENT_POSITION_PAGER);
            mImgModel = getArguments().getParcelableArrayList(Constants.ARGUMENT_IMAGE_MODEL_PAGER);
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

//        mViewPager.setOnClickListener(view1 -> {
//            mFooter.animate()
//                    .translationY(0)
//                    .setDuration(200);
//            Log.i(TAG, "onViewCreated: " + mFooter.getY());
//        });
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(new PagerFragmentAdapter());
        mViewPager.setCurrentItem(mCurrentPosition);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }

    class PagerFragmentAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImgModel.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == ((LinearLayout) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.pager_item, container, false);
            PhotoView imgView = view.findViewById(R.id.img_pager_item);
            initImgByGlide(mImgModel.get(position).getPath(), imgView);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((LinearLayout) object);
        }

        private void initImgByGlide(String path, ImageView view) {
            GlideApp.with(mContext)
                    .load("file://" + path)
                    .fitCenter()
                    .into(view);
        }
    }
}

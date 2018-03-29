package com.android.cndd.tripsmanager.View;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.android.cndd.tripsmanager.Model.Option.PlanCategories;
import com.android.cndd.tripsmanager.Model.PlanCategory.IPlanViewer;
import com.android.cndd.tripsmanager.R;
import com.android.cndd.tripsmanager.ViewHelper.MarkerDemoActivity;
import com.android.cndd.tripsmanager.ViewHelper.OnFragmentAnimationEndListener;
import com.android.cndd.tripsmanager.ViewModel.PlanViewModel;

/**
 * Created by Minh Nhi on 3/14/2018.
 */

public class PlanDetailsFragment extends Fragment implements View.OnClickListener {

    private View.OnClickListener clickListener;
    private OnFragmentAnimationEndListener mListener;
    private WebView webView;
    private IPlanViewer object;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PlanViewModel planViewModel = ViewModelProviders.of(getActivity()).get(PlanViewModel.class);
        try {
            object = planViewModel.getSelected();
            if(object != null)
                webView.loadData(object.toHtmlLayout(), "text/html", "utf-8");
        }
        catch (Exception e){
            Log.e("Details Plan Error", "onCreateView: ", e);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.plan_detais_fragment_layout, container, false);
        webView = view.findViewById(R.id.content);

        ImageButton exit = view.findViewById(R.id.exit);
        exit.setOnClickListener(clickListener);

        FloatingActionButton nagative = view.findViewById(R.id.nagative);
        nagative.setOnClickListener(this);

        FloatingActionButton edit = view.findViewById(R.id.edit);
        edit.setOnClickListener(v->{
            if(object == null) return;
            Intent intent = null;
            switch (PlanCategories.valueOf(object.getCategoryName())){
                case Meeting:
                    //intent = new Intent(getContext(),MeetingCreateActivity.class);
                    break;
                case Restaurant:
                    break;
            }
            intent = new Intent(getContext(),MeetingCreateActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("action", 0);
            bundle.putInt("tripId", mTripViewer.getId());
            intent.putExtra("plan", bundle);
            startActivity(intent);
        });

        return view;
    }

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim)
    {
        int id = enter ? R.animator.slide_fragment_in : R.animator.slide_fragment_out;
        final Animator anim = AnimatorInflater.loadAnimator(getActivity(), id);
        if (enter) {
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if(mListener != null)
                        mListener.onAnimationEnd();
                }
            });
        }
        return anim;
    }

    public void setOnTextFragmentAnimationEnd(OnFragmentAnimationEndListener listener)
    {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), MarkerDemoActivity.class);
        startActivity(intent);
    }
}


package com.android.cndd.tripsmanager.View;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
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

import com.android.cndd.tripsmanager.Model.ITripViewer;
import com.android.cndd.tripsmanager.Model.Option.PlanCategories;
import com.android.cndd.tripsmanager.Model.IPlanViewer;
import com.android.cndd.tripsmanager.Model.Plan;
import com.android.cndd.tripsmanager.R;
import com.android.cndd.tripsmanager.ViewHelper.MarkerDemoActivity;
import com.android.cndd.tripsmanager.ViewHelper.OnFragmentAnimationEndListener;
import com.android.cndd.tripsmanager.ViewModel.PlanViewModel;
import com.android.cndd.tripsmanager.ViewModel.Query;
import com.android.cndd.tripsmanager.ViewModel.QueryFactory;
import com.android.cndd.tripsmanager.ViewModel.QueryTransaction;

/**
 * Created by Minh Nhi on 3/14/2018.
 */

public class PlanDetailsFragment extends Fragment implements View.OnClickListener {

    private View.OnClickListener clickListener;
    private OnFragmentAnimationEndListener mListener;
    private WebView webView;
    private IPlanViewer object;
    private PlanViewModel planViewModel;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        planViewModel = ViewModelProviders.of(getActivity()).get(PlanViewModel.class);

        MutableLiveData<IPlanViewer> planViewerMutableLiveData = planViewModel.getSelected();
        if(planViewerMutableLiveData != null)
            planViewerMutableLiveData.observe(this, iPlanViewer -> {
                if(iPlanViewer == null) return;
                object = iPlanViewer;
                webView.loadData(iPlanViewer.toHtmlLayout(),"text/html", "utf-8");
            });
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
        Bundle bundle = getArguments();
        ITripViewer mTripViewer = (ITripViewer) bundle.getSerializable("trip");

        edit.setOnClickListener(v->{
            if(object == null ||  mTripViewer == null) return;
            Intent intent = null;
            switch (PlanCategories.valueOf(object.getCategoryName())){
                case Meeting:
                    intent = new Intent(getContext(),MeetingCreateActivity.class);
                    break;
                case Restaurant:
                    break;
            }
            Bundle b = new Bundle();
            b.putInt("action", 1);
            b.putInt("tripId", mTripViewer.getId());
            b.putInt("planId", object.getId());
            intent.putExtra("plan", b);
            startActivity(intent);
        });

        FloatingActionButton del = view.findViewById(R.id.delete);
        del.setOnClickListener(v ->{
            Plan plan = planViewModel.getPlanById(object.getId());
            Query<Plan> query = new QueryFactory.DeleteOperation<>(planViewModel, plan);
            query.setUpdateUIListener(planViewModel.getPlanLiveData(mTripViewer.getId()));
            query.setArguments(object);
            QueryTransaction.getTransaction().execOnMainThread(query);
        });
        del.setOnClickListener(clickListener);
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


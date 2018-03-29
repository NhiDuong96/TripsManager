package com.android.cndd.tripsmanager.View;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.cndd.tripsmanager.Model.ITripViewer;
import com.android.cndd.tripsmanager.R;
import com.android.cndd.tripsmanager.ViewHelper.OnFragmentAnimationStartListener;
import com.android.cndd.tripsmanager.ViewHelper.OnFragmentAnimationEndListener;


/**
 * Created by Minh Nhi on 3/20/2018.
 */

public class PlansActivity extends AppCompatActivity implements
        OnFragmentAnimationEndListener,
        OnFragmentAnimationStartListener,
        FragmentManager.OnBackStackChangedListener{

    private static final String TAG = "PlansActivity";

    private PlansListFragment mPlansListFragment;
    private PlanDetailsFragment mPlanDetailsFragment;
    private View mDarkHoverView;
    private ITripViewer tripViewer;
    private Bundle bundle;

    boolean mDidSlideOut = false;
    boolean mIsAnimating = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plans_activity_layout);

        mDarkHoverView = findViewById(R.id.dark_hover_view);
        mDarkHoverView.setAlpha(0);

        bundle = new Bundle();
        bundle.putAll(getIntent().getBundleExtra("plans"));
        mPlansListFragment = new PlansListFragment();
        mPlansListFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.move_to_back_container, mPlansListFragment)
                .commit();

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        mPlansListFragment.setClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e(TAG, "onResume: " + getSupportFragmentManager().getBackStackEntryCount());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.plan_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.share:
                Log.d(TAG, "onOptionsItemSelected: share");
                break;
            case R.id.edit:
                Log.d(TAG, "onOptionsItemSelected: edit");
                break;
            case R.id.del:
                Log.d(TAG, "onOptionsItemSelected: delete");
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    View.OnClickListener mClickListener = view -> switchFragments();

    private void switchFragments () {
        if (mIsAnimating) {
            return;
        }
        mIsAnimating = true;
        if (mDidSlideOut) {
            mDidSlideOut = false;
            getSupportFragmentManager().popBackStack();
        } else {
            mDidSlideOut = true;

            mPlanDetailsFragment = new PlanDetailsFragment();
            mPlanDetailsFragment.setArguments(bundle);
            mPlanDetailsFragment.setClickListener(mClickListener);
            mPlanDetailsFragment.setOnTextFragmentAnimationEnd(this);

            Animator.AnimatorListener listener = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator arg0) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.animator.slide_fragment_in, 0, 0,
                            R.animator.slide_fragment_out);
                    transaction.add(R.id.move_to_back_container, mPlanDetailsFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            };
            slideBack (listener);
        }
    }

    @Override
    public void onBackStackChanged() {
        if (!mDidSlideOut) {
            slideForward(null);
        }

    }
    public void slideBack(Animator.AnimatorListener listener)
    {
        View movingFragmentView = mPlansListFragment.getView();

        PropertyValuesHolder rotateX =  PropertyValuesHolder.ofFloat("rotationX", 40f);
        PropertyValuesHolder scaleX =  PropertyValuesHolder.ofFloat("scaleX", 0.8f);
        PropertyValuesHolder scaleY =  PropertyValuesHolder.ofFloat("scaleY", 0.8f);
        ObjectAnimator movingFragmentAnimator = ObjectAnimator.
                ofPropertyValuesHolder(movingFragmentView, rotateX, scaleX, scaleY);

        ObjectAnimator darkHoverViewAnimator = ObjectAnimator.
                ofFloat(mDarkHoverView, "alpha", 0.0f, 0.5f);

        ObjectAnimator movingFragmentRotator = ObjectAnimator.
                ofFloat(movingFragmentView, "rotationX", 0);
        movingFragmentRotator.setStartDelay(getResources().
                getInteger(R.integer.half_slide_up_down_duration));

        AnimatorSet s = new AnimatorSet();
        s.playTogether(movingFragmentAnimator, darkHoverViewAnimator, movingFragmentRotator);
        s.addListener(listener);
        s.start();
    }

    public void slideForward(Animator.AnimatorListener listener)
    {
        View movingFragmentView = mPlansListFragment.getView();

        PropertyValuesHolder rotateX =  PropertyValuesHolder.ofFloat("rotationX", 40f);
        PropertyValuesHolder scaleX =  PropertyValuesHolder.ofFloat("scaleX", 1.0f);
        PropertyValuesHolder scaleY =  PropertyValuesHolder.ofFloat("scaleY", 1.0f);
        ObjectAnimator movingFragmentAnimator = ObjectAnimator.
                ofPropertyValuesHolder(movingFragmentView, rotateX, scaleX, scaleY);

        ObjectAnimator darkHoverViewAnimator = ObjectAnimator.
                ofFloat(mDarkHoverView, "alpha", 0.5f, 0.0f);

        ObjectAnimator movingFragmentRotator = ObjectAnimator.
                ofFloat(movingFragmentView, "rotationX", 0);
        movingFragmentRotator.setStartDelay(
                getResources().getInteger(R.integer.half_slide_up_down_duration));

        AnimatorSet s = new AnimatorSet();
        s.playTogether(movingFragmentAnimator, movingFragmentRotator, darkHoverViewAnimator);
        s.setStartDelay(getResources().getInteger(R.integer.slide_up_down_duration));
        s.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimating = false;
            }
        });
        s.start();
    }

    @Override
    public void onAnimationEnd() {
        mIsAnimating = false;
    }

    @Override
    public void onAnimationStart() {
        switchFragments();
    }
}

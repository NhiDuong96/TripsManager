package com.android.cndd.tripsmanager.viewhelper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;

import java.util.HashMap;

public class MyRecyclerView extends RecyclerView implements View.OnTouchListener{
    private static final String TAG = "MyRecyclerView";
    public interface OnTouchRemovalListener {
        void onRemovedItem(Object item);
        void onSeletedItem(Object item);
    }

    private boolean mSwiping = false;
    private boolean mItemPressed = false;
    private boolean mAnimatedAllowance = false;
    private HashMap<Long, Integer> mItemIdTopMap = new HashMap<>();

    private static final int SWIPE_DURATION = 250;
    private static final int MOVE_DURATION = 150;
    private float mDownX;
    private int mSwipeSlop = -1;

    private Context context;
    private OnTouchRemovalListener listener;
    private IRemoveViewAnimation adapter;

    public MyRecyclerView(Context context) {
        super(context);
        this.context = context;
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public void setTouchRemovalListener(OnTouchRemovalListener listener){
        this.listener = listener;
    }

    @Override
    public void setAdapter(Adapter adap) {
        super.setAdapter(adap);
        if(adap instanceof IRemoveViewAnimation){
            adapter = (IRemoveViewAnimation) adap;
            adapter.setOnTouchListener(this);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(adapter == null) return false;
        if (mSwipeSlop < 0) {
            mSwipeSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        }
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                if (mItemPressed) {
                    // Multi-item swipes not handled
                    return false;
                }
                mItemPressed = true;
                mDownX = event.getX();
                break;
            case MotionEvent.ACTION_CANCEL:
                v.setAlpha(1);
                v.setTranslationX(0);
                mItemPressed = false;
                break;
            case MotionEvent.ACTION_MOVE:
            {
                float x = event.getX() + v.getTranslationX();
                float deltaX = x - mDownX;
                float deltaXAbs = Math.abs(deltaX);
                if (!mSwiping) {
                    if (deltaXAbs > mSwipeSlop) {
                        mSwiping = true;
                        requestDisallowInterceptTouchEvent(true);

                        //my implementation
                        ValueAnimator animator = ValueAnimator.ofFloat(20.0f, 0f);
                        animator.setDuration(200);
                        animator.addUpdateListener(animation -> {
                            float animatedValue = (float)animation.getAnimatedValue();
                            v.setElevation(animatedValue);
                        });
                        animator.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                //mBackgroundContainer.showBackground(v.getTop(), v.getHeight());
                            }
                        });
                        animator.start();
                    }
                }
                if (mSwiping) {
                    v.setTranslationX((x - mDownX));
                    v.setAlpha(1 - deltaXAbs / v.getWidth());
                }
            }
            break;
            case MotionEvent.ACTION_UP:
            {
                // User let go - figure out whether to animate the view out, or back into place
                if (mSwiping) {
                    float x = event.getX() + v.getTranslationX();
                    float deltaX = x - mDownX;
                    float deltaXAbs = Math.abs(deltaX);
                    float fractionCovered;
                    float endX;
                    float endAlpha;
                    final boolean remove;
                    if (deltaXAbs > v.getWidth() / 3) {
                        // Greater than a quarter of the width - animate it out
                        fractionCovered = deltaXAbs / v.getWidth();
                        endX = deltaX < 0 ? -v.getWidth() : v.getWidth();
                        endAlpha = 0;
                        remove = true;
                    } else {
                        // Not far enough - animate it back
                        fractionCovered = 1 - (deltaXAbs / v.getWidth());
                        endX = 0;
                        endAlpha = 1;
                        remove = false;
                    }
                    // Animate position and alpha of swiped item
                    // NOTE: This is a simplified version of swipe behavior, for the
                    // purposes of this demo about animation. A real version should use
                    // velocity (via the VelocityTracker class) to send the item off or
                    // back at an appropriate speed.
                    long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                    setEnabled(false);
                    v.animate().setDuration(duration).
                            alpha(endAlpha).translationX(endX).
                            withEndAction(() -> {
                                // Restore animated values
                                v.setAlpha(1);
                                v.setTranslationX(0);
                                if (remove) {
                                    animateRemoval(v);
                                } else {
                                    //my implementation
                                    ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 20.0f);
                                    animator.setDuration(500);
                                    animator.addUpdateListener(animation -> {
                                        float animatedValue = (float)animation.getAnimatedValue();
                                        v.setElevation(animatedValue);
                                    });
                                    animator.start();
                                    //
                                    mSwiping = false;
                                    setEnabled(true);
                                }
                            });
                }
                else{
                    int position = getChildPosition(v);
                    listener.onSeletedItem(adapter.getItem(position));
                }
            }
            mItemPressed = false;
            break;
            default:
                return false;
        }
        return true;
    }

    private void animateRemoval(View viewToRemove) {
        for (int i = 0; i < getChildCount(); ++i) {
            View child = getChildAt(i);
            if (child != viewToRemove) {
                long itemId = adapter.getIdItem(i);
                mItemIdTopMap.put(itemId, child.getTop());
            }
        }

        // onDelete the item from the adapter
        int position = getChildPosition(viewToRemove);
        listener.onRemovedItem(adapter.getItem(position));
        adapter.removeItem(position);

        final ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                observer.removeOnPreDrawListener(this);
                boolean firstAnimation = true;
                for (int i = 0; i < getChildCount(); ++i) {
                    final View child = getChildAt(i);
                    long itemId = adapter.getIdItem(i);
                    Integer startTop = mItemIdTopMap.get(itemId);
                    int top = child.getTop();
                    if (startTop != null) {
                        if (startTop != top) {
                            int delta = startTop - top;
                            child.setTranslationY(delta);
                            child.animate().setDuration(MOVE_DURATION).translationY(0);
                            if (firstAnimation) {
                                child.animate().withEndAction(() -> {
                                    mSwiping = false;
                                    setEnabled(true);
                                });
                                firstAnimation = false;
                            }
                        }
                    } else {
                        int childHeight = child.getHeight();
                        startTop = top + (i > 0 ? childHeight : -childHeight);
                        int delta = startTop - top;
                        child.setTranslationY(delta);
                        child.animate().setDuration(MOVE_DURATION).translationY(0);
                        if (firstAnimation) {
                            child.animate().withEndAction(() -> {
                                mSwiping = false;
                                setEnabled(true);
                            });
                            firstAnimation = false;
                        }
                    }
                    child.setElevation(20.0f);
                }
                mItemIdTopMap.clear();
                return true;
            }
        });
    }
}

package com.plattysoft.snappinglist;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by manolo.dantonio
 * on 19/10/2017.
 */

class CarouselViewManager extends LinearLayoutManager {
    // Shrink the cards around the center by a factor of 25%
    private final float mShrinkAmount = 0.25f;
    // The cards will be at max shrink when they are 75% of the way between the
    // center and the edge.
    private final float mShrinkDistance = 0.75f;
    private int maxPagePosition = 1;

    public CarouselViewManager(Context context) {
        super(context);
    }

    public CarouselViewManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public CarouselViewManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {


        // Stop scrolling if user can not go on next page
        if (checkStopScroll(dx)) return 0;
        /////////////

        // Fade next page
        checkFadeNext(dx);
        /////////////


        /// Interpolator - page animations
        int scrolled = super.scrollHorizontallyBy(dx, recycler, state);

        float midpoint = getWidth() / 2.f;
        float distance0 = 0.f;
        float distance1 = mShrinkDistance * midpoint;
        float shrink0 = 1.f;
        float shrink1 = 1.f - mShrinkAmount;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            float childMidpoint =
                    (getDecoratedRight(child) + getDecoratedLeft(child)) / 2.f;
            float distanceElab = Math.min(distance1, Math.abs(midpoint - childMidpoint));
            float scaleX = (float) (shrink0 + ((shrink1 * 1.1) - shrink0) * (distanceElab - distance0) / (distance1 - distance0));
            float scaleY = shrink0 + (shrink1 - shrink0) * (distanceElab - distance0) / (distance1 - distance0);

            child.setScaleX(scaleX);
            child.setScaleY(scaleY);
        }

        return scrolled;
    }

    private void checkFadeNext(int dx) {
        if (dx < 0) return;
        View nextPage = getChildAt(2);
        if (nextPage == null) return;

        boolean isFade = findFirstCompletelyVisibleItemPosition() + 1 > maxPagePosition;
        nextPage.setAlpha(isFade ? 0.4f : 1f);
    }

    private boolean checkStopScroll(int dx) {
        View child = getChildAt(1); // getChild(n) gets position of visible items. We get the central one.
        int centralPosition = findFirstCompletelyVisibleItemPosition();
        if (child == null // we have no view
                || dx < 0  // or is scolling left
                || centralPosition < maxPagePosition) { // or has not reached checkpoint
            return false; //do not stop
        } else if (centralPosition > maxPagePosition) {
            scrollToPosition(maxPagePosition);
            return true;
        }

        float childMidpoint = (getDecoratedRight(child) + getDecoratedLeft(child)) / 2.f;
        float midpoint = getWidth() / 2.f;
        float centerPoint = childMidpoint - midpoint; // 0 at position 1
        return (centerPoint <= 0); //has reached max position
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        scrollVerticallyBy(0, recycler, state);
    }

    public int getMaxPagePosition() {
        return maxPagePosition;
    }

    public void setMaxPagePosition(int maxPagePosition) {
        this.maxPagePosition = maxPagePosition;
    }

}

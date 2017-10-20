package com.plattysoft.snappinglist;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

public class MainActivity extends Activity {

    private static final int NUM_ITEMS = 5;
    private static final String BUNDLE_LIST_PIXELS = "allPixels";
    private static final int MAX_FLING_SPEED = 8000;

    private float itemWidth, padding, firstItemWidth, allPixels;
    private SlowRecyclerView recyclerView;
    private TestAdapter adapter;
    private CarouselViewManager carouselViewManager;


    private void advanceCarousel() {
        carouselViewManager.setMaxPagePosition(carouselViewManager.getMaxPagePosition() + 1);
        scrollListToPosition(recyclerView, carouselViewManager.getMaxPagePosition());
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

        itemWidth = getResources().getDimension(R.dimen.item_width);
        padding = (size.x - itemWidth) / 2;
        firstItemWidth = getResources().getDimension(R.dimen.padding_item_width);
        allPixels = 0;

        recyclerView = findViewById(R.id.item_list);

        adapter = new TestAdapter(NUM_ITEMS);
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(new TestAdapter.RecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                //todo test
//                if (position == 2) adapter.setCheckPointReached(position);
//                //
                advanceCarousel();
//                checkPointSensor(view, position);

            }
        });

        carouselViewManager = new CarouselViewManager(getApplicationContext());
        carouselViewManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerView.setLayoutManager(carouselViewManager);

        recyclerView.setMaxSpeed(MAX_FLING_SPEED);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                synchronized (this) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        calculatePositionAndScroll(recyclerView);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dx == 0 && dy == 0) return;

                allPixels += dx;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        final RecyclerView items = (RecyclerView) findViewById(R.id.item_list);

        ViewTreeObserver vto = items.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                items.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                calculatePositionAndScroll(items);
            }
        });
    }

    private void calculatePositionAndScroll(RecyclerView recyclerView) {
        int expectedPosition = Math.round((allPixels + padding - firstItemWidth) / itemWidth);
        // Special cases for the padding items
        if (expectedPosition == -1) {
            expectedPosition = 0;
        } else if (expectedPosition >= recyclerView.getAdapter().getItemCount() - 2) {
            expectedPosition--;
        }
        scrollListToPosition(recyclerView, expectedPosition);
    }

    private void scrollListToPosition(RecyclerView recyclerView, int expectedPosition) {
        float targetScrollPos = expectedPosition * itemWidth + firstItemWidth - padding;
        float missingPx = (targetScrollPos - allPixels);
        if (missingPx != 0) {
            recyclerView.smoothScrollBy((int) missingPx, 0);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        allPixels = savedInstanceState.getFloat(BUNDLE_LIST_PIXELS);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat(BUNDLE_LIST_PIXELS, allPixels);
    }

//    private void checkPointSensor(View view, int position) {
//        // check if view is "checkpointed"!
//        if (position <= adapter.getCheckPointReached()) {
//            disableViews(view);
//        }
//    }
//
//    private void disableViews(View view) {
//        // iterate all views to find viewGroups and restart this function
//        if (view instanceof ViewGroup) {
//            ViewGroup viewGroup = (ViewGroup) view;
//            for (int i = 0; i < viewGroup.getChildCount(); i++) {
//                View child = viewGroup.getChildAt(i);
//                disableViews(child);
//            }
//        }
//
//        // disable single views
//        if (view != null) view.setEnabled(false);
//    }
}

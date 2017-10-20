package com.plattysoft.snappinglist;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by manolo.dantonio
 * on 19/10/2017.
 */

public class SlowRecyclerView extends RecyclerView {
    private int brakeFactor = 1;
    private int maxSpeed = 0;

    public SlowRecyclerView(Context context) {
        super(context);
    }

    public SlowRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SlowRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        if (maxSpeed > 0) {
            if (velocityX > maxSpeed) velocityX = maxSpeed;
            if (velocityY > maxSpeed) velocityY = maxSpeed;
        }
        return super.fling(velocityX * brakeFactor, velocityY * brakeFactor);
    }

    public int getBrakeFactor() {
        return brakeFactor;
    }

    public void setBrakeFactor(int brakeFactor) {
        this.brakeFactor = brakeFactor;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
}

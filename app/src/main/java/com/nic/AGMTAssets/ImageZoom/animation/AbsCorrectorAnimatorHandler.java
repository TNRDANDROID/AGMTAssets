package com.nic.AGMTAssets.ImageZoom.animation;

import android.animation.ValueAnimator;

import com.nic.AGMTAssets.ImageZoom.ImageMatrixCorrector;



/**
 * Created by Martin on 13-10-2016.
 */

public abstract class AbsCorrectorAnimatorHandler implements ValueAnimator.AnimatorUpdateListener {

    private ImageMatrixCorrector corrector;
    private float[] values;

    public AbsCorrectorAnimatorHandler(ImageMatrixCorrector corrector) {
        this.corrector = corrector;
        this.values = new float[9];
    }

    public ImageMatrixCorrector getCorrector() {
        return corrector;
    }

    protected float[] getValues() {
        return values;
    }
}

package com.developer.skylight.markdowncalender.calender;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;

public class PercentView extends Drawable {

    Paint bgpaint;
    RectF rect;
    float percentage = 0;
    private int mRadius = 0;
    int color11, color22,height1,width1;
    private static int[] mColors={Color.RED,  Color.BLUE};


    private boolean mSetShader=false;
    public PercentView(int color1, int color2, int height, int width) {

       /* paint = new Paint();
        paint.setColor(color1);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        bgpaint = new Paint();
        bgpaint.setColor(color2);
        bgpaint.setAntiAlias(true);
        bgpaint.setStyle(Paint.Style.FILL);*/

        this.bgpaint = new Paint(Paint.ANTI_ALIAS_FLAG);


        rect = new RectF();
        color11=color1;
        color22=color2;
        height1=height;
        width1=width;
    }

    @Override
    protected void onBoundsChange(final Rect bounds) {
        super.onBoundsChange(bounds);
        mRadius = Math.min(bounds.width(), bounds.height()) / 2;
    }
    @Override
    public void draw(final Canvas canvas) {

        final Rect bounds = getBounds();
        //draw background circle anyway
        int left = 0;
        //int width = bounds.width();
        int top = 0;
        int width = canvas.getWidth();
        int h1 = canvas.getHeight();
        //rect.set(left, arcRectStartingY, left+width, arcRectEndingY);
        /*   rect.set(left, top, left + width, top + width);
         *//*  Shader gradient = new SweepGradient(0,bounds.height()/2, color11, color22);
        bgpaint.setShader(gradient);
        canvas.drawArc(rect, -90, (360*percentage), true, bgpaint);*//*
        Shader gradient = new SweepGradient(0,bounds.height()/2, color11, color22);
        bgpaint.setShader(gradient);
        canvas.drawArc(rect, -90, 360, false, bgpaint);*/
        //canvas.drawArc(rect, -90, 360, false, bgpaint);



        float cX=width/2F, cY=h1/2F;

       /* if (!mSetShader) {
            bgpaint.setShader(new SweepGradient(cX, cY,color11, color22));
            mSetShader=false;
        }*/
        Shader gradient = new SweepGradient(0,bounds.height()/2, color22, color11);
        bgpaint.setShader(gradient);
        canvas.drawCircle(cX, cY, Math.min(cX, cY), bgpaint);

    }





    @Override
    public void setAlpha(final int alpha) {
        bgpaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(final ColorFilter cf) {
        bgpaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}


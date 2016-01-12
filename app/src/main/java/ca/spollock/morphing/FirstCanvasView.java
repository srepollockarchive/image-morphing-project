package ca.spollock.morphing;

import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.*;
import java.util.ArrayList;

public class FirstCanvasView extends View {
    private LineController lc;
    private SecondCanvasView secondView;
    private final Paint mPaint;
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private Line tempL;
    private int idx = 0;

    public FirstCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.RED);
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(Line l : lc.firstCanvas) {
            canvas.drawLine(l.startX, l.startY, l.endX, l.endY, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Line tempLine = new Line(event.getX(), event.getY());
                lc.addLine(tempLine);
                invalidate();
                secondView.invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                lc.addX(idx, event.getX());
                lc.addY(idx, event.getY());
                invalidate();
                secondView.invalidate();
                break;
            case MotionEvent.ACTION_UP:
                lc.addX(idx, event.getX());
                lc.addY(idx, event.getY());
                idx++;
                invalidate();
                secondView.invalidate();
                break;
        }
        return true;
    }

    public void init(LineController controller, SecondCanvasView sView){
        lc = controller;
        secondView = sView;
    }

    public void indexZero(){
        idx = 0;
    }

    public void removed(){
        idx--;
    }
}
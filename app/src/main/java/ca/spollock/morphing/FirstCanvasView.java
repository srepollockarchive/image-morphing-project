package ca.spollock.morphing;

import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.*;

public class FirstCanvasView extends View {
    private LineController lc;
    private SecondCanvasView secondCanvas;
    private final Paint mPaint;
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private Line tempL;
    private int idx = 0;
    private boolean drawingMode = true;
    private boolean endOfLine;
    private Point lastTouch;
    private int closestIndex = -1;
    private Paint arcPaint;
    private final static int MAX_DISTANCE = 300;

    public FirstCanvasView(Context context){
        super(context);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.RED);
        arcPaint.setColor(Color.BLUE);
    }

    public FirstCanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.RED);
    }

    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(lc.firstCanvas != null) {
            for (Line l : lc.firstCanvas) {
                canvas.drawLine(l.startX, l.startY, l.endX, l.endY, mPaint);
            }
        }
        if(!drawingMode){
            if(closestIndex != -1){
                // if in edit, draw a line around the index we found
                canvas.drawCircle(lc.firstCanvas.get(closestIndex).startX,
                        lc.firstCanvas.get(closestIndex).startY, 20, arcPaint);
                canvas.drawCircle(lc.firstCanvas.get(closestIndex).endX,
                        lc.firstCanvas.get(closestIndex).endY, 20, arcPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if(drawingMode) {
            // drawing mode
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Line tempLine = new Line(event.getX(), event.getY());
                    lc.addLine(tempLine);
                    invalidate();
                    secondCanvas.invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    lc.addX(idx, event.getX());
                    lc.addY(idx, event.getY());
                    invalidate();
                    secondCanvas.invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    lc.addX(idx, event.getX());
                    lc.addY(idx, event.getY());
                    idx++;
                    invalidate();
                    secondCanvas.invalidate();
                    secondCanvas.updateIndex();
                    lastTouch = new Point((int)event.getX(), (int)event.getY());
                    break;
            }
        }else{
            // edit mode

            // get the closest line based on the touch of the screen
                // should this also be last touch point?

            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    // run function to find the closest point based on that object
                    lastTouch = new Point((int)event.getX(), (int)event.getY());
                    findClosestLine();
                    if(closestIndex != -1){
                        endOfLine = checkPointStartEnd(lc.firstCanvas.get(closestIndex));
                            // sets the closest to be either start or end
                    }
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    // edit the point to have the new points from the touch event
                    if(closestIndex != -1){
                        // we found one
                        if(!endOfLine) {
                            lc.firstCanvas.get(closestIndex).startX = event.getX();
                            lc.firstCanvas.get(closestIndex).startY = event.getY();
                        }else{
                            lc.firstCanvas.get(closestIndex).endX = event.getX();
                            lc.firstCanvas.get(closestIndex).endY = event.getY();
                        }
                    }
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    if(closestIndex != -1){
                        if(!endOfLine) {
                            lc.firstCanvas.get(closestIndex).startX = event.getX();
                            lc.firstCanvas.get(closestIndex).startY = event.getY();
                        }else{
                            lc.firstCanvas.get(closestIndex).endX = event.getX();
                            lc.firstCanvas.get(closestIndex).endY = event.getY();
                        }
                    }
                    invalidate();
                    break;
            }
        }
        return true;
    }

    public void init(LineController controller, SecondCanvasView sView){
        lc = controller;
        secondCanvas = sView;
    }

    public void updateIndex() { idx++; }

    public void indexZero(){
        idx = 0;
    }

    public void removed(){
        idx--;
        invalidate();
    }

    public void changeMode(boolean mode){
        drawingMode = mode;
    }

    private void findClosestLine(){
        boolean start;
        int tempDex = 0;
        int closestDistance = MAX_DISTANCE;
        // needs to loop through the array list, for both startX,Y and endX,Y of each line in the array
        // then needs to get the index to that point and draw a circle around that point
            // also change the colour of the line and the corresponding line based on that index

        // runs through all and finds the absolute closest
        for(Line l : lc.firstCanvas){
            // loops through the entire list
            int temp = checkPoint(l);
            System.out.println(temp +" "+ closestIndex);
            if(temp < closestDistance && temp != -1){
                closestIndex = tempDex;
            }
            tempDex++;
        }

    }
    // returns the distance based on the index
    private int checkPoint(Line l){
        int firstDistance = pyth((lastTouch.x - l.startX), (lastTouch.y - l.startY));
        int secondDistance = pyth((lastTouch.x - l.endX), (lastTouch.y - l.endY));

        if(MAX_DISTANCE > firstDistance) {
            return (int)firstDistance;
        }else if(MAX_DISTANCE > secondDistance){
            return (int)secondDistance;
        }
        return -1;
    }

    private boolean checkPointStartEnd(Line l){
        boolean start = false;
        int firstDistance = pyth((lastTouch.x - l.startX), (lastTouch.y - l.startY));
        int secondDistance = pyth((lastTouch.x - l.endX), (lastTouch.y - l.endY));

        if(MAX_DISTANCE < firstDistance) {
            start = true;
        }else if(MAX_DISTANCE < secondDistance){
            start = false;
        }

        return start;
    }

    private int pyth(double x, double y){
        int z;
        z = (int)Math.sqrt(((x * x) + (y * y)));
        return z;
    }
}
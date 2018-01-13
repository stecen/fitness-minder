package ase.activityminder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import ase.activityminder.activities.MainActivity;

/**
 * Created by Steven on 8/15/2015.
 */
public class CircleView extends View {
    Paint white = new Paint();

    public CircleView(Context c, AttributeSet attrs) {
        super(c, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        white.setColor(getResources().getColor(R.color.grayish));
        white.setStrokeWidth(10);
        white.setStyle(Paint.Style.STROKE);

        canvas.drawCircle(MainActivity.SCREEN_WIDTH / 2, MainActivity.SCREEN_HEIGHT / 2, /*340*/ MainActivity.SCREEN_WIDTH * .4f , white);
        Log.e("CircleView", String.format("%d, %d", MainActivity.SCREEN_HEIGHT, MainActivity.SCREEN_WIDTH));


    }
}
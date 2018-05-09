package com.igame.kingspinball;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    boolean hasRun = false;
    Rect gameArea;
    Paint ballPaint;
    Paint scorePaint;
    SurfaceHolder holder;

    int STATUS = 0;
    Rect ballLocation;
    Handler handler = new Handler();

    //球的运行方向
    int dx = 1;
    int dy = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ballPaint = new Paint();
        ballPaint.setColor(Color.WHITE);
        ballPaint.setStyle(Paint.Style.FILL);
        scorePaint = new Paint();
        scorePaint.setStyle(Paint.Style.STROKE);
        scorePaint.setColor(Color.YELLOW);
        SurfaceView surfaceView = new SurfaceView(this);
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runBall();
            }
        });
        holder = surfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (hasRun) {
                    return;
                }
                hasRun = true;
                gameArea = new Rect(10, (int) (height * 0.1), width - 10, (int) (height * 0.9));
                //draw game area.
                Canvas canvas = holder.getSurface().lockCanvas(null);
                canvas.drawColor(0xFF2B2B2B, PorterDuff.Mode.SRC_OVER);
                canvas.drawRect(gameArea, scorePaint);
                holder.getSurface().unlockCanvasAndPost(canvas);

                //start draw.
                ballLocation = new Rect(width / 2 - 50, height / 2 - 50, width / 2 + 50, height / 2 + 50);
                canvas = holder.getSurface().lockCanvas(ballLocation);
                canvas.drawColor(0xFF2B2B2B, PorterDuff.Mode.SRC_OVER);
                canvas.drawCircle(width / 2, height / 2, 50, ballPaint);
                holder.getSurface().unlockCanvasAndPost(canvas);

                //begin game.
                start();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                STATUS = -1;
            }
        });
        setContentView(surfaceView);
    }

    private void start() {
        if (STATUS != 0) {
            return;
        }
        STATUS = 1;
    }

    private void runBall() {
        if (STATUS != 1) {
            return;
        }
        //start move
        ballLocation.offset(dx, dy);
        //碰撞检测
        if (ballLocation.right >= gameArea.right || ballLocation.left <= gameArea.left) {
            dx = -dx;
        }
        if (ballLocation.top <= gameArea.top || ballLocation.bottom >= gameArea.bottom) {
            dy = -dy;
        }
        //draw
        int left = ballLocation.left - ballLocation.width() < gameArea.left ? gameArea.left : ballLocation.left - ballLocation.width();
        int top = ballLocation.top - ballLocation.height() < gameArea.top ? gameArea.top : ballLocation.top - ballLocation.height();
        int right = ballLocation.right + ballLocation.width() > gameArea.right ? gameArea.right : ballLocation.right + ballLocation.width();
        int bottom = ballLocation.bottom + ballLocation.height() > gameArea.bottom ? gameArea.bottom : ballLocation.bottom + ballLocation.height();
        Rect inOutDirty = new Rect(left, top, right, bottom);
        Canvas canvas = holder.getSurface().lockCanvas(inOutDirty);
        canvas.drawColor(0xFF2B2B2B, PorterDuff.Mode.SRC_OVER);
        canvas.drawCircle(ballLocation.exactCenterX(), ballLocation.exactCenterY(), 50, ballPaint);
        holder.getSurface().unlockCanvasAndPost(canvas);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runBall();
            }
        }, 5);
    }
}

package com.igame.kingspinball;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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
    int dx = -20;
    int dy = -12;

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
                ballLocation = new Rect(width / 2 - 50, height / 2 - 50, width / 2 + 50, height / 2 + 50);

                initDraw();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initDraw();
                        //begin game.
                        start();
                    }
                }, 1);

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                STATUS = -1;
            }
        });
        setContentView(surfaceView);
    }

    private void initDraw(){
        Canvas canvas = holder.lockCanvas(null);
        //清屏
        canvas.drawColor(0xFF2B2B2B, PorterDuff.Mode.SRC_OVER);
        //绘制黄色框
        canvas.drawRect(gameArea, scorePaint);
        //计算小球位置
        //绘制小球
        canvas.drawCircle(ballLocation.centerX(), ballLocation.centerY(), 50, ballPaint);
        holder.unlockCanvasAndPost(canvas);
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
        //清除上一次痕迹
        Canvas canvas = holder.lockCanvas(getUpdateArea(ballLocation, gameArea, dx, dy));
        canvas.drawColor(0xFF2B2B2B, PorterDuff.Mode.SRC_OVER);
        //边界检测， tx，ty为实际移动位置，当临近黄色框边缘时，该值会使球移动到黄色框内紧挨边缘的位置而不会超出黄色框
        int tx = dx, ty = dy;
        if (ballLocation.left + dx <= gameArea.left) {
            tx = gameArea.left - ballLocation.left + 1;
            dx = -dx;
        } else if (ballLocation.right + dx >= gameArea.right) {
            tx = gameArea.right - ballLocation.right - 1;
            dx = -dx;
        }
        if (ballLocation.top + dy <= gameArea.top) {
            ty = gameArea.top - ballLocation.top + 1;
            dy = -dy;
        } else if (ballLocation.bottom + dy >= gameArea.bottom) {
            ty = gameArea.bottom - ballLocation.bottom - 1;
            dy = -dy;
        }
        //更新圆的位置
        ballLocation.offset(tx, ty);
        canvas.drawCircle(ballLocation.centerX(), ballLocation.centerY(), 50, ballPaint);
        holder.getSurface().unlockCanvasAndPost(canvas);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                runBall();
            }
        }, 10);
    }

    //计算实际应该重绘的区域
    private static Rect getUpdateArea(Rect src, Rect dst, int dx, int dy) {
        int l = src.left, t = src.top, r = src.right, b = src.bottom;
        if (dx > 0) {
            r = src.right + dx;
        } else {
            l = src.left + dx;
        }
        if (dy > 0) {
            b = src.bottom + dy;
        } else {
            t = src.top + dy;
        }
        //-1是为了不影响黄色边框
        l = l <= dst.left ? dst.left + 1 : l;
        t = t <= dst.top ? dst.top + 1 : t;
        r = r >= dst.right ? dst.right - 1 : r;
        b = b >= dst.bottom ? dst.bottom - 1 : b;
        return new Rect(l, t, r, b);
    }
}

package com.riku.land.cs.seekwifi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.net.wifi.ScanResult;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * WifiRadarView
 */
class WifiRadarView extends View {
    private static final int HAND_SWEEP_ANGLE = 60;

    // Datas
    private List<ScanResult> scanResults = new ArrayList<ScanResult>();
    // Canvas
    private Paint backgroundPaint = new Paint();
    private Paint contourPaint = new Paint();
    private Paint handPaint = new Paint();
    private Paint pointPaint = new Paint();
    private int radarAngle = 0;
    private int frameCount = 0;
    // View Handler
    private final Handler handler = new Handler();

    public WifiRadarView(Context context) {
        this(context, null);
    }

    public WifiRadarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WifiRadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        backgroundPaint.setColor(Color.BLACK);
        contourPaint.setStyle(Paint.Style.STROKE);
        contourPaint.setStrokeWidth(10);
        contourPaint.setColor(Color.GREEN);
        contourPaint.setAlpha(108);
        handPaint.setColor(Color.GREEN);
        handPaint.setAlpha(96);
        handPaint.setStyle(Paint.Style.FILL);
        pointPaint.setColor(Color.WHITE);
        pointPaint.setAlpha(128);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                radarAngle = (radarAngle + 15) % 360;
                frameCount++;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        invalidate();
                    }
                });
            }
        }, 0, 100);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 描画クラス呼び出し
        drawBackground(canvas);
        drawContour(canvas);
        drawHand(canvas);
        drawPoints(canvas);
    }

    /**
     * 背景の描画
     *
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
    }

    /**
     * 等高線の描画
     *
     * @param canvas
     */
    private void drawContour(Canvas canvas) {
        contourPaint.setAlpha(frameCount % 2 == 0 ? 108 : 96);
        final int centerX = canvas.getWidth() / 2;
        final int centerY = canvas.getHeight() / 2;
        final int radius = Math.min(canvas.getWidth(), canvas.getHeight()) / 2;
        // Padding
        for (float i = 0.1F; i < 1; i += 0.1F) {
            int realRadius = (int) (radius * i);
//            Log.d(WifiRadarView.class.getSimpleName(), "centerX:" + centerX + ", centerY:" + centerY + ", realRadius:" + realRadius);
            canvas.drawCircle(centerX, centerY, realRadius, contourPaint);
        }
    }

    /**
     * 芯の描画
     *
     * @param canvas
     */
    private void drawHand(Canvas canvas) {
        final int centerX = canvas.getWidth() / 2;
        final int centerY = canvas.getHeight() / 2;
        final int radius = (int) (Math.min(canvas.getWidth(), canvas.getHeight()) / 2 * 1.1F);
        final RectF arcOval = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
//        Log.d(WifiRadarView.class.getSimpleName(), "randarAngle:" + radarAngle);
        canvas.drawArc(arcOval, radarAngle, HAND_SWEEP_ANGLE, true, handPaint);
    }

    private void drawPoints(Canvas canvas) {
        pointPaint.setAlpha(frameCount % 2 == 0 ? 128 : 108);
        int maxLevel = 0;
        for (ScanResult scan : scanResults) {
            maxLevel = Math.max(maxLevel, Math.abs(scan.level));
        }
        int level;
        for (ScanResult scan : scanResults) {
            level = Math.abs(scan.level);
            // 近い方を手前に表示させるため、逆にする
            final float percentage = 1 - ((float) level / (float) maxLevel);
            // FIX Radius size
            canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2 * percentage, 20, pointPaint);
        }
    }

    public void updateScanResult(List<ScanResult> scanResults) {
        this.scanResults = scanResults;
    }
}

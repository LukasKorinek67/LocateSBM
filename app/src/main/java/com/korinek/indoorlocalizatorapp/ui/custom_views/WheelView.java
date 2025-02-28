package com.korinek.indoorlocalizatorapp.ui.custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.korinek.indoorlocalizatorapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WheelView extends View {

    private Paint segmentPaint;
    private Paint highlightSegmentPaint;
    private Paint centerPaint;
    private Paint textPaint;
    private Paint valuePaint;
    private Paint linePaint;
    private RectF outerRect;
    private RectF innerRect;
    private int mainColor;
    private final List<WheelViewSegment> menuItems = new ArrayList<>();
    private float centerX, centerY, outerRadius, innerRadius;
    private int selectedSegment = -1;
    private boolean isCenterPressed = false;

    public WheelView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        // Load color from attribute
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.WheelView);
        mainColor = a.getColor(R.styleable.WheelView_mainColor, Color.BLACK);
        a.recycle();

        // circle item segment
        segmentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        segmentPaint.setStyle(Paint.Style.FILL);
        segmentPaint.setColor(mainColor);

        // highlighted/chosen circle item segment
        highlightSegmentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        highlightSegmentPaint.setStyle(Paint.Style.FILL);
        highlightSegmentPaint.setColor(lightenColor(mainColor));

        // center button
        centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setStyle(Paint.Style.FILL);

        // texts - name o attributes
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
        textPaint.setTextSize(35);
        textPaint.setTextAlign(Paint.Align.CENTER);

        // value inside circle
        valuePaint= new Paint(Paint.ANTI_ALIAS_FLAG);
        valuePaint.setColor(Color.DKGRAY);
        valuePaint.setTextSize(100);
        valuePaint.setFakeBoldText(true);
        valuePaint.setTextAlign(Paint.Align.CENTER);

        // gap between segments
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(ContextCompat.getColor(getContext(), R.color.background_color));
        linePaint.setStrokeWidth(10);
    }

    private int lightenColor(int color) {
        float mixFactor = 0.4f;
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        red = (int) (red + (255 - red) * mixFactor);
        green = (int) (green + (255 - green) * mixFactor);
        blue = (int) (blue + (255 - blue) * mixFactor);

        return Color.argb(alpha, red, green, blue);
    }

    private int darkenColor(int color) {
        float mixFactor = 0.95f;
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        red = (int) (red + (red) * mixFactor);
        green = (int) (green + (green) * mixFactor);
        blue = (int) (blue + (blue) * mixFactor);

        return Color.argb(alpha, red, green, blue);
    }

    public void setData(Map<String, Object> items) {
        menuItems.clear();
        for (Map.Entry<String, Object> entry : items.entrySet()) {
            menuItems.add(new WheelViewSegment(entry.getKey(), entry.getValue(), getContext()));
        }
        if (menuItems.isEmpty()) {
            selectedSegment = -1;
        } else {
            selectedSegment = 0;
        }
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2f;
        centerY = h / 2f;
        outerRadius = Math.min(w, h) / 2f - 20;
        innerRadius = outerRadius * 0.45f;

        if (outerRect == null || innerRect == null) {
            outerRect = new RectF();
            innerRect = new RectF();
        }

        outerRect.set(centerX - outerRadius, centerY - outerRadius, centerX + outerRadius, centerY + outerRadius);
        innerRect.set(centerX - innerRadius, centerY - innerRadius, centerX + innerRadius, centerY + innerRadius);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (menuItems.isEmpty()) {
            return;
        }
        int segmentCount = menuItems.size();
        drawSegments(canvas, segmentCount);
        drawSegmentGaps(canvas, segmentCount);
        drawCenter(canvas);
    }

    private void drawSegments(Canvas canvas, int segmentCount) {
        float segmentAngle = 360f / segmentCount;

        int index = 0;
        for (WheelViewSegment menuItem : menuItems) {
            float startAngle = index * segmentAngle - 90;

            // segment
            Path path = new Path();
            path.arcTo(outerRect, startAngle, segmentAngle);
            path.arcTo(innerRect, startAngle + segmentAngle, -segmentAngle);
            path.close();
            canvas.drawPath(path, index == selectedSegment ? highlightSegmentPaint : segmentPaint);

            // icon
            float distanceFromCenter = 0.72f;
            Drawable icon = ContextCompat.getDrawable(getContext(), menuItem.getIcon());
            if (icon != null) {
                int iconSize = 120;
                float iconAngle = startAngle + segmentAngle / 2;
                int iconX = (int) (centerX + (outerRadius * distanceFromCenter) * Math.cos(Math.toRadians(iconAngle)) - (float) iconSize / 2);
                int iconY = (int) (centerY + (outerRadius * distanceFromCenter) * Math.sin(Math.toRadians(iconAngle)) - (float) iconSize / 2);

                icon.setBounds(iconX, iconY, iconX + iconSize, iconY + iconSize);
                icon.setTint(ContextCompat.getColor(getContext(), R.color.white));
                icon.draw(canvas);
            }

            // text
            float textAngle = startAngle + segmentAngle / 2;
            float textX = (float) (centerX + (outerRadius * distanceFromCenter) * Math.cos(Math.toRadians(textAngle)));
            float textY = (float) (centerY + (outerRadius * distanceFromCenter) * Math.sin(Math.toRadians(textAngle))) + 85; // move below icon
            canvas.drawText(menuItem.getText(), textX, textY, textPaint);

            index++;
        }
    }

    private void drawSegmentGaps(Canvas canvas, int segmentCount) {
        for (int i = 0; i < segmentCount; i++) {
            float angle = i * (360f / segmentCount) - 90f;
            float startX = (float) (centerX + outerRadius * Math.cos(Math.toRadians(angle)));
            float startY = (float) (centerY + outerRadius * Math.sin(Math.toRadians(angle)));
            float endX = centerX;
            float endY = centerY;
            canvas.drawLine(startX, startY, endX, endY, linePaint);
        }
    }

    private void drawCenter(Canvas canvas) {
        centerPaint.setColor(isCenterPressed ?
                darkenColor(ContextCompat.getColor(getContext(), R.color.background_color)) :
                ContextCompat.getColor(getContext(), R.color.background_color));

        canvas.drawCircle(centerX, centerY, innerRadius, centerPaint);

        // value or icon inside
        if (selectedSegment == -1) {
            Drawable icon = ContextCompat.getDrawable(getContext(), R.drawable.ic_gesture_tap);
            if (icon != null) {
                int iconSize = 250;
                int iconX = (int) (centerX - (float) iconSize / 2);
                int iconY = (int) (centerY - (float) iconSize / 2);

                icon.setBounds(iconX, iconY, iconX + iconSize, iconY + iconSize);
                icon.setTint(mainColor);
                icon.draw(canvas);
            }
        } else {
            canvas.drawText(menuItems.get(selectedSegment).getValueWithUnit(), centerX, centerY + 30, valuePaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() - centerX;
        float y = event.getY() - centerY;
        double distance = Math.sqrt(x * x + y * y);

        boolean isInsideCenter = distance < innerRadius;
        boolean isSegmentClick = distance > innerRadius && distance < outerRadius;

        if (isInsideCenter) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (selectedSegment != -1) {
                        isCenterPressed = true;
                        invalidate();
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    if (isCenterPressed) {
                        isCenterPressed = false;
                        invalidate();
                        performClick();
                        showSegmentDialog("Středové tlačítko");
                    }
                    break;

                case MotionEvent.ACTION_CANCEL:
                    isCenterPressed = false;
                    invalidate();
                    break;
            }
            return true;
        } else if (isSegmentClick) {
            int segmentCount = menuItems.size();
            float sweepAngle = 360f / segmentCount;
            double angle = Math.toDegrees(Math.atan2(y, x)) + 90;
            if (angle < 0) angle += 360;

            int newSelectedSegment = (int) (angle / sweepAngle);

            if (newSelectedSegment < segmentCount) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        if (selectedSegment != newSelectedSegment) {
                            selectedSegment = newSelectedSegment;
                            invalidate();
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        invalidate();
                        performClick();
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        selectedSegment = -1;
                        invalidate();
                        break;
                }
            }
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    private void showSegmentDialog(String segmentName) {
        new AlertDialog.Builder(getContext())
                .setTitle("Vybraný segment")
                .setMessage("Klikl jsi na: " + segmentName)
                .setPositiveButton("OK", null)
                .show();
    }
}

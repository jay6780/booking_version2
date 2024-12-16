package com.hair.booking.activity.tools.Model;

import android.text.Layout;
import android.text.Spannable;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.annotation.NonNull;

public abstract class LinkMovementMethods extends android.text.method.LinkMovementMethod {
    public abstract boolean onLinkClick(@NonNull TextView textView, @NonNull String url);

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
            if (link.length != 0) {
                String url = link[0].getURL();
                // Call your abstract method to handle link clicks
                return onLinkClick(widget, url) || super.onTouchEvent(widget, buffer, event);
            }
        }
        return super.onTouchEvent(widget, buffer, event);
    }
}

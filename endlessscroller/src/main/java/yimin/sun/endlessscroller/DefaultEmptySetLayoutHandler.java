package yimin.sun.endlessscroller;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yzsh-sym on 2017/9/7.
 */

public class DefaultEmptySetLayoutHandler implements ILayoutHandler {

    int drawableRes;
    String text;

    public DefaultEmptySetLayoutHandler(int drawableRes, String text) {
        this.drawableRes = drawableRes;
        this.text = text;
    }

    @Override
    public void handleLayout(View view) {
        TextView textView = (TextView) view.findViewById(R.id.text1);
        ImageView imageView = (ImageView) view.findViewById(R.id.image1);

        textView.setText(text);
        imageView.setImageResource(drawableRes);
    }
}

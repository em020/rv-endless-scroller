package yimin.sun.endlessscroller;

import java.util.List;

/**
 * Created by yzsh-sym on 2017/9/7.
 */

public abstract class EndlessRViewAdapter2 extends EndlessRViewAdapter {

    public <T> void loadSuccess(int refreshPosition, int numRequested, List<T> data) {
        int numFetched = data.size();
        loadSuccess(refreshPosition, numRequested, numFetched);

        if (refreshPosition == TOP) {
            refillAndNotify(data);
        } else {
            appendAndNotify(data);
        }
    }

    abstract <T> void appendAndNotify(List<T> data);

    abstract <T> void refillAndNotify(List<T> data);
}

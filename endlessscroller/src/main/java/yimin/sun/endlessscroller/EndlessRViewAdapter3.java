package yimin.sun.endlessscroller;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yzsh-sym on 2017/9/7.
 */

public abstract class EndlessRViewAdapter3<T> extends EndlessRViewAdapter {

    public List<T> data;


    public void loadSuccess(int refreshPosition, int numRequested, List<T> dataLoaded) {
        int numFetched = dataLoaded.size();
        loadSuccess(refreshPosition, numRequested, numFetched);

        if (refreshPosition == TOP) {
            if (data == null) {
                data = new ArrayList<>();
            }
            data.clear();
            data.addAll(dataLoaded);
            notifyDataSetChanged();
        } else {
            data.addAll(dataLoaded);
            notifyItemRangeInserted(getDataSetSize() - dataLoaded.size(), dataLoaded.size());
        }
    }

    @Override
    public int getDataSetSize() {
        return data == null ? 0 : data.size();
    }


//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder2(ViewGroup parent, int viewType) {
//        return null;
//    }
//
//    @Override
//    public void onBindViewHolder2(RecyclerView.ViewHolder holder, int position) {
//
//    }
//
//    @Override
//    public int getItemCount2() {
//        return 0;
//    }
//
//    @Override
//    public int getItemViewType2(int position) {
//        return 0;
//    }

}

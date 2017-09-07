package yimin.sun.endlessscroller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yzsh-sym on 2017/2/22.
 */

public abstract class EndlessRViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IEndlessRViewAdapter {

    public static final int TOP = 0;
    public static final int BOTTOM = 1;

    private int mFooterState = FOOTER_INVISIBLE;
    private View.OnClickListener retryListener;
    private FooterConfig footerConfig = new FooterConfig();


    @Override
    public int getFooterState() {
        return mFooterState;
    }

    @Override
    public void setFooterState(int state) {
        mFooterState = state;
    }

    @Override
    public void setFooterRetryListener(View.OnClickListener clickListener) {
        retryListener = clickListener;
    }

    @Override
    public View.OnClickListener getFooterRetryListener() {
        return retryListener;
    }

    /**
     * 改变footer样式至加载中，一般来说是显示一个环形progress bar
     */
    public void loading() {
        setFooterState(FOOTER_LOADING);
        notifyItemChanged(getItemCount() - 1);
    }

    /**
     * 改变footer样式至加载成功，一般来说就是隐藏footer。注意这里仅仅改变footer样式，不会操控数据，刷新列表。
     *
     * @param refreshPosition {@link #BOTTOM} or {@link #TOP} 顶部刷新和底部加载逻辑有区别，所以需要知道这个信息
     * @param numRequested 请求数据数量，如20个
     * @param numFetched 获取到的数据数量
     */
    public void loadSuccess(int refreshPosition, int numRequested, int numFetched) {

        if (numFetched <= 0) {

            // 拉到了0条数据，小于0是不可能的，
            // 如果此时是顶部刷新，说明数据源为空，此时应为empty set状态，
            // 否则，如果是底部加载，则是上一页刚好拉满，这一页刚好为0条，这种情况是no more，

            if (refreshPosition == TOP) {
                setFooterState(FOOTER_EMPTY_SET);
            } else {
                //BOTTOM
                setFooterState(FOOTER_NO_MORE);
            }

        } else {

            if (numFetched < numRequested) {
                setFooterState(FOOTER_NO_MORE);
            } else {
                // num fetched >= num requested
                // mostly its just =, > is a server error, anyway, deal them together
                setFooterState(FOOTER_INVISIBLE);
            }

        }

        notifyItemChanged(getItemCount() - 1);
    }

    /**
     * 改变footer样式至加载失败，一般来说是显示“加载失败，点击重试”。
     * @param retryImpl
     */
    public void loadFail(final EndlessScroller.Retryable retryImpl) {
        setFooterRetryListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retryImpl.retry();
                setFooterState(FOOTER_LOADING);
                notifyItemChanged(getItemCount() - 1);
            }
        });
        setFooterState(FOOTER_RETRY);
        notifyItemChanged(getItemCount() - 1);
    }


    public void setFooterConfig(@NonNull FooterConfig footerConfig) {
        this.footerConfig = footerConfig;
    }

    // ------ 以下方法对尾项即footer进行管理，继承者不需要考虑footer，对继承者是透明的


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_FOOTER) {
            Context context = parent.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.item_footer, parent, false);
            return new FooterVH(view, footerConfig);
        } else {
            return onCreateViewHolder2(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == getItemCount() - 1) {
            ((FooterVH)holder).onBind();
        } else {
            onBindViewHolder2(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return getItemCount2() + 1; // + footer item
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return ITEM_TYPE_FOOTER;
        } else {
            return getItemViewType2(position);
        }
    }

    private static final int ITEM_TYPE_FOOTER = -1000;

    /**
     * 父类的onCreateViewHolder方法，对footer项创建footerVH，对其他位置调用本方法
     */
    abstract public RecyclerView.ViewHolder onCreateViewHolder2(ViewGroup parent, int viewType);

    /**
     * 父类的onBindViewHolder方法，对footer项进行相应处理，对其他项调用本方法
     */
    abstract public void onBindViewHolder2(RecyclerView.ViewHolder holder, int position);

    /**
     * 父类的getItemCount方法返回本方法+1，即添加footer
     */
    abstract public int getItemCount2();

    /**
     * 父类的getItemViewType方法，对 getItemCount() - 1的位置返回footer类型-1000，其他位置调用本方法
     */
    abstract public int getItemViewType2(int position);


    private class FooterVH extends RecyclerView.ViewHolder {

//        static FooterVH create(ViewGroup parent, FooterConfig footerConfig) {
//            Context context = parent.getContext();
//            View view = LayoutInflater.from(context).inflate(R.layout.item_footer, parent, false);
//            return new FooterVH(view, footerConfig);
//        }

        FrameLayout frameLoading, frameNoMore, frameFail, frameEmptySet;

        FooterVH(View itemView, FooterConfig footerConfig) {
            super(itemView);

            frameLoading = (FrameLayout) itemView.findViewById(R.id.frame_loading);
            frameNoMore = (FrameLayout) itemView.findViewById(R.id.frame_no_more);
            frameFail = (FrameLayout) itemView.findViewById(R.id.frame_fail);
            frameEmptySet = (FrameLayout) itemView.findViewById(R.id.frame_empty_set);

            Context context = itemView.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            if (footerConfig.loadingLayout != null) {
                View view = inflater.inflate(footerConfig.loadingLayout, frameLoading, false);
                frameLoading.removeAllViews();
                frameLoading.addView(view);
            }

            if (footerConfig.noMoreLayout != null) {
                View view = inflater.inflate(footerConfig.noMoreLayout, frameNoMore, false);
                frameNoMore.removeAllViews();
                frameNoMore.addView(view);
            }

            if (footerConfig.failLayout != null) {
                View view = inflater.inflate(footerConfig.failLayout, frameFail, false);
                frameFail.removeAllViews();
                frameFail.addView(view);
            }

            if (footerConfig.emptySetLayout != null) {
                View view = inflater.inflate(footerConfig.emptySetLayout, frameEmptySet, false);
                frameEmptySet.removeAllViews();
                frameEmptySet.addView(view);
            }

            if (footerConfig.emptySetLayoutHandler != null) {
                footerConfig.emptySetLayoutHandler.handleLayout(frameEmptySet);
            }

        }

        void onBind() {

            int mFooterState = getFooterState();

            if (mFooterState == FOOTER_EMPTY_SET) {
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.width = -1;
                params.height = -1;
            } else {
                ViewGroup.LayoutParams params = itemView.getLayoutParams();
                params.width = -1;
                params.height = -2;
            }


            frameNoMore.setVisibility(mFooterState == FOOTER_NO_MORE ? View.VISIBLE : View.GONE);
            frameLoading.setVisibility(mFooterState == FOOTER_LOADING ? View.VISIBLE : View.GONE);
            frameFail.setVisibility(mFooterState == FOOTER_RETRY ? View.VISIBLE: View.GONE);
            frameEmptySet.setVisibility(mFooterState == FOOTER_EMPTY_SET ? View.VISIBLE : View.GONE);

        }
    }

}
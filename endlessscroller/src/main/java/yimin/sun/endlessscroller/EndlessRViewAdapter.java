package yimin.sun.endlessscroller;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by yzsh-sym on 2017/2/22.
 */

public abstract class EndlessRViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> /*implements IEndlessRViewAdapter*/ {

    public static final int FOOTER_INVISIBLE        = 100;
    public static final int FOOTER_LOADING          = 101;
    public static final int FOOTER_NO_MORE          = 102;
    public static final int FOOTER_EMPTY_SET        = 103;
    public static final int FOOTER_RETRY            = 104;
    public static final int FOOTER_FAIL             = 105;

    private static final int FOOTER_DEBUG           = -1;
    private static final int DEF_FIRST_PAGE_NUM = 1;
    private static final int DEF_PAGE_SIZE = 20;

    public static final int TOP = 0;
    public static final int BOTTOM = 1;

    private int mFooterState = FOOTER_INVISIBLE;
    private View.OnClickListener retryListener;
    private EndlessRViewAdapterConfig endlessRViewAdapterConfig;

    private static EndlessRViewAdapterConfig globalConfig = null;

    int getFooterState() {
        return mFooterState;
    }

    private void setFooterState(int state) {
        mFooterState = state;
    }

    private void setFooterRetryListener(View.OnClickListener clickListener) {
        retryListener = clickListener;
    }

    /*public View.OnClickListener getFooterRetryListener() {
        return retryListener;
    }*/

    public void setFooterStateToDebug() {
        mFooterState = FOOTER_DEBUG;
    }

    /**
     * 底部加载的目标数据的当前长度，用于计算当前page
     */
    abstract public int getDataSetSize();

    /**
     * 改变footer样式至加载中，一般来说是显示一个环形progress bar，注意需要在UI线程调用本方法
     */
    public void loading() {
        setFooterState(FOOTER_LOADING);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(getItemCount() - 1);
            }
        });
    }

    /**
     * 改变footer样式至加载成功，一般来说就是隐藏footer。注意这里仅仅改变footer样式，不会操控数据，刷新列表。
     *
     * @param refreshPosition {@link #BOTTOM} or {@link #TOP} 顶部刷新和底部加载逻辑有区别，所以需要知道这个信息
     * @param numFetched 获取到的数据数量
     */
    public void loadSuccess(int refreshPosition, /*int numRequested,*/ int numFetched) {

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

            if (numFetched < getPageSize()) {
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
     */
    public void loadFail(final EndlessScroller.Retryable retryImpl) {
        if (retryImpl != null) {
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
        } else {
            setFooterState(FOOTER_FAIL);
            notifyItemChanged(getItemCount() - 1);
        }
    }

    /**
     * 设置实例config，将会覆盖global config
     */
    public void setConfig(EndlessRViewAdapterConfig endlessRViewAdapterConfig) {
        this.endlessRViewAdapterConfig = endlessRViewAdapterConfig;
    }

    public int getFirstPageNum() {
        Integer num = getConfig().firstPageNum;
        return num == null ? DEF_FIRST_PAGE_NUM : num;
    }

    public int getPageSize() {
        Integer size = getConfig().pageSize;
        return size == null ? DEF_PAGE_SIZE : size;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_FOOTER) {
            Context context = parent.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.item_footer, parent, false);
            return new FooterVH(view);
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

        FrameLayout frameLoading, frameNoMore, frameRetry, frameFail, frameEmptySet;

        FooterVH(View itemView) {
            super(itemView);

            frameLoading = (FrameLayout) itemView.findViewById(R.id.frame_loading);
            frameNoMore = (FrameLayout) itemView.findViewById(R.id.frame_no_more);
            frameRetry = (FrameLayout) itemView.findViewById(R.id.frame_retry);
            frameFail = (FrameLayout) itemView.findViewById(R.id.frame_fail);
            frameEmptySet = (FrameLayout) itemView.findViewById(R.id.frame_empty_set);

            Context context = itemView.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            if (getConfig().loadingLayout != null) {
                View view = inflater.inflate(getConfig().loadingLayout, frameLoading, false);
                frameLoading.removeAllViews();
                frameLoading.addView(view);
            }

            if (getConfig().noMoreLayout != null) {
                View view = inflater.inflate(getConfig().noMoreLayout, frameNoMore, false);
                frameNoMore.removeAllViews();
                frameNoMore.addView(view);
            }

            if (getConfig().failLayout != null) {
                View view = inflater.inflate(getConfig().failLayout, frameFail, false);
                frameFail.removeAllViews();
                frameFail.addView(view);
            }

            if (getConfig().retryLayout != null) {
                View view = inflater.inflate(getConfig().retryLayout, frameRetry, false);
                frameRetry.removeAllViews();
                frameRetry.addView(view);
            }

            if (getConfig().emptySetLayout != null) {
                View view = inflater.inflate(getConfig().emptySetLayout, frameEmptySet, false);
                frameEmptySet.removeAllViews();
                frameEmptySet.addView(view);
            }

            if (getConfig().emptySetLayoutHandler != null) {
                getConfig().emptySetLayoutHandler.handleLayout(frameEmptySet);
            }

        }

        void onBind() {

            int mFooterState = getFooterState();
            if (mFooterState != FOOTER_DEBUG) {
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
                frameFail.setVisibility(mFooterState == FOOTER_FAIL ? View.VISIBLE : View.GONE);
                frameRetry.setVisibility(mFooterState == FOOTER_RETRY ? View.VISIBLE : View.GONE);
                frameEmptySet.setVisibility(mFooterState == FOOTER_EMPTY_SET ? View.VISIBLE : View.GONE);
            } else {
                frameNoMore.setVisibility(View.VISIBLE);
                frameLoading.setVisibility(View.VISIBLE);
                frameFail.setVisibility(View.VISIBLE);
                frameRetry.setVisibility(View.VISIBLE);
                frameEmptySet.setVisibility(View.VISIBLE);
            }

            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            if (params instanceof StaggeredGridLayoutManager.LayoutParams) {
                ((StaggeredGridLayoutManager.LayoutParams)params).setFullSpan(true);
            }

        }
    }

    private EndlessRViewAdapterConfig getConfig() {
        if (endlessRViewAdapterConfig != null) {
            return endlessRViewAdapterConfig;
        } else if (globalConfig != null) {
            return globalConfig;
        } else {
            return new EndlessRViewAdapterConfig();
        }
    }

    /**
     * 一般来说app内部所有adapter应共享大多数设置，如firstPageNum 等，通过本方法统一设置全局，建议在Application onCreate调用
     */
    public static void setGlobalConfig(EndlessRViewAdapterConfig config) {
        globalConfig = config;
    }

//    interface IEndlessRViewAdapter {
//
//        int FOOTER_INVISIBLE        = 100;
//        int FOOTER_LOADING          = 101;
//        int FOOTER_NO_MORE          = 102;
//        int FOOTER_EMPTY_SET        = 103;
//        int FOOTER_RETRY            = 104;
//
//        /**
//         * adapter总元素数，用于计算是否触发load more事件，注意，由于此方法与recycler view adapter中的getItemCount方法相同，
//         * 所以当继承recycler adapter并实现本接口时（绝大多数情况都是这样用），此方法自动实现
//         */
//        int getItemCount();
//
//        /**
//         * load more的目标数据元素数，用于计算当前page
//         */
//        int getDataSetSize();
//
//        int getFooterState();
//
//        void setFooterState(int state);
//
//        View.OnClickListener getFooterRetryListener();
//
//        void setFooterRetryListener(View.OnClickListener clickListener);
//
//    }

}
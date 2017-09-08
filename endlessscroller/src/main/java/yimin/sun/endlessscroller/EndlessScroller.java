package yimin.sun.endlessscroller;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import java.util.Locale;

/**
 * Created by yzsh-sym on 2017/9/7.
 */

public class EndlessScroller extends RecyclerView.OnScrollListener {

    public interface Retryable {
        void retry();
    }

    public interface ILoadMoreListener {
        void onLoadMore(int pageToLoad);
    }

    private static final String TAG = EndlessScroller.class.getSimpleName();

    @SuppressWarnings("FieldCanBeLocal")
    private final boolean showLog = true;

    private RecyclerView.LayoutManager mLayoutManager;

    private int visibleThreshold = 3;

    private int startPage = 1;

    private int pageSize = 10;

    private ILoadMoreListener loadMoreListener;


    public EndlessScroller(RecyclerView.LayoutManager layoutManager, int startPage, int pageSize, ILoadMoreListener loadMoreHandler) {
        this.mLayoutManager = layoutManager;
        this.startPage = startPage;
        this.pageSize = pageSize;
        this.loadMoreListener = loadMoreHandler;
        if (mLayoutManager instanceof GridLayoutManager) {
            visibleThreshold = visibleThreshold * ((GridLayoutManager)mLayoutManager).getSpanCount();
        } else if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            visibleThreshold = visibleThreshold * ((StaggeredGridLayoutManager)mLayoutManager).getSpanCount();
        }
    }

    public EndlessScroller(ILoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    private void init(RecyclerView view) {
        mLayoutManager = view.getLayoutManager();
        EndlessRViewAdapter adapter = ((EndlessRViewAdapter) view.getAdapter());
        startPage = adapter.getFirstPageNum();
        pageSize = adapter.getPageSize();
        if (mLayoutManager instanceof GridLayoutManager) {
            visibleThreshold = visibleThreshold * ((GridLayoutManager)mLayoutManager).getSpanCount();
        } else if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            visibleThreshold = visibleThreshold * ((StaggeredGridLayoutManager)mLayoutManager).getSpanCount();
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {

        RecyclerView.Adapter temp = view.getAdapter();

        if ( !(temp instanceof EndlessRViewAdapter)) {
            throw new RuntimeException("Adapter must extend EndlessRViewAdapter");
        }

        if (mLayoutManager == null) {
            init(view);
        }

        EndlessRViewAdapter adapter = ((EndlessRViewAdapter) temp);
        int footerState = adapter.getFooterState();
        boolean allow = footerState == EndlessRViewAdapter.FOOTER_INVISIBLE || footerState == EndlessRViewAdapter.FOOTER_FAIL;

        if (showLog) {
            Log.d(TAG, String.format(Locale.US, "[EndlessScroller] onScrolled, RecyclerView scroll state = %d, adapter footer state = %d",
                    view.getScrollState(), footerState));
        }

        if (allow && view.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {

            int lastVisibleItemPosition = 0;

            if (mLayoutManager instanceof StaggeredGridLayoutManager) {
                int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
                lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);

            } else if (mLayoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();

            } else if (mLayoutManager instanceof LinearLayoutManager) {
                lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();

            }

            if (showLog) {
                Log.d(TAG, String.format(Locale.US, "[EndlessScroller] lastVisible = %d, threshold = %d, total = %d",
                        lastVisibleItemPosition, visibleThreshold, adapter.getItemCount()));
            }

            if (lastVisibleItemPosition + 1 + visibleThreshold >= adapter.getItemCount()) {

                if (showLog) {
                    Log.d(TAG, String.format(Locale.US, "[EndlessScroller] Bingo! EndlessScroller is loading more, lastVisible = %d, threshold = %d, total = %d",
                            lastVisibleItemPosition, visibleThreshold, adapter.getItemCount()));
                }

                int curPage = calculateCurrentPage(adapter.getDataSetSize());
                loadMoreListener.onLoadMore(curPage + 1);
            }
        }
    }

    private int calculateCurrentPage(int itemCount) {
        if (itemCount <= 0) {
            return startPage - 1; //当数据项数为0时，令load more加载第一页
        } else {
            return (itemCount - 1) / pageSize + startPage;
        }

    }

    // StaggeredLayoutManager findLastVisible方法返回一组index，分别是每个span的last visible项，
    // 本方法在这组index中找出最大值
    private int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

}

package yimin.sun.endlessscroller;

import android.view.View;

/**
 * Created by yzsh-sym on 2017/9/7.
 */

public interface IEndlessRViewAdapter {

    int FOOTER_INVISIBLE        = 100;
    int FOOTER_LOADING          = 101;
    int FOOTER_NO_MORE          = 102;
    int FOOTER_EMPTY_SET        = 103;
    int FOOTER_RETRY            = 104;
    
    /**
     * adapter总元素数，用于计算是否触发load more事件，注意，由于此方法与recycler view adapter中的getItemCount方法相同，
     * 所以当继承recycler adapter并实现本接口时（绝大多数情况都是这样用），此方法自动实现
     */
    int getItemCount();

    /**
     * load more的目标数据元素数，用于计算当前page
     */
    int getDataSetSize();

    int getFooterState();

    void setFooterState(int state);

    View.OnClickListener getFooterRetryListener();

    void setFooterRetryListener(View.OnClickListener clickListener);


    //-------

//    void setFooterToLoading();
//
//    void setFooterToSuccess();
//
//    void setFooterToFail();


}

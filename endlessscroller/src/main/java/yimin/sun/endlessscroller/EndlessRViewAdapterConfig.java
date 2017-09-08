package yimin.sun.endlessscroller;

import yimin.sun.endlessscroller.layouthandler.ILayoutHandler;

/**
 * Created by yzsh-sym on 2017/9/7.
 */

public class EndlessRViewAdapterConfig {

    Integer firstPageNum;
    Integer pageSize;

    Integer loadingLayout;
    Integer noMoreLayout;
    Integer failLayout;
    Integer retryLayout;
    Integer emptySetLayout;
    ILayoutHandler emptySetLayoutHandler;


    public EndlessRViewAdapterConfig setFirstPageNum(Integer firstPageNum) {
        this.firstPageNum = firstPageNum;
        return this;
    }

    public EndlessRViewAdapterConfig setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public EndlessRViewAdapterConfig setLoadingLayout(Integer loadingLayout) {
        this.loadingLayout = loadingLayout;
        return this;
    }

    public EndlessRViewAdapterConfig setNoMoreLayout(Integer noMoreLayout) {
        this.noMoreLayout = noMoreLayout;
        return this;
    }

    public EndlessRViewAdapterConfig setFailLayout(Integer failLayout) {
        this.failLayout = failLayout;
        return this;
    }

    public EndlessRViewAdapterConfig setRetryLayout(Integer retryLayout) {
        this.retryLayout = retryLayout;
        return this;
    }

    public EndlessRViewAdapterConfig setEmptySetLayout(Integer emptySetLayout) {
        this.emptySetLayout = emptySetLayout;
        return this;
    }

    public EndlessRViewAdapterConfig setEmptySetLayoutHandler(ILayoutHandler emptySetLayoutHandler) {
        this.emptySetLayoutHandler = emptySetLayoutHandler;
        return this;
    }
}
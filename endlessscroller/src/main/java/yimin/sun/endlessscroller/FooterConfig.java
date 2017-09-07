package yimin.sun.endlessscroller;

/**
 * Created by yzsh-sym on 2017/9/7.
 */

public class FooterConfig {
    Integer loadingLayout;
    Integer noMoreLayout;
    Integer failLayout;
    Integer emptySetLayout;
    ILayoutHandler emptySetLayoutHandler;

    public FooterConfig setLoadingLayout(Integer loadingLayout) {
        this.loadingLayout = loadingLayout;
        return this;
    }

    public FooterConfig setNoMoreLayout(Integer noMoreLayout) {
        this.noMoreLayout = noMoreLayout;
        return this;
    }

    public FooterConfig setFailLayout(Integer failLayout) {
        this.failLayout = failLayout;
        return this;
    }

    public FooterConfig setEmptySetLayout(Integer emptySetLayout) {
        this.emptySetLayout = emptySetLayout;
        return this;
    }

    public FooterConfig setEmptySetLayoutHandler(ILayoutHandler emptySetLayoutHandler) {
        this.emptySetLayoutHandler = emptySetLayoutHandler;
        return this;
    }
}
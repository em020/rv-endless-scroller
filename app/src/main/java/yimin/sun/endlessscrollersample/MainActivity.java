package yimin.sun.endlessscrollersample;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import yimin.sun.endlessscroller.EndlessRViewAdapter;
import yimin.sun.endlessscroller.EndlessRViewAdapter3;
import yimin.sun.endlessscroller.EndlessScroller;
import yimin.sun.endlessscroller.EndlessRViewAdapterConfig;
import yimin.sun.handyactionbar.HandyActionBarLayout;

public class MainActivity extends AppCompatActivity implements EndlessScroller.ILoadMoreListener {

    interface Callback2 {
        void onCall(int status, Object obj);
    }

    SampleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HandyActionBarLayout hab = (HandyActionBarLayout) findViewById(R.id.ab);
        hab.setLeftAsBack(this);
        hab.setXWithText(HandyActionBarLayout.POSITION_M, "EndlessScrollerSample", null);
        hab.addToR1WithText("top refresh", new Runnable() {
            @Override
            public void run() {
                topRefresh();
            }
        });
        hab.addToR1WithText("clear and top refresh", new Runnable() {
            @Override
            public void run() {
                adapter.data = new ArrayList<>();
                adapter.notifyDataSetChanged();
                topRefresh();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnScrollListener(new EndlessScroller(this));

        // global config，对app全局adapter都生效
        EndlessRViewAdapter.setGlobalConfig(new EndlessRViewAdapterConfig()
                .setFirstPageNum(2)
                .setPageSize(14));

        adapter = new SampleAdapter();

        EndlessRViewAdapterConfig endlessRViewAdapterConfig = new EndlessRViewAdapterConfig()
                .setPageSize(10)
                .setFirstPageNum(0)
//                .setFailLayout(R.layout.footer_loading)
//                .setRetryLayout(R.layout.footer_loading)
//                .setLoadingLayout(R.layout.footer_loading)
//                .setNoMoreLayout(R.layout.footer_loading)
//                .setEmptySetLayout(R.layout.footer_loading)
//                .setEmptySetLayoutHandler(new DefaultEmptySetLayoutHandler(android.R.drawable.ic_media_play, "ah uh..."))
//                .setEmptySetLayoutHandler(new ILayoutHandler() {
//                    @Override
//                    public void handleLayout(View view) {
//                        TextView tv = (TextView) view.findViewById(R.id.text);
//                        tv.setText("hell yeah");
//                    }
//                })
                ;
        adapter.setConfig(endlessRViewAdapterConfig);

        recyclerView.setAdapter(adapter);

        topRefresh();
    }

    @Override
    public void onLoadMore(int pageToLoad) {
        adapter.loading();
        fakeApiRequest(pageToLoad, adapter.getPageSize(), new Callback2() {
            @Override
            public void onCall(int status, Object obj) {
                if (status == 1) {
                    List<Integer> data = (List<Integer>) obj;
                    adapter.loadSuccess(EndlessRViewAdapter.BOTTOM, data);
                } else {
                    adapter.loadFail(null);
                }
            }
        });
    }

    private void topRefresh() {
        adapter.loading();
        //请求第一页
        fakeApiRequest(adapter.getFirstPageNum(), adapter.getPageSize(), new Callback2() {
            @Override
            public void onCall(int status, Object obj) {
                if (status == 1) {
                    List<Integer> data = (List<Integer>) obj;
                    adapter.loadSuccess(EndlessRViewAdapter.TOP, data);
                } else {
                    adapter.loadFail(null);
                }
            }
        });
    }

    private void fakeApiRequest(final int page, final int pageSize, final Callback2 callback) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //生成一个20以内的随机数，根据其值随机返回成功，成功尾页，失败情况
                int randNum = new Random().nextInt(20);

                Log.d("edmund", "randNum = " + randNum);

                if (randNum < 4) {
                    // 成功，返回不足page size的数据，假装尾页
                    List<Integer> fakeData = new ArrayList<Integer>();
                    for (int i = (page - 1) * pageSize; i < (page - 1) * pageSize + randNum; i++) {
                        fakeData.add(i);
                    }
                    callback.onCall(1, fakeData);

                } else if (randNum < 15) {
                    // 成功
                    List<Integer> fakeData = new ArrayList<Integer>();
                    for (int i = (page - 1) * pageSize; i < (page) * pageSize; i++) {
                        fakeData.add(i);
                    }
                    callback.onCall(1, fakeData);
                } else {
                    // 失败
                    callback.onCall(-2, null);
                }
            }
        }, 2000);
    }


    private class SampleAdapter extends EndlessRViewAdapter3<Integer> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder2(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder2(RecyclerView.ViewHolder holder, int position) {
            ((VH)holder).tv1.setText("hell");
            ((VH)holder).tv2.setText("" + data.get(position));
        }

        @Override
        public int getItemCount2() {
            return getDataSetSize();
        }

        @Override
        public int getItemViewType2(int position) {
            return 0;
        }


        class VH extends RecyclerView.ViewHolder {

            TextView tv1, tv2;

            VH(View itemView) {
                super(itemView);
                tv1 = (TextView) itemView.findViewById(android.R.id.text1);
                tv2 = (TextView) itemView.findViewById(android.R.id.text2);
            }
        }
    }
}

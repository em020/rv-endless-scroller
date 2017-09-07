package yimin.sun.endlessscrollersample;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import yimin.sun.endlessscroller.DefaultEmptySetLayoutHandler;
import yimin.sun.endlessscroller.EndlessRViewAdapter;
import yimin.sun.endlessscroller.FooterConfig;
import yimin.sun.endlessscroller.IEndlessRViewAdapter;
import yimin.sun.endlessscroller.ILayoutHandler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FooAdapter adapter = new FooAdapter();
        adapter.setFooterState(IEndlessRViewAdapter.FOOTER_EMPTY_SET);
        FooterConfig footerConfig = new FooterConfig()
//                .setEmptySetLayout(R.layout.footer_loading)
//                .setFailLayout(R.layout.footer_loading)
//                .setLoadingLayout(R.layout.footer_loading)
//                .setNoMoreLayout(R.layout.footer_loading)
//                .setEmptySetLayoutHandler(new DefaultEmptySetLayoutHandler(android.R.drawable.ic_media_play, "ah uh..."))
//                .setEmptySetLayoutHandler(new ILayoutHandler() {
//                    @Override
//                    public void handleLayout(View view) {
//                        TextView tv = (TextView) view.findViewById(R.id.text);
//                        tv.setText("hell yeah");
//                    }
//                })
                ;
        adapter.setFooterConfig(footerConfig);
        recyclerView.setAdapter(adapter);


    }



    class FooAdapter extends EndlessRViewAdapter {

        List<Integer> data;

        @Override
        public int getDataSetSize() {
            return data == null ? 0 : data.size();
        }

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

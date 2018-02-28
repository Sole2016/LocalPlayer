package com.zy.vplayer.tv.simple;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

/**
 * @author ZhiTouPC
 */
public class SimpleMainFragment extends BrowseFragment {
    private static final String TAG = "SimpleMainFragment";
    private RecyclerView mRecyclerView;
    public static final String INTERFACE = TAG + "Simple";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupElements();
        setOnSearchClickedListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "click search", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ArrayObjectAdapter mArrayObjectAdapter;

    private void setupElements() {
        setTitle("title is show");
        BackgroundManager instance = BackgroundManager.getInstance(getActivity());
        instance.attach(getActivity().getWindow());
        instance.setDrawable(new ColorDrawable(Color.BLUE));

        ListRowPresenter listRowPresenter = new ListRowPresenter();
        listRowPresenter.setNumRows(2);

        mArrayObjectAdapter = new ArrayObjectAdapter(listRowPresenter);

        HeaderItem headerItem = new HeaderItem(0, "SimplePresenter0");
        //自定义adapterItem布局
        SimplePresenter presenter = new SimplePresenter();

        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(presenter);
        for (int i = 0; i < 15; i++) {
            gridRowAdapter.add(i + "");
        }
        mArrayObjectAdapter.add(new ListRow(headerItem, gridRowAdapter));

        HeaderItem headerItem1 = new HeaderItem(1, "SimplePresenter1");

        ArrayObjectAdapter gridRowAdapter1 = new ArrayObjectAdapter(presenter);

        for (int i = 0; i < 30; i++) {
            gridRowAdapter1.add(i + "");
        }

        mArrayObjectAdapter.add(new ListRow(headerItem1, gridRowAdapter1));

        setAdapter(mArrayObjectAdapter);

        setHeadersState(HEADERS_ENABLED);

        setHeadersTransitionOnBackEnabled(true);
        //设置左边导航背景颜色
        setBrandColor(Color.GRAY);
        //设置搜索图标的背景颜色
        setSearchAffordanceColor(Color.WHITE);

    }

}

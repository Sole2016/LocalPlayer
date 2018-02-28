package com.zy.vplayer.tv.simple;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.widget.Toast;

public class SimpleImageCardFragment extends BrowseFragment{

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTitle("title is show");
        ArrayObjectAdapter arrayObjectAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        HeaderItem headerItem = new HeaderItem("SimpleHeadItem");
        CardPresenter cardPresenter = new CardPresenter();
        ArrayObjectAdapter gridAdapter = new ArrayObjectAdapter(cardPresenter);

        gridAdapter.add("");
        gridAdapter.add("");
        gridAdapter.add("");
        gridAdapter.add("");
        gridAdapter.add("");


        arrayObjectAdapter.add(new ListRow(headerItem,gridAdapter));

        setAdapter(arrayObjectAdapter);

        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder,
                                      Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
                Toast.makeText(getActivity(),String.valueOf(row.getId()),Toast.LENGTH_SHORT).show();
            }
        });
        setOnItemViewSelectedListener(new SimpleItemSelectedListener());
    }

    private static class SimpleItemSelectedListener implements OnItemViewSelectedListener {

        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder,
                                   Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            System.out.println("itemSelected="+row.getId());
        }
    }
}

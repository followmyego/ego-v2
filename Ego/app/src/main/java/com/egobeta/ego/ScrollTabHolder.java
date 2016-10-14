package com.egobeta.ego;

import android.widget.AbsListView;

/**
 * Created by Lucas on 06/10/2016.
 */
public interface ScrollTabHolder {

    void adjustScroll(int scrollHeight);

    void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition);

}

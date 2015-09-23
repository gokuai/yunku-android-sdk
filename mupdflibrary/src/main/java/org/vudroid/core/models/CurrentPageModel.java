package org.vudroid.core.models;

import org.vudroid.core.events.PageChangeListener;
import org.vudroid.core.events.EventDispatcher;

public class CurrentPageModel extends EventDispatcher {
    private int currentPageIndex;

    public void setCurrentPageIndex(int currentPageIndex, int total) {
        if (this.currentPageIndex != currentPageIndex) {
            this.currentPageIndex = currentPageIndex;
            dispatch(new PageChangeListener.PageChangeEvent(currentPageIndex, total));
        }
    }

}

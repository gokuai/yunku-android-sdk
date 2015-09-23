package org.vudroid.core.events;

public interface PageChangeListener {
    void onPageChanged(int index, int total);

    class PageChangeEvent extends SafeEvent<PageChangeListener> {
        private final int pageIndex;
        private final int total;

        public PageChangeEvent(int pageIndex, int total) {
            this.pageIndex = pageIndex;
            this.total = total;
        }

        @Override
        public void dispatchSafely(PageChangeListener listener) {
            listener.onPageChanged(pageIndex, total);
        }
    }
}

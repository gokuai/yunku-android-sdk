package org.vudroid.core.events;

public interface SingleTapListener {
    void onMainViewSingleTap();

    class SingleTapEvent extends SafeEvent<SingleTapListener> {

        public SingleTapEvent() {
        }

        @Override
        public void dispatchSafely(SingleTapListener listener) {
            listener.onMainViewSingleTap();
        }
    }
}

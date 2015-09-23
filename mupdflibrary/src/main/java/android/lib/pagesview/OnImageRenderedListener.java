package android.lib.pagesview;

import android.graphics.Bitmap;

import java.util.Map;

/**
 * Allow renderer to notify view that new bitmaps are ready.
 * Implemented by PagesView.
 */
public interface OnImageRenderedListener {
	void onImagesRendered(Map<Tile, Bitmap> renderedImages);
	void onRenderingException(RenderingException reason);
}

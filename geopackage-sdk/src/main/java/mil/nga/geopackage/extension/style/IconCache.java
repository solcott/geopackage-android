package mil.nga.geopackage.extension.style;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.LruCache;

import mil.nga.geopackage.io.BitmapConverter;

/**
 * Icon Cache of icon bitmaps
 *
 * @author osbornb
 * @since 3.1.1
 */
public class IconCache {

    /**
     * Default max number of icon bitmaps to retain in cache
     */
    public static final int DEFAULT_CACHE_SIZE = 100;

    /**
     * Icon bitmap cache
     */
    private final LruCache<Long, Bitmap> iconCache;

    /**
     * Constructor, created with cache size of {@link #DEFAULT_CACHE_SIZE}
     */
    public IconCache() {
        this(DEFAULT_CACHE_SIZE);
    }

    /**
     * Constructor
     *
     * @param size max icon bitmaps to retain in the cache
     */
    public IconCache(int size) {
        iconCache = new LruCache<>(size);
    }

    /**
     * Get the cached bitmap for the icon row or null if not cached
     *
     * @param iconRow icon row
     * @return icon bitmap or null
     */
    public Bitmap get(IconRow iconRow) {
        return get(iconRow.getId());
    }

    /**
     * Get the cached bitmap for the icon row id or null if not cached
     *
     * @param iconRowId icon row id
     * @return icon bitmap or null
     */
    public Bitmap get(long iconRowId) {
        return iconCache.get(iconRowId);
    }

    /**
     * Cache the icon bitmap for the icon row
     *
     * @param iconRow icon row
     * @param bitmap  icon bitmap
     * @return previous cached icon bitmap or null
     */
    public Bitmap put(IconRow iconRow, Bitmap bitmap) {
        return put(iconRow.getId(), bitmap);
    }

    /**
     * Cache the icon bitmap for the icon row id
     *
     * @param iconRowId icon row id
     * @param bitmap    icon bitmap
     * @return previous cached icon bitmap or null
     */
    public Bitmap put(long iconRowId, Bitmap bitmap) {
        return iconCache.put(iconRowId, bitmap);
    }

    /**
     * Remove the cached bitmap for the icon row
     *
     * @param iconRow icon row
     * @return removed icon bitmap or null
     */
    public Bitmap remove(IconRow iconRow) {
        return remove(iconRow.getId());
    }

    /**
     * Remove the cached bitmap for the icon row id
     *
     * @param iconRowId icon row id
     * @return removed icon bitmap or null
     */
    public Bitmap remove(long iconRowId) {
        return iconCache.remove(iconRowId);
    }

    /**
     * Clear the cache
     */
    public void clear() {
        iconCache.evictAll();
    }

    /**
     * Create or retrieve from cache an icon bitmap for the icon row
     *
     * @param icon    icon row
     * @param density display density: {@link android.util.DisplayMetrics#density}
     * @return icon bitmap
     */
    public Bitmap createIcon(IconRow icon, float density) {
        return createIcon(icon, density, this);
    }

    /**
     * Create an icon bitmap for the icon row without caching
     *
     * @param icon    icon row
     * @param density display density: {@link android.util.DisplayMetrics#density}
     * @return icon bitmap
     */
    public static Bitmap createIconNoCache(IconRow icon, float density) {
        return createIcon(icon, density, null);
    }

    /**
     * Create or retrieve from cache an icon bitmap for the icon row
     *
     * @param icon      icon row
     * @param density   display density: {@link android.util.DisplayMetrics#density}
     * @param iconCache icon cache
     * @return icon bitmap
     */
    public static Bitmap createIcon(IconRow icon, float density, IconCache iconCache) {

        Bitmap iconImage = null;

        if (icon != null) {

            if (iconCache != null) {
                iconImage = iconCache.get(icon.getId());
            }

            if (iconImage == null) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeByteArray(icon.getData(), 0, icon.getData().length, options);
                int dataWidth = options.outWidth;
                int dataHeight = options.outHeight;

                double styleWidth = dataWidth;
                double styleHeight = dataHeight;

                double widthDensity = DisplayMetrics.DENSITY_DEFAULT;
                double heightDensity = DisplayMetrics.DENSITY_DEFAULT;

                if (icon.getWidth() != null) {
                    styleWidth = icon.getWidth();
                    double widthRatio = dataWidth / styleWidth;
                    widthDensity *= widthRatio;
                    if (icon.getHeight() == null) {
                        heightDensity = widthDensity;
                    }
                }

                if (icon.getHeight() != null) {
                    styleHeight = icon.getHeight();
                    double heightRatio = dataHeight / styleHeight;
                    heightDensity *= heightRatio;
                    if (icon.getWidth() == null) {
                        widthDensity = heightDensity;
                    }
                }

                options = new BitmapFactory.Options();
                options.inDensity = (int) (Math.min(widthDensity, heightDensity) + 0.5f);
                options.inTargetDensity = (int) (DisplayMetrics.DENSITY_DEFAULT * density + 0.5f);

                iconImage = BitmapConverter.toBitmap(icon
                        .getData(), options);

                if (widthDensity != heightDensity) {

                    int width = (int) (styleWidth * density + 0.5f);
                    int height = (int) (styleHeight * density + 0.5f);

                    if (width != iconImage.getWidth() || height != iconImage.getHeight()) {
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(iconImage, width, height, false);
                        iconImage.recycle();
                        iconImage = scaledBitmap;
                    }

                }

                if (iconCache != null) {
                    iconCache.put(icon.getId(), iconImage);
                }
            }

        }

        return iconImage;
    }

}
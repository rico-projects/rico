package dev.rico.core.http;

/**
 * Defines if a download (see {@link DownloadInputStream}) has a determined size
 * ({@link #NORMAL}) or not ({@link #INDETERMINATE}).
 */
public enum DownloadType {

    INDETERMINATE, NORMAL;
}

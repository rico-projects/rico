package dev.rico.core.http;

/**
 * Defines if a download (see {@link DownloadInputStream}) knows the final size of the download
 * ({@link #NORMAL}) or not ({@link #INDETERMINATE})
 */
public enum DownloadType {

    INDETERMINATE, NORMAL;
}

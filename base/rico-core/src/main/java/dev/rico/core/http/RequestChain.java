package dev.rico.core.http;

import java.io.IOException;
import java.net.HttpURLConnection;

public interface RequestChain {

    HttpURLConnection call() throws IOException;

}

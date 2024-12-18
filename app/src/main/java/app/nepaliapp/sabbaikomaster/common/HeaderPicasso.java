package app.nepaliapp.sabbaikomaster.common;


import android.content.Context;
import android.util.Log;

import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;

public class HeaderPicasso {

    private static boolean isInitialized = false;

    public static void initializePicassoWithHeaders(Context context, String headerName, String headerValue) {
        if (!isInitialized) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        okhttp3.Request.Builder requestBuilder = chain.request().newBuilder()
                                .addHeader(headerName, headerValue);
                        return chain.proceed(requestBuilder.build());
                    })
                    .build();

            Picasso picasso = new Picasso.Builder(context)
                    .downloader(new OkHttp3Downloader(client))
                    .build();

            try {
                Picasso.setSingletonInstance(picasso);
                isInitialized = true;
            } catch (IllegalStateException e) {
                // Picasso instance already exists; log or handle this case as needed
                Log.w("HeaderPicasso", "Picasso instance already exists, skipping initialization.");
            }
        }
    }

}
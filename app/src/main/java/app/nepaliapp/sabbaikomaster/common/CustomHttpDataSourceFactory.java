package app.nepaliapp.sabbaikomaster.common;

import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.HttpDataSource;

import java.util.Map;

@UnstableApi
public class CustomHttpDataSourceFactory implements HttpDataSource.Factory {
    private final String jwtToken;

    public CustomHttpDataSourceFactory(String jwtToken) {
        this.jwtToken = jwtToken;
    }
    @Override
    public HttpDataSource createDataSource() {
        DefaultHttpDataSource dataSource = new DefaultHttpDataSource.Factory().createDataSource();
        dataSource.setRequestProperty("Authorization", "Bearer " + jwtToken);
        return dataSource;
    }

    @Override
    public HttpDataSource.Factory setDefaultRequestProperties(Map<String, String> defaultRequestProperties) {
        return null;
    }
}

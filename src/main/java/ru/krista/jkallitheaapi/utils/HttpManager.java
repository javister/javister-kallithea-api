package ru.krista.jkallitheaapi.utils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

/**
 * Менеджер установки HTTP-соединений.
 */
public final class HttpManager {

    private static final Logger LOGGER = Logger.getLogger(HttpManager.class.getName());
    // 10 минут
    private static final int CONNECTION_TIMEOUT = 600_000;

    private HttpManager() {
        //
    }

    private static CloseableHttpClient createHttpClient() {
        try {
            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(CONNECTION_TIMEOUT)
                    .setConnectionRequestTimeout(CONNECTION_TIMEOUT)
                    .setSocketTimeout(CONNECTION_TIMEOUT)
                    .build();
            SSLContext sslContext = new SSLContextBuilder()
                    .loadTrustMaterial(null, (certificate, authType) -> true).build();
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier());
            HttpsURLConnection.setDefaultHostnameVerifier((string, ssls) -> true);
            return HttpClients.custom()
                    .setSSLSocketFactory(sslConnectionSocketFactory)
                    .setDefaultRequestConfig(config)
                    .build();
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
            LOGGER.log(Level.SEVERE, null, e);
            throw new IllegalStateException(String.format("Ошибка создания менеджера закачек: %s", e.getMessage()), e);
        }
    }

    /**
     * Получает контетн по URL'у.
     * @param url url.
     * @return контент.
     */
    public static byte[] selectContent(String url) {
        try (CloseableHttpClient httpClient = createHttpClient();
                CloseableHttpResponse response = httpClient.execute(new HttpGet(url))) {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                throw new IllegalStateException(String.format("Ошибка получения страницы %s (код %d) %s",
                        url, response.getStatusLine().getStatusCode(),
                        IOUtils.toString(response.getEntity().getContent(), "utf-8")));
            }
            HttpEntity entity = response.getEntity();
            if (entity == null || entity.getContent() == null) {
                throw new IllegalStateException(String.format("По адресу %s нет контента", url));
            }
            return IOUtils.toByteArray(response.getEntity().getContent());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, null, e);
            throw new IllegalStateException(String.format("Ошибка получения информации: %s", e.getMessage()), e);
        }
    }
}

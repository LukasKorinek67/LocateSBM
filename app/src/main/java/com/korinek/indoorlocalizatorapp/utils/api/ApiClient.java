package com.korinek.indoorlocalizatorapp.utils.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.korinek.indoorlocalizatorapp.R;
import com.korinek.indoorlocalizatorapp.model.BuildingApiModel;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit;
    private static final String TEST_URL = "https://147.230.77.197/TecoApi/";

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(BuildingApiModel.class, new BuildingDeserializer())
                    .create();

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String apiUrl = sharedPreferences.getString("settings_teco_api_url", "");
            char urlEndCharacter = '/';
            if (apiUrl.isEmpty()) {
                apiUrl = TEST_URL;
            } else if (apiUrl.charAt(apiUrl.length() - 1) != urlEndCharacter) {
                apiUrl += urlEndCharacter;
            }

            try {
                retrofit = new Retrofit.Builder()
                        .baseUrl(apiUrl)
                        .client(getHttpClient(context))
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            } catch (IllegalArgumentException e) {
                apiUrl = TEST_URL;
                retrofit = new Retrofit.Builder()
                        .baseUrl(apiUrl)
                        .client(getHttpClient(context))
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
            }
        }
        return retrofit;
    }

    private static OkHttpClient getHttpClient(Context context) {
        try {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

            // Načtení certifikátu ze složky res/raw
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream certInput = context.getResources().openRawResource(R.raw.teco_api_cert);
            Certificate ca = cf.generateCertificate(certInput);
            certInput.close();

            // Uložení certifikátu do KeyStore
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry("custom_ca", ca);

            // Přidání vlastního hostname verifieru, který vše povolí
            HostnameVerifier hostnameVerifier = (hostname, session) -> true;

            // Vytvoření nového TrustManagerFactory s oběma certifikáty (systémový + vlastní)
            TrustManagerFactory combinedTmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            combinedTmf.init(keyStore);

            // Použij TrustManager, který kombinuje oba zdroje certifikátů
            X509TrustManager systemTrustManager = getSystemTrustManager();
            X509TrustManager customTrustManager = (X509TrustManager) combinedTmf.getTrustManagers()[0];

            X509TrustManager combinedTrustManager = new CombinedTrustManager(systemTrustManager, customTrustManager);

            // Konfigurace SSL
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{combinedTrustManager}, new java.security.SecureRandom());


            clientBuilder.sslSocketFactory(sslContext.getSocketFactory(), combinedTrustManager);
            clientBuilder.hostnameVerifier(hostnameVerifier);

            // Přihlašovací údaje
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean isAuthEnabled = sharedPreferences.getBoolean("settings_request_authorization", false);
            String username = sharedPreferences.getString("settings_request_authorization_username", "");
            String password = sharedPreferences.getString("settings_request_authorization_password", "");

            // Přidání interceptoru pouze pokud je isAuthEnabled true
            if (isAuthEnabled) {
                String credentials = username + ":" + password;
                String basicAuth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

                Interceptor authInterceptor = chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", basicAuth)
                            .header("Accept", "application/json")
                            .method(original.method(), original.body());

                    return chain.proceed(requestBuilder.build());
                };
                clientBuilder.addInterceptor(authInterceptor);
            }
            return clientBuilder.build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static X509TrustManager getSystemTrustManager() {
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);
            for (TrustManager tm : tmf.getTrustManagers()) {
                if (tm instanceof X509TrustManager) {
                    return (X509TrustManager) tm;
                }
            }
            throw new IllegalStateException("No X509TrustManager found");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void resetClient() {
        retrofit = null;
    }
}

package com.korinek.indoorlocalizatorapp.utils.api;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class CombinedTrustManager implements X509TrustManager {
    private final X509TrustManager systemTrustManager;
    private final X509TrustManager customTrustManager;

    public CombinedTrustManager(X509TrustManager systemTrustManager, X509TrustManager customTrustManager) {
        this.systemTrustManager = systemTrustManager;
        this.customTrustManager = customTrustManager;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            systemTrustManager.checkClientTrusted(chain, authType);
        } catch (CertificateException e) {
            customTrustManager.checkClientTrusted(chain, authType);
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            systemTrustManager.checkServerTrusted(chain, authType);
        } catch (CertificateException e) {
            customTrustManager.checkServerTrusted(chain, authType);
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        X509Certificate[] systemIssuers = systemTrustManager.getAcceptedIssuers();
        X509Certificate[] customIssuers = customTrustManager.getAcceptedIssuers();

        X509Certificate[] combined = new X509Certificate[systemIssuers.length + customIssuers.length];
        System.arraycopy(systemIssuers, 0, combined, 0, systemIssuers.length);
        System.arraycopy(customIssuers, 0, combined, systemIssuers.length, customIssuers.length);

        return combined;
    }
}

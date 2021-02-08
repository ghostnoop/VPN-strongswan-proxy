package com.fast.fastvpnsecured.app;

import android.util.Log;

import com.fast.fastvpnsecured.model.ProxyServer;

import org.strongswan.android.security.LocalCertificateKeyStoreSpi;
import org.strongswan.android.security.LocalCertificateStore;
import org.strongswan.android.security.TrustedCertificateEntry;

import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class VpnUtils {
    public static String getCaCertificate(ProxyServer proxyServer) {
        String path = "/assets/" + proxyServer.getCaCertificate() + ".pem";

        try {
            CertificateFactory certFactory;
            certFactory = CertificateFactory.getInstance("X.509");
            InputStream inputStream = VpnUtils.class.getResourceAsStream(path);
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(inputStream);
            LocalCertificateStore temp = new LocalCertificateStore();
            temp.addCertificate(cert);
            String al = new LocalCertificateKeyStoreSpi().engineGetCertificateAlias(cert);

            TrustedCertificateEntry a = new TrustedCertificateEntry(al, cert);
            return a.getAlias();

        } catch (Exception e) {
            Log.e("N@@_ca_exp",e.getMessage()+" q");
            return "";
        }
    }
}

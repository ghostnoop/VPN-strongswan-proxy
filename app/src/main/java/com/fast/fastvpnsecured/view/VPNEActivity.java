package com.fast.fastvpnsecured.view;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.fast.fastvpnsecured.R;
import com.fast.fastvpnsecured.controller.VpnUiController;
import com.fast.fastvpnsecured.model.ProxyServer;
import com.github.ybq.android.spinkit.SpinKitView;

import org.strongswan.android.data.VpnProfile;
import org.strongswan.android.data.VpnProfileDataSource;
import org.strongswan.android.data.VpnType;
import org.strongswan.android.logic.VpnStateService;
import org.strongswan.android.security.LocalCertificateKeyStoreSpi;
import org.strongswan.android.security.LocalCertificateStore;
import org.strongswan.android.security.TrustedCertificateEntry;
import org.strongswan.android.ui.VpnProfileControlActivity;

import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static com.fast.fastvpnsecured.app.UtilsKt.hardContact;
import static com.fast.fastvpnsecured.app.UtilsKt.hardPrivacy;
import static com.fast.fastvpnsecured.app.UtilsKt.hardTerms;
import static com.fast.fastvpnsecured.app.UtilsKt.setImage;

public class VPNEActivity extends AppCompatActivity implements VpnStateService.VpnStateListener {
    public final int SERVER_REQUEST = 123;

    private Button connect;
    private static final String TAG = "NAOMI";
    private VpnProfile profile;
    private boolean mVisible;
    private DrawerLayout drawer_layout;
    private Toolbar toolbar;
    private VpnUiController vpnController;
    private TextView country_tv;
    private ImageView country_img;
    private VpnProfileDataSource vpnProfileDataSource;

    private TextView status_tv;
    private TextView turn_tv;
    private ImageView turn_btn;

    private ProxyServer currentProxyServer;
    private Boolean isConnected = false;

    SpinKitView spin_kit;
    CardView location_pick_btn;
    Boolean isOpen = true;

    private VpnStateService mService;
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((VpnStateService.LocalBinder) service).getService();
            if (mVisible) {
                mService.registerListener(VPNEActivity.this);
                updateView();
            }
        }
    };

    private String getCurrentViaCaCertificate() {
        try {
            CertificateFactory certFactory;
            certFactory = CertificateFactory.getInstance("X.509");
            InputStream inputStream = getResources().openRawResource(currentProxyServer.getCaIndex());
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(inputStream);
            LocalCertificateStore temp = new LocalCertificateStore();
            temp.addCertificate(cert);
            String al = new LocalCertificateKeyStoreSpi().engineGetCertificateAlias(cert);

            TrustedCertificateEntry trustedCertificateEntry = new TrustedCertificateEntry(al, cert);
            return trustedCertificateEntry.getAlias();

        } catch (Exception e) {
            return "";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vpn);
        initViews();
        setSupportActionBar(toolbar);

        setListeners();
        drawerListeners();

        bindService(new Intent(this, VpnStateService.class),
                mServiceConnection, Service.BIND_AUTO_CREATE);
        vpnProfileDataSource = new VpnProfileDataSource(this);

        currentProxyServer = ProxyServer.Companion.getDefault().get(0);
        updateServerPicker(currentProxyServer);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SERVER_REQUEST && resultCode == Activity.RESULT_OK) {
            ProxyServer proxyServer = data.getParcelableExtra("proxy");
            updateServerPicker(proxyServer);
        }
    }


    private void updateServerPicker(ProxyServer proxyServer) {
        country_tv.setText(proxyServer.getName());
        setImage(proxyServer, country_img, this);
        changeProfile(proxyServer);
    }

    private void changeProfile(ProxyServer proxyServer) {
        currentProxyServer = proxyServer;

        vpnProfileDataSource.open();
        profile = vpnProfileDataSource.getVpnProfile(1);
        profile = new VpnProfile();

        profile.setName(proxyServer.getName());
        profile.setVpnType(VpnType.IKEV2_EAP);
        profile.setGateway(proxyServer.getIpAddress());
        profile.setUsername(proxyServer.getLogin());
        profile.setPassword(proxyServer.getPassword());
        profile.setCertificateAlias(getCurrentViaCaCertificate());
        profile.setFlags(0);

        vpnProfileDataSource.insertProfile(profile);
        vpnProfileDataSource.close();
    }


    private void initViews() {
        drawer_layout = findViewById(R.id.drawer_layout);
        connect = findViewById(R.id.connect);
        toolbar = findViewById(R.id.toolbar);

        status_tv = findViewById(R.id.status_tv);
        turn_tv = findViewById(R.id.turn_tv);
        turn_btn = findViewById(R.id.turn_btn);

        country_tv = findViewById(R.id.country_tv);
        country_img = findViewById(R.id.country_img);

        vpnController = new VpnUiController(status_tv, turn_tv, turn_btn);

        spin_kit = findViewById(R.id.spin_kit);
    }

    public void setConnection() {
        Intent intent = new Intent(VPNEActivity.this, VpnProfileControlActivity.class);
        intent.setAction(VpnProfileControlActivity.START_PROFILE);
        intent.putExtra(VpnProfileControlActivity.EXTRA_VPN_PROFILE_ID, profile.getUUID().toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        isConnected=true;
    }
    private void setDisconnect() {
        mService.disconnect();
        isConnected=false;
    }

    private void setListeners() {
        ImageView menu_btn = findViewById(R.id.menu_btn);
        ImageView side_menu_back_btn = findViewById(R.id.side_menu_back_btn);
        location_pick_btn = findViewById(R.id.location_pick_btn);


        menu_btn.setOnClickListener(v -> {
            if (!drawer_layout.isDrawerOpen(GravityCompat.END)) {
                drawer_layout.openDrawer(GravityCompat.END);
            }
        });

        side_menu_back_btn.setOnClickListener(v -> {
            if (drawer_layout.isDrawerOpen(GravityCompat.END)) {
                drawer_layout.closeDrawer(GravityCompat.END);
            }
        });
        location_pick_btn.setOnClickListener(v -> {
            if (isOpen) {
                Intent intent = new Intent(this, ServerChooserActivity.class);
                intent.putExtra("proxyServer", currentProxyServer);
                startActivityForResult(intent, SERVER_REQUEST);
            }
        });
        turn_btn.setOnClickListener(v -> {
            if (!isConnected)
                setConnection();
            else
                setDisconnect();
        });

    }



    private void drawerListeners() {
        TextView menu_privacy = findViewById(R.id.menu_privacy);
        TextView menu_terms = findViewById(R.id.menu_terms);
        TextView menu_contact = findViewById(R.id.menu_contact);

        menu_privacy.setOnClickListener(v -> {
            openSideMenuFeature(menu_privacy.getText().toString(), hardPrivacy);
        });
        menu_terms.setOnClickListener(v -> {
            openSideMenuFeature(menu_terms.getText().toString(), hardTerms);


        });
        menu_contact.setOnClickListener(v -> {
            openSideMenuFeature(menu_contact.getText().toString(), hardContact);

        });
    }

    private void openSideMenuFeature(String title, String url) {
        Intent intent = new Intent(this, SideMenuFeatureActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("message", url);
        startActivity(intent);
    }


    @Override
    public void onStart() {
        super.onStart();
        mVisible = true;
        if (mService != null) {
            mService.registerListener(this);
            updateView();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mVisible = false;
        if (mService != null) {
            mService.unregisterListener(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            unbindService(mServiceConnection);
        }
    }

    @Override
    public void stateChanged() {
        updateView();
    }

    private void updateView() {
        VpnStateService.State state = mService.getState();
        Log.d(TAG, "updateView: state : " + state.name());

        switch (state) {
            case DISABLED:
                turn_tv.setText("START");
                turn_btn.setEnabled(true);
                vpnController.setStatus(false);
                spin_kit.setVisibility(View.GONE);
                isOpen = true;
                isConnected = false;
                break;
            case CONNECTED:
                isConnected = true;
                vpnController.setStatus(true);
                turn_tv.setText("OFF");
                turn_btn.setEnabled(true);
                spin_kit.setVisibility(View.GONE);
                isOpen = false;
                break;
            case CONNECTING:
                turn_tv.setText("CONNECTING");
                turn_btn.setEnabled(false);
                spin_kit.setVisibility(View.VISIBLE);
                isOpen = false;
                break;
            case DISCONNECTING:
                turn_tv.setText("DISCONNECTING");
                turn_btn.setEnabled(false);
                spin_kit.setVisibility(View.VISIBLE);
                isOpen = false;
                break;

        }

    }

    @Override
    public void onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.END)) {
            drawer_layout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }
}

package tn.isetn.projet_mobile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class delivery_map extends FragmentActivity {

    private MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Récupération des données
        String emailReceived = getIntent().getStringExtra("email");
        String roleReceived = getIntent().getStringExtra("role");

        if (emailReceived == null) emailReceived = "";
        if (roleReceived == null) roleReceived = "";

        final String email = emailReceived;
        final String role = roleReceived;

        Toast.makeText(this, "Bienvenue : " + email, Toast.LENGTH_LONG).show();

        // Charger OSM
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.delivery_map);

        ImageView iconProfile = findViewById(R.id.iconProfile);

        iconProfile.setOnClickListener(v -> {
            Intent i = new Intent(delivery_map.this, ProfileActivity.class);
            i.putExtra("email", email);
            startActivity(i);
        });

        // Initialiser carte
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        // Permission GPS
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1
            );
        }

        // Position : Tunis
        GeoPoint tunis = new GeoPoint(36.8065, 10.1815);
        map.getController().setZoom(13.0);
        map.getController().setCenter(tunis);

        Marker marker = new Marker(map);
        marker.setPosition(tunis);
        marker.setTitle("Tunis");
        map.getOverlays().add(marker);
    }
}

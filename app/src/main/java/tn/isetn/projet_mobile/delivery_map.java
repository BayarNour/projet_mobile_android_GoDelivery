package tn.isetn.projet_mobile;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

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

        // Charger configuration OSMDroid
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.delivery_map);

        // Initialiser la carte
        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        // Permissions localisation
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1
            );
        }

        // Centre de la carte : Tunis
        GeoPoint tunis = new GeoPoint(36.8065, 10.1815);
        map.getController().setZoom(13.0);
        map.getController().setCenter(tunis);

        // Ajouter un marqueur
        Marker marker = new Marker(map);
        marker.setPosition(tunis);
        marker.setTitle("Tunis");
        map.getOverlays().add(marker);
    }
}

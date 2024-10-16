package com.example.lbs;

import static android.location.Location.FORMAT_SECONDS;
import static android.location.Location.FORMAT_DEGREES;
import static android.location.Location.FORMAT_MINUTES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.Manifest;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

public class GNSSActivity extends AppCompatActivity {
    private LocationManager locationManager; // O Gerente de localização
    private LocationProvider locationProvider; // O provedor de localizações
    private static final int REQUEST_LOCATION = 1;
    private String selectedFormat = "Graus [+/-DDD.DDDDD]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gnssactivity);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        obtemLocationProvider_Permission();
    }

    public void obtemLocationProvider_Permission() {
        // Verifica se a aplicação tem acesso a sistema de localização
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // caso tenha permissão para acessar sistema de localização, obtenha o LocationProvider
            locationProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
            // Começa processo de aquisição de localizações e Satelites
            startLocationAndGNSSUpdates();
        } else {
            // Solicite a permissão
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                // O usuário acabou de dar a permissão
                obtemLocationProvider_Permission();
            } else {
                // O usuário não deu a permissão solicitada
                Toast.makeText(this, "Sem permissão para acessar o sistema de posicionamento",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void startLocationAndGNSSUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(locationProvider.getName(), 1000, 0.1f, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                mostraLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                LocationListener.super.onStatusChanged(provider, status, extras);
            }
        });
        locationManager.registerGnssStatusCallback(new GnssStatus.Callback() {
            @Override
            public void onSatelliteStatusChanged(@NonNull GnssStatus status) {
                super.onSatelliteStatusChanged(status);
                mostraGNSS(status);
               mostraGNSSGrafico(status);
            }
        });
    }

    public void mostraGNSSGrafico(GnssStatus status) {
        EsferaCelesteView esferaCelesteView = findViewById(R.id.esferacelesteview_id);
        esferaCelesteView.setNewStatus(status);
    }

    public void mostraGNSS(GnssStatus status) {
        TextView textView = findViewById(R.id.textviewGnss_id);
        String mens = "Dados do Sitema de Posicionamento\n";
        if (status != null) {
            mens += "Número de Satélites:" + status.getSatelliteCount() + "\n";
            for (int i = 0; i < status.getSatelliteCount(); i++) {
                mens += "SVID-CONST-SNR="
                        + status.getSvid(i) + "-"
                        + status.getConstellationType(i) + "-"
                        + status.getCn0DbHz(i) +
                        "Azi=" + status.getAzimuthDegrees(i) +
                        "Elev=" + status.getElevationDegrees(i) + "\n";

            }
        } else {
            mens += "GNSS Não disponível";
        }
        textView.setText(mens);
    }

    public void mostraLocation(Location location) {
        TextView textView = findViewById(R.id.textviewLocation_id);
        String mens = "Dados da Última posição\n";
        if (location != null) {
            mens += String.valueOf("Latitude(graus)="
                    + Location.convert(location.getLatitude(), FORMAT_SECONDS)) + "\n"
                    + String.valueOf("Longitude(graus)="
                    + Location.convert(location.getLongitude(), FORMAT_SECONDS)) + "\n"
                    + String.valueOf("Velocidade(m/s)=" + location.getSpeed()) + "\n"
                    + String.valueOf("Rumo(graus)=" + location.getBearing());
        } else {
            mens += "Localização Não disponível";
        }
        switch (selectedFormat) {
            case "Graus [+/-DDD.DDDDD]":
                String r =  String.valueOf("Latitude(graus)="
                        + Location.convert(location.getLatitude(), FORMAT_DEGREES)) + "\n"
                        + String.valueOf("Longitude(graus)="
                        + Location.convert(location.getLongitude(), FORMAT_DEGREES));
                break;
            case "Graus-Minutos [+/-DDD:MM.MMMMM]":
                String s = String.valueOf("Latitude(graus)="
                        + Location.convert(location.getLatitude(), FORMAT_MINUTES)) + "\n"
                        + String.valueOf("Longitude(graus)="
                        + Location.convert(location.getLongitude(), FORMAT_MINUTES));
                break;
        }
        textView.setText(mens);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            showCoordinatesOptions();
            return true;
        }
        return false;
    }

    public void showCoordinatesOptions(){
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final String[] coordinates = {"Graus [+/-DDD.DDDDD]", "Graus-Minutos [+/-DDD:MM.MMMMM]", "Graus-Minutos-Segundos [+/-DDD:MM:SS.SSSSS]"};
        builder.setTitle("Configurar formato de apresentação das coordenadas geográficas:");
        builder.setSingleChoiceItems(coordinates, 0, null);
        builder.setPositiveButton("Salvar", (dialog, which) -> {
            selectedFormat = coordinates[((android.app.AlertDialog) dialog).getListView().getCheckedItemPosition()];
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.create().show();

    }
}
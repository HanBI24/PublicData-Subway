package hello.world.datagostation;

import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MarkerAroundAdapter implements GoogleMap.InfoWindowAdapter {
    View window;
    MarkerAroundInfo markerAroundInfo;

    public MarkerAroundAdapter(View window, MarkerAroundInfo markerAroundInfo){
        this.window = window;
        this.markerAroundInfo = markerAroundInfo;//정보를 담은 객체
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView exitNo = window.findViewById(R.id.exit_no);
        TextView buildingName = window.findViewById(R.id.buildings);

        exitNo.setText(markerAroundInfo.getExit_no());
        buildingName.setText(markerAroundInfo.getBuilding_name());
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}

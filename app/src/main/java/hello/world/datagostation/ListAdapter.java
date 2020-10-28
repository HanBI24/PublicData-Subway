package hello.world.datagostation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    public ArrayList<Station> list = new ArrayList<>();
    // backup ArrayList
    private ArrayList<Station> arrayList = list;

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_frame, viewGroup, false);

            TextView stationLineText = (TextView) view.findViewById(R.id.station_line_text);
            TextView stationNameText = (TextView) view.findViewById(R.id.station_name_text);

            holder.stationLine = stationLineText;
            holder.stationName = stationNameText;

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Station station = arrayList.get(i);
        holder.stationLine.setText(station.getStationLine());
        holder.stationName.setText(String.valueOf(station.getStationName()));

        return view;
    }

    public void addItem(String stationLine, String stationName) {
        Station station = new Station();

        station.setStationLine(stationLine);
        station.setStationName(stationName);

        list.add(station);
    }

    public void resetList() {
        arrayList.clear();
    }

    static class ViewHolder {
        TextView stationLine, stationName;
    }
}
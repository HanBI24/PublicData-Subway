package hello.world.datagostation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class StationAroundInfoListAdapter extends BaseAdapter {
    public ArrayList<StationAroundInfo> list = new ArrayList<>();
    // backup ArrayList
    private ArrayList<StationAroundInfo> arrayList = list;

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new StationAroundInfoListAdapter.ViewHolder();
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_frame, viewGroup, false);

            TextView stationBuilding = (TextView) view.findViewById(R.id.station_line_text);
            TextView stationExit = (TextView) view.findViewById(R.id.station_name_text);

            holder.stationBuilding = stationBuilding;
            holder.stationExit = stationExit;

            view.setTag(holder);
        } else {
            holder = (StationAroundInfoListAdapter.ViewHolder) view.getTag();
        }

        StationAroundInfo stationAroundInfo = arrayList.get(i);
        holder.stationBuilding.setText(stationAroundInfo.getBuildingName());
        holder.stationExit.setText(String.valueOf(stationAroundInfo.getExitNo()));

        return view;
    }

    public void addItem(String stationBuilding, String stationExit) {
        StationAroundInfo station = new StationAroundInfo();

        station.setBuildingName(stationBuilding);
        station.setExitNo(stationExit);

        list.add(station);
    }

    public void resetList() {
        arrayList.clear();
    }

    static class ViewHolder {
        TextView stationBuilding, stationExit;
    }
}

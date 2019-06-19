package mebeidcreations.apps.livegosee;

import android.support.v7.util.DiffUtil;

import java.util.List;

public class TripInfoDiffCallBack extends DiffUtil.Callback {
    private List<TripInfo> current;
    private List<TripInfo> next;

    public TripInfoDiffCallBack(List<TripInfo> current, List<TripInfo> next) {
        this.current = current;
        this.next = next;
    }

    @Override
    public int getOldListSize() {
        return current.size();
    }

    @Override
    public int getNewListSize() {
        return next.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        TripInfo currentItem = current.get(oldItemPosition);
        TripInfo nextItem = next.get(newItemPosition);
        return currentItem.gettrip_id() == nextItem.gettrip_id();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        TripInfo currentItem = current.get(oldItemPosition);
        TripInfo nextItem = next.get(newItemPosition);
        return currentItem.equals(nextItem);
    }
}
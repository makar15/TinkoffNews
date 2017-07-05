package ma.makar.tinkoffnews.adapters;

import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ma.makar.base.Assert;

public abstract class RecyclerListAdapter<ViewHolder extends RecyclerView.ViewHolder, V>
        extends RecyclerView.Adapter<ViewHolder> {

    private static final String TAG = "TitleAdapter";

    public interface OnClickItemListener<V> {
        void onClick(V type);
    }

    private final List<V> mList = new ArrayList<>();
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            V value = (V) v.getTag();
            Assert.assertNotNull(value);
            if (value == null) {
                Log.e(TAG, "Value on clicked is null object");
                return;
            }
            if (mOnClickItemListener != null) {
                mOnClickItemListener.onClick(value);
            }
        }
    };

    private @Nullable OnClickItemListener<V> mOnClickItemListener;

    public RecyclerListAdapter() {}

    public RecyclerListAdapter(List<V> list) {
        mList.addAll(list);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public final void onBindViewHolder(ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(mOnClickListener);
        fillHolder(holder, position);
    }

    public V getItem(int position) {
        return mList.get(position);
    }

    public void updateList(List<V> newList) {
        mList.clear();
        mList.addAll(newList);
        notifyDataSetChanged();
    }

    public void setOnClickItemListener(OnClickItemListener<V> listener) {
        mOnClickItemListener = listener;
    }

    @CallSuper
    protected void fillHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(getItem(position));
    }

}

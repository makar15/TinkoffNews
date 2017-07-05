package ma.makar.tinkoffnews.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ma.makar.tinkoffnews.R;
import ma.makar.tinkoffnews.models.Title;

public class TitleAdapter extends RecyclerListAdapter<TitleAdapter.TitleHolder, Title> {

    @Override
    public TitleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_title, parent, false);
        return new TitleHolder(view);
    }

    @Override
    protected void fillHolder(TitleHolder holder, int position) {
        super.fillHolder(holder, position);
        holder.fillView(getItem(position));
    }

    static class TitleHolder extends RecyclerView.ViewHolder {

        final TextView text;

        TitleHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.text);
        }

        void fillView(Title title) {
            text.setText(title.text);
        }
    }
}

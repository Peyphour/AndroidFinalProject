/**
 * By Bertrand NANCY and Kevin NUNES
 * Copyright 2018
 */

package ph.pey.finalproject.fragment.pictures;

import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ph.pey.finalproject.R;
import ph.pey.finalproject.fragment.pictures.PictureHolder.Picture;

import java.util.List;

public class MatchPicturesRecyclerViewAdapter extends RecyclerView.Adapter<MatchPicturesRecyclerViewAdapter.ViewHolder> {

    private final List<Picture> mValues;

    public MatchPicturesRecyclerViewAdapter(List<Picture> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_matchpictures, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.imageView.setImageBitmap(BitmapFactory.decodeFile(mValues.get(position).path));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView imageView;
        public Picture mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            imageView = view.findViewById(R.id.image);
        }
    }
}

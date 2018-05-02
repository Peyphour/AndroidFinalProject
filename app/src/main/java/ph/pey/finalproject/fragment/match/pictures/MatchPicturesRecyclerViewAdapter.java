package ph.pey.finalproject.fragment.match.pictures;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ph.pey.finalproject.R;
import ph.pey.finalproject.fragment.match.pictures.MatchPicturesFragment.OnListFragmentInteractionListener;
import ph.pey.finalproject.fragment.match.pictures.PictureHolder.Picture;

import java.util.List;

public class MatchPicturesRecyclerViewAdapter extends RecyclerView.Adapter<MatchPicturesRecyclerViewAdapter.ViewHolder> {

    private final List<Picture> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MatchPicturesRecyclerViewAdapter(List<Picture> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
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
        holder.imageView.setImageURI(new Uri.Builder().appendPath(mValues.get(position).path).build());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
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

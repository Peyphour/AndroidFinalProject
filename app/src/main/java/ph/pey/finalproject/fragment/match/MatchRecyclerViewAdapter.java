package ph.pey.finalproject.fragment.match;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import ph.pey.finalproject.MainActivity;
import ph.pey.finalproject.R;
import ph.pey.finalproject.sql.MatchEntity;

import java.util.List;

public class MatchRecyclerViewAdapter extends RecyclerView.Adapter<MatchRecyclerViewAdapter.ViewHolder> {

    private final List<MatchEntity> mValues;
    private final MainActivity mListener;

    public MatchRecyclerViewAdapter(List<MatchEntity> items, MainActivity listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_match, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getUid() + "");
        holder.mLocationView.setText(mListener.getAddressFromLocation(new LatLng(mValues.get(position).getLatitude(), mValues.get(position).getLongitude())));
        holder.mDurationView.setText(mValues.get(position).getDurationInMinutes() + "min");
        holder.score.setText(mValues.get(position).getScore());
        holder.winner.setText(mValues.get(position).getWinner());
        holder.loser.setText(mValues.get(position).getLoser());

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
        public final TextView mIdView;
        public final TextView mLocationView;
        public final TextView mDurationView;
        public final TextView score;
        public final TextView winner;
        public final TextView loser;
        public MatchEntity mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.id);
            mLocationView = view.findViewById(R.id.location);
            mDurationView = view.findViewById(R.id.duration);
            score = view.findViewById(R.id.score);
            winner = view.findViewById(R.id.winner);
            loser = view.findViewById(R.id.loser);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mLocationView.getText() + "'";
        }
    }
}

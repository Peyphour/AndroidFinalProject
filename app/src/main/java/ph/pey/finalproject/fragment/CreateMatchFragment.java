package ph.pey.finalproject.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import ph.pey.finalproject.MainActivity;
import ph.pey.finalproject.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateMatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateMatchFragment extends Fragment {

    private Listener listener;
    private MainActivity mainActivity;

    public CreateMatchFragment() {
        // Required empty public constructor
    }

    public static CreateMatchFragment newInstance() {
        return new CreateMatchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void locationUpdate() {
        if(mainActivity != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ((TextView) getView().findViewById(R.id.location_textview)).append(" " + mainActivity.reverseGeoCodeLastLocation());
                }
            }).start();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v =  inflater.inflate(R.layout.fragment_create_match, container, false);
        v.findViewById(R.id.create_match_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateButtonClick();
            }
        });

        ((TextView) v.findViewById(R.id.location_textview)).append(" " + mainActivity.reverseGeoCodeLastLocation());

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof Listener)
            this.listener = (Listener) context;

        if(context instanceof MainActivity)
            this.mainActivity = (MainActivity) context;
    }

    public void onCreateButtonClick() {
        if(this.listener != null) {
            this.listener.onCreateButtonPressed(Integer.valueOf(((EditText)getView().findViewById(R.id.duration_input)).getText().toString()),
                    ((EditText)getView().findViewById(R.id.score_input)).getText().toString(),
                    ((EditText)getView().findViewById(R.id.winner_input)).getText().toString(),
                    ((EditText)getView().findViewById(R.id.loser_input)).getText().toString());
        }
    }

    public interface Listener {
        void onCreateButtonPressed(Integer duration, String score, String winner, String loser);
    }
}

package com.jonathan.taxidispatching.activity.passenger_main_activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jonathan.taxidispatching.Model.Transcation;
import com.jonathan.taxidispatching.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Passenger_Waiting_Fragment extends Fragment {
    private static final String TRANSACTION = "transaction";

    private Transcation transcation;

    @BindView(R.id.idText)
    TextView idText;
    @BindView(R.id.pickUpPointText)
    TextView pickUpPointText;
    @BindView(R.id.destinationText)
    TextView destinationText;
    @BindView(R.id.requirementText)
    TextView requirementText;
    @BindView(R.id.statusText)
    TextView statusText;
    @BindView(R.id.passengerProgressBar)
    ProgressBar progressBar;
    @BindView(R.id.cancelOrderButton)
    Button cancelOrderButton;


    public Passenger_Waiting_Fragment() {
        // Required empty public constructor
    }

    public static Passenger_Waiting_Fragment newInstance(Transcation transcation) {
        Passenger_Waiting_Fragment fragment = new Passenger_Waiting_Fragment();
        Bundle args = new Bundle();
        args.putSerializable(TRANSACTION, transcation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transcation = (Transcation) getArguments().getSerializable(TRANSACTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_passenger_waiting, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(transcation != null) {
            idText.setText(String.valueOf(transcation.data.id));
            pickUpPointText.setText(transcation.data.startAddr);
            destinationText.setText(transcation.data.desAddr);
            requirementText.setText(transcation.data.requirement);
            statusText.setText(String.valueOf(transcation.data.status));
        }
    }

    @OnClick(R.id.cancelOrderButton)
    public void cancelOrder() {
        // Cancel the transaction order
    }
}

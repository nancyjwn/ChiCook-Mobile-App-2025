package com.example.chicook.model.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.chicook.R;

public class OnboardingFragment extends Fragment {

    private static final String ARG_IMAGE = "image";
    private static final String ARG_DESCRIPTION = "description";

    public OnboardingFragment() {}

    public static OnboardingFragment newInstance(int imageRes, String description) {
        OnboardingFragment fragment = new OnboardingFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_IMAGE, imageRes);
        args.putString(ARG_DESCRIPTION, description);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_onboarding, container, false);

        if (getArguments() != null) {
            int imageRes = getArguments().getInt(ARG_IMAGE);
            String description = getArguments().getString(ARG_DESCRIPTION);

            ImageView imageView = rootView.findViewById(R.id.onboarding_image);

            imageView.setImageResource(imageRes);
        }

        return rootView;
    }
}

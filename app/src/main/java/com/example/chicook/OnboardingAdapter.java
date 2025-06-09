package com.example.chicook;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {

    private int[] images;
    private String[] descriptions;

    public OnboardingAdapter(int[] images, String[] descriptions) {
        this.images = images;
        this.descriptions = descriptions;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_onboarding, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        holder.bind(images[position], descriptions[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public class OnboardingViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView description;

        public OnboardingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.onboarding_image);
            description = itemView.findViewById(R.id.onboarding_description);
        }

        public void bind(int imageResId, String descriptionText) {
            imageView.setImageResource(imageResId);
            description.setText(descriptionText);
        }
    }
}

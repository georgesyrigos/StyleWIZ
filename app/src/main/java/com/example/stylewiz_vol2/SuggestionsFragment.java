package com.example.stylewiz_vol2;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SuggestionsFragment extends Fragment {
    LinearLayout optionSeason, optionOccasion, optionType;
    FrameLayout contentContainer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_suggestions, container, false);

        optionSeason = view.findViewById(R.id.optionSeason);
        optionOccasion = view.findViewById(R.id.optionOccasion);
        optionType = view.findViewById(R.id.optionType);
        contentContainer = view.findViewById(R.id.contentContainer); // Make sure this matches the XML

        // Set default selected (initial selection if needed)
        selectOption(optionSeason, "season");

        optionSeason.setOnClickListener(v -> selectOption(optionSeason, "season"));
        optionOccasion.setOnClickListener(v -> selectOption(optionOccasion, "occasion"));
        optionType.setOnClickListener(v -> selectOption(optionType, "type"));

        return view;
    }

    private void selectOption(LinearLayout selectedOption, String type) {
        // Reset backgrounds
        optionSeason.setBackgroundColor(Color.TRANSPARENT);
        optionOccasion.setBackgroundColor(Color.TRANSPARENT);
        optionType.setBackgroundColor(Color.TRANSPARENT);

        // Highlight selected
        selectedOption.setBackgroundResource(R.drawable.selected_background); // custom drawable

        // Show content based on selected option
        switch (type) {
            case "season":
                displaySeasonSelector();
                break;
            case "occasion":
                displayOccasionContent();
                break;
            case "type":
                displayTypeContent();
                break;
        }
    }

    private void displaySeasonSelector() {
        // Remove previous content if any
        contentContainer.removeAllViews();

        // Inflate and add the GridLayout for seasons dynamically
        View seasonGrid = getLayoutInflater().inflate(R.layout.season_selector, contentContainer, false);
        contentContainer.addView(seasonGrid);

        // Set up the season selector (handle clicks)
        setupSeasonSelector(seasonGrid);
    }

    private void displayOccasionContent() {
        // Remove previous content if any
        contentContainer.removeAllViews();

        // Add content for "Occasion"
        TextView content = new TextView(requireContext());
        content.setText("Information for Occasion");
        content.setTextSize(16);
        content.setPadding(10, 10, 10, 10);
        contentContainer.addView(content);
    }

    private void displayTypeContent() {
        // Remove previous content if any
        contentContainer.removeAllViews();

        // Add content for "Type"
        TextView content = new TextView(requireContext());
        content.setText("Information for Type");
        content.setTextSize(16);
        content.setPadding(10, 10, 10, 10);
        contentContainer.addView(content);
    }

    private void setupSeasonSelector(View rootView) {
        // Get the GridLayout that you inflated
        GridLayout seasonGrid = rootView.findViewById(R.id.seasonGrid);

        for (int i = 0; i < seasonGrid.getChildCount(); i++) {
            View item = seasonGrid.getChildAt(i);

            // Set an OnClickListener for each item in the GridLayout
            item.setOnClickListener(v -> {
                // Clear selection (remove background) for all items
                for (int j = 0; j < seasonGrid.getChildCount(); j++) {
                    seasonGrid.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);  // Reset background
                }

                // Set the background for the selected item
                v.setBackgroundResource(R.drawable.selected_background); // Apply custom selected background

                // Optionally, save the selected season
                String selectedSeason = (String) v.getTag();
                Log.d("SelectedSeason", selectedSeason);
            });
        }

        // Optionally, pre-select one (e.g., Spring)
        View defaultSelectedView = seasonGrid.findViewById(R.id.seasonSpring);
        if (defaultSelectedView != null) {
            defaultSelectedView.setBackgroundResource(R.drawable.selected_background); // Pre-select Spring
        }
    }

}
package com.example.stylewiz_vol2;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.function.Consumer;

public class SuggestionsFragment extends Fragment {
    LinearLayout optionSeason, optionOccasion, optionType;
    FrameLayout contentContainer;

    private String selectedSeasonTag = "spring";
    private String selectedStyleTag = "sport";
    private String selectedTypeTag = "one_piece";
    private String selectedAccessoriesTag = "0";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_suggestions, container, false);

        //loadUserPreferences(); //Load and store default values


        optionSeason = view.findViewById(R.id.optionSeason);
        optionOccasion = view.findViewById(R.id.optionOccasion);
        optionType = view.findViewById(R.id.optionType);
        contentContainer = view.findViewById(R.id.contentContainer); // Make sure this matches the XML

        // Set default selected (initial selection if needed)
        selectOption(optionSeason, "season");


        optionSeason.setOnClickListener(v -> selectOption(optionSeason, "season"));
        optionOccasion.setOnClickListener(v -> selectOption(optionOccasion, "occasion"));
        optionType.setOnClickListener(v -> selectOption(optionType, "type"));

        Button btnApplyFilters = view.findViewById(R.id.btnApplyFilters);
        btnApplyFilters.setOnClickListener(v -> {
            logUserPreferences();

            Log.d("DEBUG", "Apply Filters clicked");

            OutfitGenerator.generateOutfits(requireContext(), outfits -> {
                for (int i = 0; i < outfits.size(); i++) {
                    List<String> outfit = outfits.get(i);
                    Log.d("OUTFIT_" + (i + 1), outfit.toString());
                }
            });
        });


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

        // Inflate and add the GridLayout for occasion dynamically
        View styleGrid = getLayoutInflater().inflate(R.layout.style_selector, contentContainer, false);
        contentContainer.addView(styleGrid);

        // Set up the style selector (handle clicks)
        setupStyleSelector(styleGrid);
    }

    private void displayTypeContent() {
        // Remove previous content if any
        contentContainer.removeAllViews();

        // Inflate and add the GridLayout for type dynamically
        View typeGrid = getLayoutInflater().inflate(R.layout.type_selector, contentContainer, false);
        contentContainer.addView(typeGrid);

        // Set up the type selector (handle clicks)
        setupTypeSelector(typeGrid);
    }

    private void setupSeasonSelector(View rootView) {
        // Get the GridLayout that you inflated
        GridLayout seasonGrid = rootView.findViewById(R.id.seasonGrid);

        for (int i = 0; i < seasonGrid.getChildCount(); i++) {
            View item = seasonGrid.getChildAt(i);
            item.setOnClickListener(v -> {
                for (int j = 0; j < seasonGrid.getChildCount(); j++) {
                    seasonGrid.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                }

                v.setBackgroundResource(R.drawable.selected_background);
                selectedSeasonTag = (String) v.getTag(); // Save selection
                Log.d("SelectedSeason", selectedSeasonTag);

                saveUserPreferences(); // <<-- Save immediately after change

            });

            String tag = (String) item.getTag();
            if (tag != null && tag.equals(selectedSeasonTag)) {
                item.setBackgroundResource(R.drawable.selected_background);
            }

            
        }
    }

    private void setupStyleSelector(View rootView) {
        GridLayout styleGrid = rootView.findViewById(R.id.styleGrid);

        for (int i = 0; i < styleGrid.getChildCount(); i++) {
            View item = styleGrid.getChildAt(i);
            item.setOnClickListener(v -> {
                for (int j = 0; j < styleGrid.getChildCount(); j++) {
                    styleGrid.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                }

                v.setBackgroundResource(R.drawable.selected_background);
                selectedStyleTag = (String) v.getTag(); // Save selection
                Log.d("SelectedStyle", selectedStyleTag);

                saveUserPreferences();

            });

            // Restore previously selected
            String tag = (String) item.getTag();
            if (tag != null && tag.equals(selectedStyleTag)) {
                item.setBackgroundResource(R.drawable.selected_background);
            }
        }
    }

    private void setupTypeSelector(View rootView) {
        GridLayout outfitTypeGrid = rootView.findViewById(R.id.outfitTypeGrid);
        GridLayout accessoriesGrid = rootView.findViewById(R.id.accessoriesGrid);

        setupSingleSelection(outfitTypeGrid, selectedTypeTag, tag -> selectedTypeTag = tag);
        setupSingleSelection(accessoriesGrid, selectedAccessoriesTag, tag -> selectedAccessoriesTag = tag);
    }


    private void setupSingleSelection(GridLayout grid, String selectedTag, Consumer<String> onSelect) {
        for (int i = 0; i < grid.getChildCount(); i++) {
            View item = grid.getChildAt(i);
            String tag = (String) item.getTag();

            // Pre-select the one matching selectedTag
            if (tag != null && tag.equals(selectedTag)) {
                item.setBackgroundResource(R.drawable.selected_background);
            } else {
                item.setBackgroundColor(Color.TRANSPARENT);
            }

            item.setOnClickListener(v -> {
                // Clear previous selection
                for (int j = 0; j < grid.getChildCount(); j++) {
                    grid.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                }

                // Highlight selected
                v.setBackgroundResource(R.drawable.selected_background);

                // Pass selected tag to handler
                String newTag = (String) v.getTag();
                if (newTag != null) {
                    onSelect.accept(newTag);
                    Log.d("Selection", "Selected " + newTag);

                    saveUserPreferences();
                }
            });
        }
    }

    private void loadUserPreferences() {
        if (getContext() == null) return;

        SharedPreferences prefs = getContext().getSharedPreferences("user_filters", getContext().MODE_PRIVATE);

        // Load or fall back to default
        selectedSeasonTag = prefs.getString("season", selectedSeasonTag); // "spring" by default
        selectedStyleTag = prefs.getString("occasion", selectedStyleTag); // "sport" by default
        selectedTypeTag = prefs.getString("outfit_type", selectedTypeTag); // "one_piece" by default
        selectedAccessoriesTag = prefs.getString("accessories", selectedAccessoriesTag); // "0" by default

        // Save these back in case this is the first run (harmless if already exists)
        saveUserPreferences();
    }

    //save user preferences
    private void saveUserPreferences() {
        if (getContext() == null) return; // Prevent null context crash

        getContext()
                .getSharedPreferences("user_filters", getContext().MODE_PRIVATE)
                .edit()
                .putString("season", selectedSeasonTag)
                .putString("occasion", selectedStyleTag)
                .putString("outfit_type", selectedTypeTag)
                .putString("accessories", selectedAccessoriesTag)
                .apply();
    }

    private void logUserPreferences() {
        if (getContext() == null) return;

        SharedPreferences prefs = getContext().getSharedPreferences("user_filters", getContext().MODE_PRIVATE);

        String season = prefs.getString("season", "not set");
        String occasion = prefs.getString("occasion", "not set");
        String type = prefs.getString("outfit_type", "not set");
        String accessories = prefs.getString("accessories", "not set");

        Log.d("USER_PREFERENCES", "Season: " + season);
        Log.d("USER_PREFERENCES", "Occasion: " + occasion);
        Log.d("USER_PREFERENCES", "Outfit Type: " + type);
        Log.d("USER_PREFERENCES", "Accessories: " + accessories);
    }




}
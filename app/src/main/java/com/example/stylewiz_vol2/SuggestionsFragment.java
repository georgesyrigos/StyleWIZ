package com.example.stylewiz_vol2;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class SuggestionsFragment extends Fragment {
    LinearLayout optionSeason, optionOccasion, optionType;
    FrameLayout contentContainer;
    private String selectedSeasonTag = "spring";
    private String selectedStyleTag = "sport";
    private String selectedTypeTag = "one_piece";
    private String selectedAccessoriesTag = "no";
    private String selectedOuterwearTag = "no";
    private List<OutfitSuggestion> infiniteSuggestions;
    private int middleIndex;
    FirebaseFirestore db;
    private RecyclerView suggestionsRecycler;
    private LinearLayout overlayContainer;
    private TextView noSuggestionsText;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_suggestions, container, false);

        //loadUserPreferences(); //Load and store default values
        resetUserPreferencesToDefaults();

        optionSeason = view.findViewById(R.id.optionSeason);
        optionOccasion = view.findViewById(R.id.optionOccasion);
        optionType = view.findViewById(R.id.optionType);
        contentContainer = view.findViewById(R.id.contentContainer); // Make sure this matches the XML

        // Set default selected (initial selection if needed)
        selectOption(optionSeason, "season");


        optionSeason.setOnClickListener(v -> selectOption(optionSeason, "season"));
        optionOccasion.setOnClickListener(v -> selectOption(optionOccasion, "occasion"));
        optionType.setOnClickListener(v -> selectOption(optionType, "type"));

        //Additions for the carousel
        Button btnApplyFilters = view.findViewById(R.id.btnApplyFilters);
        overlayContainer = view.findViewById(R.id.overlayCarouselContainer);
        suggestionsRecycler = view.findViewById(R.id.suggestionsRecycler);
        ImageView closeSuggestions = view.findViewById(R.id.closeSuggestions);
        noSuggestionsText = view.findViewById(R.id.noSuggestionsText);
        ImageView favSuggestions = view.findViewById(R.id.favSuggestions);


        btnApplyFilters.setOnClickListener(v -> {
            logUserPreferences();
            Log.d("DEBUG", "Apply Filters clicked");


            OutfitGenerator.generateOutfits(requireContext(), outfits -> {
                if (outfits.isEmpty()) {
                    // ✅ No outfits generated at all
                    overlayContainer.setVisibility(View.VISIBLE);
                    noSuggestionsText.setVisibility(View.VISIBLE);
                    suggestionsRecycler.setVisibility(View.GONE);
                    return;
                }

                List<OutfitSuggestion> baseSuggestions = new ArrayList<>();

                String outerwearText = selectedOuterwearTag.equals("no") ? "without outerwear" : "with outerwear";

                db = FirebaseFirestore.getInstance();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                for (int i = 0; i < outfits.size(); i++) {
                    final int outfitIndex = i;
                    List<String> outfitItemIds = outfits.get(outfitIndex);

                    List<ImageItem> imageItems = new ArrayList<>();
                    AtomicInteger fetchedCount = new AtomicInteger(0);

                    for (String itemId : outfitItemIds) {
                        db.collection("users")
                                .document(user.getUid())
                                .collection("wardrobe")
                                .document(itemId)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        DataClass item = documentSnapshot.toObject(DataClass.class);
                                        if (item != null && item.getPhotoUrl() != null && item.getCategory() != null) {
                                            imageItems.add(new ImageItem(item.getPhotoUrl(), item.getCategory()));
                                        }
                                    }

                                    if (fetchedCount.incrementAndGet() == outfitItemIds.size()) {
                                        //All items fetched for this outfit
                                        String title;
                                        if (outfitIndex == 0) {
                                            title = "Best outfit";
                                        } else if (outfitIndex == 1) {
                                            title = "Second best outfit";
                                        } else if (outfitIndex == 2) {
                                            title = "Third best outfit";
                                        } else {
                                            title = "Outfit " + (outfitIndex + 1);
                                        }

                                        String desc = String.format(
                                                "%s %s outfit %s",
                                                capitalize(selectedStyleTag),
                                                capitalize(selectedSeasonTag),
                                                outerwearText
                                        );

                                        baseSuggestions.add(new OutfitSuggestion(title, desc, imageItems));
                                        // ✅ Check if all outfits are built
                                        if (baseSuggestions.size() == outfits.size()) {
                                            buildInfiniteSuggestionsAndSetupAdapter(baseSuggestions);
                                            Log.d("DEBUG", "All outfits built");

                                        }
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FirestoreError", "Failed to fetch item " + itemId, e);
                                });
                    }
                }
            });




            /*OutfitGenerator.generateOutfits(requireContext(), outfits -> {
                List<OutfitSuggestion> baseSuggestions = new ArrayList<>();

                String outerwearText = selectedOuterwearTag.equals("no") ? "without outerwear" : "with outerwear";

                //Initialize firestore
                db = FirebaseFirestore.getInstance();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                for (int i = 0; i < outfits.size(); i++) {
                    List<String> outfitItemIds = outfits.get(i);
                    // Retrieve each item's imageUrl
                    List<String> imageUrls = new ArrayList<>();

                    for (String itemId : outfitItemIds) {
                        String userId = user.getUid();
                        db.collection("users")
                                .document(userId)
                                .collection("wardrobe")
                                .document(itemId)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        DataClass item = documentSnapshot.toObject(DataClass.class);
                                        if (item != null && item.getPhotoUrl() != null) {
                                            imageUrls.add(item.getPhotoUrl());
                                        }
                                    }

                                    // ✅ Optional: If you need to update the UI after all items are loaded,
                                    // check here if imageUrls.size() == outfitItemIds.size()
                                    SuggestionAdapter adapter = new SuggestionAdapter(infiniteSuggestions);
                                    suggestionsRecycler.setAdapter(adapter);

                                    overlayContainer.setVisibility(View.VISIBLE);

                                    suggestionsRecycler.post(() -> {
                                        RecyclerView.LayoutManager layoutManager = suggestionsRecycler.getLayoutManager();
                                        if (layoutManager instanceof LinearLayoutManager) {
                                            ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(middleIndex, 0);
                                        }
                                    });


                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FirestoreError", "Failed to fetch item " + itemId, e);
                                });
                    }


                    String title;
                    if (i == 0) {
                        title = "Best outfit";
                    } else if (i == 1) {
                        title = "Second best outfit";
                    } else if (i == 2) {
                        title = "Third best outfit";
                    } else {
                        title = "Outfit " + (i + 1);
                    }

                    String desc = String.format(
                            "%s %s outfit %s",
                            capitalize(selectedStyleTag),
                            capitalize(selectedSeasonTag),
                            outerwearText
                    );

                    // Pass the list of image URLs instead of a single image
                    baseSuggestions.add(new OutfitSuggestion(title, desc, imageUrls));

                    Log.d("OUTFIT_" + (i + 1), outfitItemIds.toString());
                }

                // Build infinite suggestions by repeating baseSuggestions
                infiniteSuggestions = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    infiniteSuggestions.addAll(baseSuggestions);
                }

                middleIndex = infiniteSuggestions.size() / 2;

                // Setup carousel here with the new data
                setupCarouselRecycler(suggestionsRecycler, middleIndex);
            });*/
        });

        closeSuggestions.setOnClickListener(v -> {
            overlayContainer.setVisibility(View.GONE);

            // Reset contentContainer layout params if needed
            contentContainer.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            contentContainer.requestLayout();

            optionOccasion.setBackgroundColor(Color.TRANSPARENT);
            optionType.setBackgroundColor(Color.TRANSPARENT);
            // Re-display season selector or other content as appropriate
            optionSeason.setBackgroundResource(R.drawable.selected_background);
            displaySeasonSelector();

        });


        return view;
    }

    private void buildInfiniteSuggestionsAndSetupAdapter(List<OutfitSuggestion> baseSuggestions) {
        overlayContainer.setVisibility(View.VISIBLE);

        if (baseSuggestions.isEmpty()) {
            noSuggestionsText.setVisibility(View.VISIBLE);
            suggestionsRecycler.setVisibility(View.GONE);
        } else {
            noSuggestionsText.setVisibility(View.GONE);
            suggestionsRecycler.setVisibility(View.VISIBLE);

            List<OutfitSuggestion> infiniteSuggestions = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                infiniteSuggestions.addAll(baseSuggestions);
            }

            int middleIndex = infiniteSuggestions.size() / 2;

            SuggestionAdapter adapter = new SuggestionAdapter(infiniteSuggestions);
            suggestionsRecycler.setAdapter(adapter);

            // ✅ Setup carousel with middle index
            setupCarouselRecycler(suggestionsRecycler, middleIndex);

            // ✅ Scroll to middle index after layout is ready
            suggestionsRecycler.post(() -> {
                RecyclerView.LayoutManager layoutManager = suggestionsRecycler.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(middleIndex, 0);
                }
            });
        }

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
        GridLayout outerwearGrid = rootView.findViewById(R.id.outerwearGrid);

        setupSingleSelection(outfitTypeGrid, selectedTypeTag, tag -> selectedTypeTag = tag);
        setupSingleSelection(accessoriesGrid, selectedAccessoriesTag, tag -> selectedAccessoriesTag = tag);
        setupSingleSelection(outerwearGrid, selectedOuterwearTag, tag -> selectedOuterwearTag = tag);
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
        selectedAccessoriesTag = prefs.getString("accessories", selectedAccessoriesTag); // "no" by default
        selectedOuterwearTag = prefs.getString("outerwear", selectedOuterwearTag); // "no" by default

        // Save these back in case this is the first run (harmless if already exists)
        saveUserPreferences();
    }

    private void resetUserPreferencesToDefaults() {
        if (getContext() == null) return;

        SharedPreferences prefs = getContext().getSharedPreferences("user_filters", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("season", "spring");
        editor.putString("occasion", "sport");
        editor.putString("outfit_type", "one_piece");
        editor.putString("accessories", "no");
        editor.putString("outerwear", "no");
        editor.apply();
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
                .putString("outerwear", selectedOuterwearTag)
                .apply();
    }

    private void logUserPreferences() {
        if (getContext() == null) return;

        SharedPreferences prefs = getContext().getSharedPreferences("user_filters", getContext().MODE_PRIVATE);

        String season = prefs.getString("season", "not set");
        String occasion = prefs.getString("occasion", "not set");
        String type = prefs.getString("outfit_type", "not set");
        String accessories = prefs.getString("accessories", "not set");
        String outerwear = prefs.getString("outerwear", "not set");

        Log.d("USER_PREFERENCES", "Season: " + season);
        Log.d("USER_PREFERENCES", "Occasion: " + occasion);
        Log.d("USER_PREFERENCES", "Outfit Type: " + type);
        Log.d("USER_PREFERENCES", "Accessories: " + accessories);
        Log.d("USER_PREFERENCES", "Outerwear: " + outerwear);
    }

    //Carousel setup
    private void setupCarouselRecycler(RecyclerView recyclerView, int middleIndex) {
        int cardWidthPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                300,
                recyclerView.getResources().getDisplayMetrics()
        );
        int padding = (recyclerView.getResources().getDisplayMetrics().widthPixels - cardWidthPx) / 2;

        recyclerView.setPadding(padding, 0, padding, 0);
        recyclerView.setClipToPadding(false);
        recyclerView.setClipChildren(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        //new LinearSnapHelper().attachToRecyclerView(recyclerView);
        // Prevent multiple SnapHelpers attached
        if (recyclerView.getOnFlingListener() != null) {
            recyclerView.setOnFlingListener(null);
        }
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);


        // Scaling effect on scroll
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int center = recyclerView.getWidth() / 2;
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    View child = recyclerView.getChildAt(i);
                    int childCenter = (child.getLeft() + child.getRight()) / 2;
                    int distance = Math.abs(center - childCenter);
                    float scale = 1 - (distance / (float) center) * 0.15f;
                    scale = Math.max(0.85f, scale); // make side cards closer in size
                    child.setScaleX(scale);
                    child.setScaleY(scale);
                    child.setAlpha(scale); // optional fade
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                int first = lm.findFirstVisibleItemPosition();
                int last = lm.findLastVisibleItemPosition();
                if (first <= 2 || last >= (lm.getItemCount() - 2)) {
                    recyclerView.scrollToPosition(middleIndex);
                }
            }
        });
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0,1).toUpperCase() + input.substring(1);
    }





}
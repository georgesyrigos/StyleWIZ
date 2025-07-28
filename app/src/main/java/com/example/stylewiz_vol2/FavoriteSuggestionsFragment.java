package com.example.stylewiz_vol2;

import static android.view.View.GONE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FavoriteSuggestionsFragment extends Fragment {

    private RecyclerView favoritesRecycler;
    private FavoriteAdapter adapter;
    private List<FavoriteSuggestion> favoriteSuggestions;
    ImageView backButton;
    private LinearLayout emptyStateLayout, favoritesLinearLayout;
    private ProgressDialogHelper progressDialogHelper;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_suggestions, container, false);

        backButton = view.findViewById(R.id.back);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        favoritesLinearLayout = view.findViewById(R.id.favoritesLinearLayout);


        favoritesRecycler = view.findViewById(R.id.favoritesRecycler);
        favoritesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        favoriteSuggestions = new ArrayList<>();
        adapter = new FavoriteAdapter(favoriteSuggestions);
        favoritesRecycler.setAdapter(adapter);
        progressDialogHelper = new ProgressDialogHelper(requireContext());

        fetchFavoriteSuggestions();


        // Set up the click listener for back
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                // Find the existing SuggestionsFragment
                Fragment suggestionsFragment = fragmentManager.findFragmentByTag("SUGGESTIONS");

                if (suggestionsFragment != null) {
                    // Just show the existing fragment instead of recreating it
                    transaction.show(suggestionsFragment);
                }

                // Remove FavoriteSuggestionsFragment to ensure it's cleared
                transaction.remove(FavoriteSuggestionsFragment.this).commit();

                // Pop from the back stack to ensure proper navigation
                fragmentManager.popBackStack();

            }
        });

        return view;
    }

    private void fetchFavoriteSuggestions() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        // Show loading spinner
        progressDialogHelper.showProgressDialog(requireContext());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(user.getUid())
                .collection("selectedOutfits")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int previousSize = favoriteSuggestions.size();

                    List<FavoriteSuggestion> fetchedList = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String description = doc.getString("description");
                        String outfitHash = doc.getString("outfitHash");
                        Timestamp timestamp = doc.getTimestamp("timestamp");

                        List<Map<String, String>> imagesMapList = (List<Map<String, String>>) doc.get("images");
                        List<FavoriteImageItem> images = new ArrayList<>();

                        if (imagesMapList != null) {
                            for (Map<String, String> map : imagesMapList) {
                                String category = map.get("category");
                                String url = map.get("url");
                                images.add(new FavoriteImageItem(category, url));
                            }
                        }

                        FavoriteSuggestion suggestion = new FavoriteSuggestion(description, images, timestamp, outfitHash);
                        fetchedList.add(suggestion);
                    }

                    // Dismiss spinner before updating UI
                    progressDialogHelper.dismissProgressDialog();

                    // Update adapter
                    favoriteSuggestions.clear();
                    adapter.notifyItemRangeRemoved(0, previousSize);

                    favoriteSuggestions.addAll(fetchedList);
                    adapter.notifyItemRangeInserted(0, fetchedList.size());
                    // Toggle empty state visibility
                    if (fetchedList.isEmpty()) {
                        emptyStateLayout.setVisibility(View.VISIBLE);
                        favoritesLinearLayout.setVisibility(View.GONE);
                    } else {
                        emptyStateLayout.setVisibility(View.GONE);
                        favoritesLinearLayout.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

}
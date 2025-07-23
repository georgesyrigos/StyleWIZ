package com.example.stylewiz_vol2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class FavoriteSuggestionsFragment extends Fragment {

    private RecyclerView favoritesRecycler;
    private FavoriteAdapter adapter;
    private List<FavoriteSuggestion> favoriteSuggestions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_suggestions, container, false);

        favoritesRecycler = view.findViewById(R.id.favoritesRecycler);
        favoritesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        favoriteSuggestions = new ArrayList<>();
        adapter = new FavoriteAdapter(favoriteSuggestions);
        favoritesRecycler.setAdapter(adapter);

        fetchFavoriteSuggestions();

        return view;
    }

    private void fetchFavoriteSuggestions() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(user.getUid())
                .collection("selectedOutfits")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int previousSize = favoriteSuggestions.size();

                    List<FavoriteSuggestion> fetchedList = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        FavoriteSuggestion suggestion = doc.toObject(FavoriteSuggestion.class);

                        if (suggestion != null
                                && suggestion.getDescription() != null
                                && suggestion.getImageUrls() != null
                                && !suggestion.getImageUrls().isEmpty()) {

                            fetchedList.add(suggestion);
                        }
                    }

                    // Notify precise changes
                    favoriteSuggestions.clear();
                    adapter.notifyItemRangeRemoved(0, previousSize);

                    favoriteSuggestions.addAll(fetchedList);
                    adapter.notifyItemRangeInserted(0, fetchedList.size());
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }

}
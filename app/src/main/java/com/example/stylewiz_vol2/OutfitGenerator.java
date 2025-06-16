package com.example.stylewiz_vol2;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutfitGenerator {
    public interface OutfitCallback {
        void onResult(List<List<String>> outfits);
    }

    private static final String[] COLORS = {"black", "white", "gray", "red", "blue", "green", "yellow", "brown", "beige", "pink", "gold"};

    private static final Map<String, Map<String, Integer>> colorScores = new HashMap<>();

    static {
        // Compatibility scores between 10 basic colors (values 1 to 5)
        for (String base : COLORS) {
            colorScores.put(base, new HashMap<>());
            for (String other : COLORS) {
                if (base.equals(other)) {
                    colorScores.get(base).put(other, 5);
                } else {
                    colorScores.get(base).put(other, 3); // Simple average
                }
            }
        }
        // Manual tweaks
        colorScores.get("black").put("white", 5);
        colorScores.get("blue").put("white", 5);
        colorScores.get("red").put("green", 2);
        colorScores.get("yellow").put("gray", 2);
    }

    public static void generateOutfits(Context context, OutfitCallback callback) {
        SharedPreferences prefs = context.getSharedPreferences("user_filters", Context.MODE_PRIVATE);

        String season = prefs.getString("season", "winter");
        String style = prefs.getString("occasion", "casual");
        String outfitType = prefs.getString("outfit_type", "two_piece");
        boolean includeAccessories = prefs.getString("accessories", "0").equals("1");

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference
                wardrobeRef = FirebaseFirestore
                .getInstance()
                .collection("users").
                document(userId).
                collection("wardrobe");

        wardrobeRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("OutfitGenerator", "Failed to fetch wardrobe: " + task.getException());
                callback.onResult(Collections.emptyList());
                return;
            }

            Log.d("OutfitGenerator", "Fetched " + task.getResult().size() + " wardrobe items");

            List<Map<String, Object>> tops = new ArrayList<>();
            List<Map<String, Object>> bottoms = new ArrayList<>();
            List<Map<String, Object>> shoes = new ArrayList<>();
            List<Map<String, Object>> outerwear = new ArrayList<>();
            List<Map<String, Object>> onePieces = new ArrayList<>();
            List<Map<String, Object>> accessories = new ArrayList<>();

            for (QueryDocumentSnapshot doc : task.getResult()) {
                Map<String, Object> item = doc.getData();
                item.put("id", doc.getId());
                String category = (String) item.get("category");
                if (category == null) continue;

                category = category.toLowerCase(); // Normalize

                switch (category) {
                    case "top": tops.add(item); break;
                    case "bottom": bottoms.add(item); break;
                    case "shoes": shoes.add(item); break;
                    case "outerwear": outerwear.add(item); break;
                    case "accessories": accessories.add(item); break;
                    case "one-piece": onePieces.add(item); break;
                }
            }

            List<OutfitScore> scoredOutfits = new ArrayList<>();

            if (outfitType.equals("one_piece")) {
                for (Map<String, Object> one : onePieces) {
                    for (Map<String, Object> shoe : shoes) {
                        for (Map<String, Object> out : outerwear) {
                            List<Map<String, Object>> outfit = new ArrayList<>();
                            outfit.add(one);
                            outfit.add(shoe);
                            outfit.add(out);
                            if (includeAccessories && !accessories.isEmpty())
                                outfit.add(accessories.get(0));
                            int score = calculateScore(outfit, season, style);
                            scoredOutfits.add(new OutfitScore(outfit, score));
                        }
                    }
                }
            } else {
                for (Map<String, Object> top : tops) {
                    for (Map<String, Object> bottom : bottoms) {
                        for (Map<String, Object> shoe : shoes) {
                            for (Map<String, Object> out : outerwear) {
                                List<Map<String, Object>> outfit = new ArrayList<>();
                                outfit.add(top);
                                outfit.add(bottom);
                                outfit.add(shoe);
                                outfit.add(out);
                                if (includeAccessories && !accessories.isEmpty())
                                    outfit.add(accessories.get(0));
                                int score = calculateScore(outfit, season, style);
                                scoredOutfits.add(new OutfitScore(outfit, score));
                            }
                        }
                    }
                }
            }

            Collections.sort(scoredOutfits, Comparator.comparingInt(o -> -o.score));

            List<List<String>> topOutfitIds = new ArrayList<>();
            for (int i = 0; i < Math.min(3, scoredOutfits.size()); i++) {
                List<String> ids = new ArrayList<>();
                for (Map<String, Object> item : scoredOutfits.get(i).items) {
                    ids.add((String) item.get("id"));
                }
                topOutfitIds.add(ids);
            }

            Log.d("OutfitGenerator", "Top outfit count: " + topOutfitIds.size());
            callback.onResult(topOutfitIds);
        });
    }

    private static int calculateScore(List<Map<String, Object>> items, String season, String style) {
        int score = 0;
        for (Map<String, Object> item : items) {
            String itemSeason = (String) item.get("season");
            String itemStyle = (String) item.get("styleTag");
            String color = (String) item.get("color");

            if (isSeasonMatch(season, itemSeason)) score += 2;
            if (style.equals(itemStyle)) score += 2;
        }

        for (int i = 0; i < items.size(); i++) {
            for (int j = i + 1; j < items.size(); j++) {
                String colorA = (String) items.get(i).get("color");
                String colorB = (String) items.get(j).get("color");
                score += getColorScore(colorA, colorB);
            }
        }
        return score;
    }

    private static int getColorScore(String colorA, String colorB) {
        if (colorA == null || colorB == null) return 0;
        colorA = normalizeColor(colorA);
        colorB = normalizeColor(colorB);
        return colorScores.getOrDefault(colorA, new HashMap<>()).getOrDefault(colorB, 1);
    }

    private static String normalizeColor(String color) {
        color = color.toLowerCase();
        if (color.contains("white")) return "white";
        if (color.contains("black")) return "black";
        if (color.contains("gray") || color.contains("grey")) return "gray";
        if (color.contains("red")) return "red";
        if (color.contains("blue")) return "blue";
        if (color.contains("green")) return "green";
        if (color.contains("yellow")) return "yellow";
        if (color.contains("brown")) return "brown";
        if (color.contains("beige")) return "beige";
        if (color.contains("pink")) return "pink";
        return "gray"; // default fallback
    }

    private static boolean isSeasonMatch(String userSeason, String itemSeason) {
        if (itemSeason == null) return false;
        itemSeason = itemSeason.toLowerCase();

        switch (userSeason.toLowerCase()) {
            case "spring":
            case "summer":
                return itemSeason.contains("spring/summer") || itemSeason.contains("all");
            case "autumn":
            case "fall":
            case "winter":
                return itemSeason.contains("autumn/winter") || itemSeason.contains("fall/winter") || itemSeason.contains("all");
            case "all":
            default:
                return true; // Accept all if user selected "all"
        }
    }

    private static class OutfitScore {
        List<Map<String, Object>> items;
        int score;

        OutfitScore(List<Map<String, Object>> items, int score) {
            this.items = items;
            this.score = score;
        }
    }
}


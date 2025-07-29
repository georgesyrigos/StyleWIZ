package com.example.stylewiz_vol2;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutfitGenerator {
    public interface OutfitCallback {
        void onResult(List<List<String>> outfits);
    }

    private static final String[] COLORS = {
            "beige", "black", "blue", "brown", "gold",
            "gray", "green", "navy", "olive", "orange",
            "pink", "red", "silver", "violet", "white", "yellow"
    };

    private static final Map<String, List<String>> rankedMatches = new HashMap<>();

    private static final Map<String, Map<String, Integer>> colorScores = new HashMap<>();

    static {
        // Ranked matches (best to good) — max 6 per color
        rankedMatches.put("black", Arrays.asList("white", "gray", "red", "blue", "gold", "pink"));
        rankedMatches.put("white", Arrays.asList("black", "blue", "gray", "pink", "red", "gold"));
        rankedMatches.put("gray", Arrays.asList("white", "black", "blue", "pink", "red", "green"));
        rankedMatches.put("red", Arrays.asList("black", "white", "gray", "beige", "blue", "pink"));
        rankedMatches.put("blue", Arrays.asList("white", "gray", "black", "pink", "gold", "red"));
        rankedMatches.put("green", Arrays.asList("white", "beige", "brown", "gray", "black", "yellow"));
        rankedMatches.put("yellow", Arrays.asList("blue", "white", "gray", "brown", "black", "green"));
        rankedMatches.put("brown", Arrays.asList("beige", "green", "yellow", "white", "black", "gold"));
        rankedMatches.put("beige", Arrays.asList("brown", "white", "green", "gray", "black", "gold"));
        rankedMatches.put("pink", Arrays.asList("gray", "white", "black", "blue", "red", "gold"));
        rankedMatches.put("gold", Arrays.asList("black", "white", "blue", "pink", "brown", "beige"));
        rankedMatches.put("silver", Arrays.asList("black", "white", "gray", "blue", "pink", "navy"));
        rankedMatches.put("navy", Arrays.asList("white", "gray", "beige", "gold", "red", "silver"));
        rankedMatches.put("violet", Arrays.asList("gray", "white", "black", "pink", "silver", "blue"));
        rankedMatches.put("orange", Arrays.asList("white", "beige", "brown", "blue", "olive", "gray"));
        rankedMatches.put("olive", Arrays.asList("white", "beige", "brown", "orange", "black", "gray"));

        // Build the colorScores matrix with 7-point system
        for (String base : COLORS) {
            colorScores.put(base, new HashMap<>());
            List<String> ranked = rankedMatches.getOrDefault(base, new ArrayList<>());

            for (String other : COLORS) {
                int score;
                if (base.equals(other)) {
                    score = 7; // Exact match
                } else {
                    int index = ranked.indexOf(other);
                    score = (index >= 0 && index < 7) ? 7 - index : 1; // Ranked 7→1, else 1
                }
                colorScores.get(base).put(other, score);
            }
        }
    }


    public static void generateOutfits(Context context, OutfitCallback callback) {
        SharedPreferences prefs = context.getSharedPreferences("user_filters", Context.MODE_PRIVATE);

        String season = prefs.getString("season", "spring");
        String style = prefs.getString("occasion", "sport");
        String outfitType = prefs.getString("outfit_type", "one_piece");
        String outfitAccessories = prefs.getString("accessories", "no");
        String outfitOuterwear = prefs.getString("outerwear", "no");


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
                    case "accessory": accessories.add(item); break;
                    case "one-piece": onePieces.add(item); break;
                }
            }

            /*
            // Filter by season
            tops = filterBySeason(tops, season);
            bottoms = filterBySeason(bottoms, season);
            shoes = filterBySeason(shoes, season);
            onePieces = filterBySeason(onePieces, season);


            // Optional: filter accessories and outerwear separately

            List<Map<String, Object>> filteredAccessories = filterBySeason(accessories, season);
            List<Map<String, Object>> filteredOuterwear = filterBySeason(outerwear, season);

            // Check if we have enough to create outfits
            boolean hasEnough =
                (outfitType.equals("one_piece") && !onePieces.isEmpty() && !shoes.isEmpty()) ||
                (!outfitType.equals("one_piece") && !tops.isEmpty() && !bottoms.isEmpty() && !shoes.isEmpty());

            if (!hasEnough) {
                callback.onResult(Collections.singletonList(Collections.singletonList("NO_MATCH")));
                return;
            }
             */

            List<OutfitScore> scoredOutfits = new ArrayList<>();

            if (outfitType.equals("one_piece")) {
                for (Map<String, Object> one : onePieces) {
                    for (Map<String, Object> shoe : shoes) {
                            List<Map<String, Object>> outfit = new ArrayList<>();
                            outfit.add(one);
                            outfit.add(shoe);

                            // Optional: Add best outerwear based on color match
                            if (outfitOuterwear.equals("yes") && !outerwear.isEmpty()) {
                                Map<String, Object> bestOuterwear = findBestAccessoryOrOuterwear(outerwear, outfit, season, style);
                                if (bestOuterwear != null) outfit.add(bestOuterwear);
                            }

                            /*
                            if (outfitOuterwear.equals("yes") && !filteredOuterwear.isEmpty()) {
                                Map<String, Object> bestOuterwear = findBestAccessoryOrOuterwear(filteredOuterwear, outfit, season, style);
                                if (bestOuterwear != null) outfit.add(bestOuterwear);
                            }
                            */


                            // Optional: Add best accessory based on color match
                            if (outfitAccessories.equals("yes") && !accessories.isEmpty()) {
                                Map<String, Object> bestAccessory = findBestAccessoryOrOuterwear(accessories, outfit, season, style);
                                if (bestAccessory != null) outfit.add(bestAccessory);
                            }

                            /*
                            if (outfitAccessories.equals("yes") && !filteredAccessories.isEmpty()) {
                                Map<String, Object> bestAccessory = findBestAccessoryOrOuterwear(filteredAccessories, outfit, season, style);
                                if (bestAccessory != null) outfit.add(bestAccessory);
                            }
                             */

                            int score = calculateScore(outfit, season, style);
                            scoredOutfits.add(new OutfitScore(outfit, score));

                    }
                }
            } else {
                for (Map<String, Object> top : tops) {
                    for (Map<String, Object> bottom : bottoms) {
                        for (Map<String, Object> shoe : shoes) {
                                List<Map<String, Object>> outfit = new ArrayList<>();
                                outfit.add(top);
                                outfit.add(bottom);
                                outfit.add(shoe);
                                // Optional: Add best outerwear based on color match
                                if (outfitOuterwear.equals("yes") && !outerwear.isEmpty()) {
                                    Map<String, Object> bestOuterwear = findBestAccessoryOrOuterwear(outerwear, outfit, season, style);
                                    if (bestOuterwear != null) outfit.add(bestOuterwear);
                                }

                                /*
                                if (outfitOuterwear.equals("yes") && !filteredOuterwear.isEmpty()) {
                                    Map<String, Object> bestOuterwear = findBestAccessoryOrOuterwear(filteredOuterwear, outfit, season, style);
                                    if (bestOuterwear != null) outfit.add(bestOuterwear);
                                }
                                */

                                // Optional: Add best accessory based on color match
                                if (outfitAccessories.equals("yes") && !accessories.isEmpty()) {
                                    Map<String, Object> bestAccessory = findBestAccessoryOrOuterwear(accessories, outfit, season, style);
                                    if (bestAccessory != null) outfit.add(bestAccessory);
                                }

                                /*
                                if (outfitAccessories.equals("yes") && !filteredAccessories.isEmpty()) {
                                    Map<String, Object> bestAccessory = findBestAccessoryOrOuterwear(filteredAccessories, outfit, season, style);
                                    if (bestAccessory != null) outfit.add(bestAccessory);
                                }
                                 */

                                int score = calculateScore(outfit, season, style);
                                    scoredOutfits.add(new OutfitScore(outfit, score));

                        }
                    }
                }
            }

            Collections.sort(scoredOutfits, Comparator.comparingInt(o -> -o.score));

            List<List<String>> topOutfitIds = new ArrayList<>();
            for (int i = 0; i < Math.min(3, scoredOutfits.size()); i++) {
                OutfitScore os = scoredOutfits.get(i);
                List<String> idsWithScore = new ArrayList<>();
                for (Map<String, Object> item : os.items) {
                    idsWithScore.add((String) item.get("id"));
                }
                idsWithScore.add("score:" + os.score); // Append score at the end
                topOutfitIds.add(idsWithScore);
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

            if (isSeasonMatch(season, itemSeason)) score += 4;
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

    // Get the compatibility score between two colors (normalized)
    private static int getColorScore(String colorA, String colorB) {
        if (colorA == null || colorB == null) return 0;
        colorA = normalizeColor(colorA);
        colorB = normalizeColor(colorB);
        return colorScores.getOrDefault(colorA, new HashMap<>()).getOrDefault(colorB, 1);
    }

    // Normalize incoming color string to match internal keys
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
        if (color.contains("gold")) return "gold";
        return "gray"; // Default fallback
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

    private static Map<String, Object> findBestAccessoryOrOuterwear(List<Map<String, Object>> candidates, List<Map<String, Object>> outfit, String season, String style) {
        Map<String, Object> bestItem = null;
        int bestScore = -1;

        for (Map<String, Object> item : candidates) {
            String color = (String) item.get("color");
            if (color == null) continue;

            int colorScore = 0;
            for (Map<String, Object> existing : outfit) {
                String existingColor = (String) existing.get("color");
                colorScore += getColorScore(color, existingColor);
            }

            // Add style/season score just like in calculateScore()
            int seasonScore = isSeasonMatch(season, (String) item.get("season")) ? 2 : 0;
            int styleScore = style.equals(item.get("styleTag")) ? 2 : 0;

            int totalScore = colorScore + seasonScore + styleScore;

            if (totalScore > bestScore) {
                bestScore = totalScore;
                bestItem = item;
            }
        }
        return bestItem;
    }

    /*
    private static List<Map<String, Object>> filterBySeason(List<Map<String, Object>> items, String userSeason) {
        List<Map<String, Object>> filtered = new ArrayList<>();
        for (Map<String, Object> item : items) {
            if (isSeasonMatch(userSeason, (String) item.get("season"))) {
                filtered.add(item);
            }
        }
        return filtered;
    }
     */




    private static class OutfitScore {
        List<Map<String, Object>> items;
        int score;

        OutfitScore(List<Map<String, Object>> items, int score) {
            this.items = items;
            this.score = score;
        }
    }
}


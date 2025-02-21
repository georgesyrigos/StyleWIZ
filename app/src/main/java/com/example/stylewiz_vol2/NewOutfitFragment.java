package com.example.stylewiz_vol2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;


public class NewOutfitFragment extends Fragment {
    CheckBox chkAccessory, chkTop, chkOnePiece, chkLayerOnePiece;
    ImageView imgAccessory1, imgAccessory2, imgTop1, imgTop2, imgOnePiece, imgLayerOnePiece;
    LinearLayout topSection, bottomSection, onePieceSection;
    Switch switchOnePiece;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_outfit, container, false);

        //checkboxes
        chkAccessory = view.findViewById(R.id.chkSecondAccessory);
        chkTop = view.findViewById(R.id.chkSecondLayer);
        chkOnePiece = view.findViewById(R.id.chkOnePiece);
        chkLayerOnePiece = view.findViewById(R.id.chkLayerOnePiece);
        switchOnePiece = view.findViewById(R.id.switchOnePiece);

        //imageViews
        imgAccessory1 = view.findViewById(R.id.imgAccessory1);
        imgAccessory2 = view.findViewById(R.id.imgAccessory2);
        imgTop1 = view.findViewById(R.id.imgTop1);
        imgTop2 = view.findViewById(R.id.imgTop2);
        imgOnePiece = view.findViewById(R.id.imgOnePiece);
        imgLayerOnePiece =view.findViewById(R.id.imgLayerOnePiece);

        //Linear Layouts
        topSection = view.findViewById(R.id.topSection);
        bottomSection = view.findViewById(R.id.bottomSection);
        onePieceSection = view.findViewById(R.id.onePieceSection);




        // Set a listener on the CheckBox for Accessories
        chkAccessory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    imgAccessory2.setVisibility(View.VISIBLE);  // Show image if checked
                } else {
                    imgAccessory2.setVisibility(View.INVISIBLE);  // Hide image if unchecked
                }
            }
        });

        // Set a listener on the CheckBox for Tops
        chkTop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    imgTop2.setVisibility(View.VISIBLE);  // Show image if checked
                } else {
                    imgTop2.setVisibility(View.INVISIBLE);  // Hide image if unchecked
                }
            }
        });

        // Set a listener on the CheckBox for One-Piece
        chkLayerOnePiece.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    imgLayerOnePiece.setVisibility(View.VISIBLE);  // Show image if checked
                } else {
                    imgLayerOnePiece.setVisibility(View.INVISIBLE);  // Hide image if unchecked
                }
            }
        });


        // Set a listener on the CheckBox for One-Piece
        chkOnePiece.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    onePieceSection.setVisibility(View.VISIBLE);
                    topSection.setVisibility(View.GONE);
                    bottomSection.setVisibility(View.GONE);

                } else {
                    onePieceSection.setVisibility(View.GONE);
                    topSection.setVisibility(View.VISIBLE);
                    bottomSection.setVisibility(View.VISIBLE);
                }
            }
        });

        switchOnePiece.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    onePieceSection.setVisibility(View.VISIBLE);
                    topSection.setVisibility(View.GONE);
                    bottomSection.setVisibility(View.GONE);

                } else {
                    onePieceSection.setVisibility(View.GONE);
                    topSection.setVisibility(View.VISIBLE);
                    bottomSection.setVisibility(View.VISIBLE);
                }
            }
        });







        return view;
    }
}
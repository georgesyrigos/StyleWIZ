package com.example.stylewiz_vol2;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private ItemsAdapter mAdapter;
    private Context mContext;
    private Drawable deleteIcon;
    private Paint backgroundPaint;
    private boolean swipeEnabled = true;  // Flag to control swipe actions
    private boolean isManualUpdate = false;



    public SwipeToDeleteCallback(Context context, ItemsAdapter adapter) {
        super(0, ItemTouchHelper.LEFT); // Enable swipe left
        mContext = context;
        mAdapter = adapter;

        // Set up delete icon and background
        deleteIcon = ContextCompat.getDrawable(context, R.drawable.baseline_delete_24); // Ensure you add a delete icon in res/drawable
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.RED); // Red color for background
    }

    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false; // No drag-and-drop functionality needed
    }

    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition(); // Get the swiped item's position
        DataClass itemToDelete = mAdapter.getDataList().get(position); // Retrieve the item

        // Confirm deletion with an alert dialog
        new AlertDialog.Builder(mContext)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    isManualUpdate = true; // Lock snapshot updates
                    mAdapter.deleteItemAtPosition(position); // Notify adapter
                })
                .setNegativeButton("No", (dialog, which) -> {
                    mAdapter.notifyItemChanged(position); // Restore if canceled
                })
                .setCancelable(false)
                .show();
    }



    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        // Draw swipe background and delete icon only if swipe is enabled
        if (swipeEnabled) {
            View itemView = viewHolder.itemView;

            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                // Draw red background
                c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                        (float) itemView.getRight(), (float) itemView.getBottom(), backgroundPaint);

                // Draw delete icon
                int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + iconMargin;
                int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();

                int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
                int iconRight = itemView.getRight() - iconMargin;

                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                deleteIcon.draw(c);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}

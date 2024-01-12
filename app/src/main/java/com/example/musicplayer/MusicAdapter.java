package com.example.musicplayer;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.content.PermissionChecker.checkSelfPermission;


import android.annotation.SuppressLint;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.core.content.FileProvider;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.Manifest;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;



public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyVieHolder> {
    private static final int REQUEST_PERMISSION_CODE = 123; // You can use any value for the request code


    private Context mContext;
    static ArrayList<MusicFiles> mFiles;

    MusicAdapter(Context mContext, ArrayList<MusicFiles> mFiles)
    {
        this.mFiles = mFiles;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public MyVieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyVieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyVieHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());
        byte[] image = new byte[0];

        if (image != null)
        {
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.album_art);
        } else {
            Glide.with(mContext)
                    .load(R.drawable.cover)
                    .into(holder.album_art);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("position", position);
                mContext.startActivities(new Intent[]{intent});
            }
        });

        holder.menuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.delete) {
                            Toast.makeText(mContext, "Delete Clicked!!", Toast.LENGTH_SHORT).show();
                            //deleteFile(position, v);
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
    }

    //Delete Method
    /*private void deleteFile(int position, View v) {
        Log.d("DeleteFile", "deleteFile() method called for position: " + position);

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            // Permission is granted, you can proceed with deleting the file
            deleteFile(position, v);
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_CODE);
        }

        try {
            // Check external storage state
            String state = Environment.getExternalStorageState();
            Log.d("DeleteFile", "External storage state: " + state);

            // Get file path and create file object
            String filePath = mFiles.get(position).getPath();
            Log.d("DeleteFile", "File path: " + filePath);
            File file = new File(filePath);

            if (file.exists()) {
                Log.d("DeleteFile", "File exists at path: " + filePath);

                // Get the content URI for the file
                Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", file);

                // Attempt to delete the file
                boolean deleted = file.delete();
                Log.d("DeleteFile", "Deletion result: " + deleted);

                if (deleted) {
                    // File deleted successfully
                    mContext.getContentResolver().delete(contentUri, null, null);
                    mFiles.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mFiles.size());
                    Snackbar.make(v, "File Deleted", Snackbar.LENGTH_LONG).show();
                } else {
                    // Deletion failed
                    Log.e("DeleteFile", "Error deleting file: File.delete() returned false");
                    Snackbar.make(v, "Error deleting file", Snackbar.LENGTH_LONG).show();
                }
            } else {
                // File does not exist
                Log.d("DeleteFile", "File does not exist at path: " + filePath);
                Snackbar.make(v, "File not found", Snackbar.LENGTH_LONG).show();
            }

        } catch (SecurityException e) {
            Log.e("DeleteFile", "Security Exception: " + e.getMessage());
            Snackbar.make(v, "Security Exception: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("DeleteFile", "Exception: " + e.getMessage());
            Snackbar.make(v, "Error deleting file: " + e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }*/



    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class  MyVieHolder extends  RecyclerView.ViewHolder {
        TextView file_name;
        ImageView album_art, menuMore;

        public MyVieHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_file_name);
            album_art = itemView.findViewById(R.id.music_img);
            menuMore = itemView.findViewById(R.id.menuMore);

        }
    }

    private byte[] getAlbumArt(String uri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    void  updateList (ArrayList<MusicFiles> musicFilesArrayList) {
        mFiles = new ArrayList<>();
        mFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }


}

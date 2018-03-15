package com.example.spark.cleaniiitd.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spark.cleaniiitd.R;

import java.util.ArrayList;

/**
 * Created by spark on 16/3/18.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    private ArrayList<Uri> images;
    private Context mContext;

    public ImageAdapter(Context context) {
        images = new ArrayList<>();
        this.mContext = context;
    }

    public ImageAdapter(Context context, ArrayList<Uri> images) {
        this.images = images;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.washroomImage.setImageURI(images.get(position));
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        // downsizing image as it throws OutOfMemory Exception for larger images
//        options.inSampleSize = 8;
//        final Bitmap bitmap = BitmapFactory.decodeFile(images.get(position).getPath(), options);
//        holder.washroomImage.setImageBitmap(bitmap);
        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Clicked", Toast.LENGTH_SHORT).show();
                images.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void updateImageList(ArrayList<Uri> image_list) {
        images = image_list;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView washroomImage;
        private TextView deleteImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            washroomImage = itemView.findViewById(R.id.washroom_image);
            deleteImage = itemView.findViewById(R.id.delete_image);
        }
    }
}

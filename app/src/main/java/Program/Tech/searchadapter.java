package Program.Tech;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class searchadapter extends RecyclerView.Adapter<searchadapter.ItemViewHolder> {

    private List<PostItem> items;
    private Context context;

    public searchadapter(List<PostItem> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        PostItem item = items.get(position);
        holder.account.setText(item.getAccount());
        holder.title.setText(item.getTitle());
        holder.subtitle.setText(item.getSubtitle());

        // Decode Base64 image
        byte[] decodedString = Base64.decode(item.getImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.postImage.setImageBitmap(decodedByte);

        // Set click listener to open postOverview
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, postOverview.class);
            intent.putExtra("postID", item.getPostID());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage;
        TextView account;
        TextView title;
        TextView subtitle;

        public ItemViewHolder(View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.postimage);
            account = itemView.findViewById(R.id.account);
            title = itemView.findViewById(R.id.item_title);
            subtitle = itemView.findViewById(R.id.item_subtitle);
        }
    }

    public static class PostItem {
        private String image;
        private String account;
        private String title;
        private String subtitle;
        private String postID;

        public PostItem(String image, String account, String title, String subtitle, String postID) {
            this.image = image;
            this.account = account;
            this.title = title;
            this.subtitle = subtitle;
            this.postID = postID;
        }

        public String getImage() {
            return image;
        }

        public String getAccount() {
            return account;
        }

        public String getTitle() {
            return title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public String getPostID() {
            return postID;
        }
    }
}


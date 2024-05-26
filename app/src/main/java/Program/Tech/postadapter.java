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

public class postadapter extends RecyclerView.Adapter<postadapter.ViewHolder> {

    private List<PostItem> itemList;
    private Context context;
    private String username;

    public postadapter(List<PostItem> itemList, Context context, String username) {
        this.itemList = itemList;
        this.context = context;
        this.username = username;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PostItem item = itemList.get(position);
        holder.account.setText(item.getAccount());
        holder.title.setText(item.getTitle());
        holder.subtitle.setText(item.getSubtitle());

        // 将 Base64 图片字符串转换为 Bitmap
        byte[] decodedString = Base64.decode(item.getImageUrl(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.postImage.setImageBitmap(decodedByte);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, postOverview.class);
            intent.putExtra("postID", item.getPostID());
            intent.putExtra("username", username);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage;
        TextView account;
        TextView title;
        TextView subtitle;

        public ViewHolder(View itemView) {
            super(itemView);
            postImage = itemView.findViewById(R.id.postimage);
            account = itemView.findViewById(R.id.account);
            title = itemView.findViewById(R.id.item_title);
            subtitle = itemView.findViewById(R.id.item_subtitle);
        }
    }

    public static class PostItem {
        private String imageUrl;
        private String account;
        private String title;
        private String subtitle;
        private String postID;

        public PostItem(String imageUrl, String account, String title, String subtitle, String postID) {
            this.imageUrl = imageUrl;
            this.account = account;
            this.title = title;
            this.subtitle = subtitle;
            this.postID = postID;
        }

        public String getImageUrl() {
            return imageUrl;
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

package Program.Tech;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Homepage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_homepage);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 找到 ImageView 对象
        ImageView imageView = findViewById(R.id.image);
        // 设置动画资源为 ImageView 的背景
        imageView.setBackgroundResource(R.drawable.donation_show);
        // 将背景转换为 AnimationDrawable 对象
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        // 启动动画
        animationDrawable.start();
    }

    public void Test(View view) {
        startActivity(new Intent(this,Slideview.class));
    }
}

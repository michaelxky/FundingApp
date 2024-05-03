package Program.Tech;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


        public void loginUser(String username, String password){
            // 构建登录请求的URL
            String url = "https://studev.groept.be/api/a23PT414/Login_data/" + username + "/" + password;
            Log.d("urlString", url);

            // 创建一个请求队列
            RequestQueue requestQueue = Volley.newRequestQueue(this);

            // 创建一个JSON数组请求
            JsonArrayRequest queueRequest = new JsonArrayRequest(Request.Method.GET, url, null, responses -> {
                try {
                    // 处理服务器响应
                    if (responses.length() != 0) {
                        // 如果响应不为空，则认为登录成功
                        JSONObject response = responses.getJSONObject(0);

                        // 启动首页
                        Intent intent = new Intent(Main.this, Homepage.class);
                        startActivity(intent);
                        finish(); // 关闭当前登录页面
                    } else {
                        // 如果响应为空，则用户名或密码不正确
                        Toast.makeText(Main.this, "Incorrect username or password", Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    // JSON解析错误
                    throw new RuntimeException(e);
                }
            }, error -> {
                // 处理网络错误
                Log.d("VolleyError", error.toString());
                Toast.makeText(Main.this, "Unable to communicate with the server", Toast.LENGTH_LONG).show();
            });

            // 将请求加入到请求队列中
            requestQueue.add(queueRequest);
        }

        public void onLogin_Clicked(View view) {
            // 获取用户名和密码输入框的引用
            EditText usernameEditText = findViewById(R.id.username);
            EditText passwordEditText = findViewById(R.id.password);

            // 获取用户名和密码的文本值
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // 检查用户名和密码是否为空
            if (!username.isEmpty() && !password.isEmpty()) {
                // 调用登录方法
                loginUser(username, password);
            } else {
                // 用户名或密码为空，显示错误消息
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            }
        }



    public void CreateAccount(View view) {
        startActivity(new Intent(this, create_account.class));
    }

    public void Forget(View view) {
        startActivity(new Intent(this, security_page.class));
    }
}
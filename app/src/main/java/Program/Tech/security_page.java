package Program.Tech;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

public class security_page extends AppCompatActivity {
    EditText userNameEditText;
    EditText answerEditText;
    TextView questionTextView;
    String correctAnswerHash; // 保存正确答案的哈希值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_page);

        // 初始化视图
        userNameEditText = findViewById(R.id.Name);
        answerEditText = findViewById(R.id.Answer);
        questionTextView = findViewById(R.id.Question);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // 当用户点击获取问题按钮时调用
    public void Enter(View view) {
        // 获取用户输入的用户名
        String usernameString = userNameEditText.getText().toString().trim();
        if (usernameString.isEmpty()) {
            Toast.makeText(this, "Please enter a valid Username", Toast.LENGTH_SHORT).show();
            return;
        }

        // 调用方法来获取Security_Question
        Security(usernameString);
    }

    // 获取Security_Question的方法
    public void Security(String username) {
        String url = "https://studev.groept.be/api/a23PT414/security_info";
        Log.d("urlString", url);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest queueRequest = new JsonArrayRequest(Request.Method.GET, url, null, responses -> {
            try {
                // 遍历所有的JSON对象
                for (int i = 0; i < responses.length(); i++) {
                    JSONObject response = responses.getJSONObject(i);
                    // 检查Username是否匹配
                    if (response.getString("Name").equals(username)) {
                        // 获取Security Question 和正确的答案哈希值
                        String question = response.getString("Question");
                        correctAnswerHash = response.getString("Answer"); // 保存正确答案的哈希值
                        // 在页面上显示 Security_Question
                        questionTextView.setText(question);
                        return; // 找到匹配的Username后，跳出循环
                    }
                }
                // 如果未找到匹配的Username
                Toast.makeText(security_page.this, "Username not found", Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                Log.e("JSONException", "Error parsing JSON response: " + e.getMessage());
                Toast.makeText(security_page.this, "Error fetching security information", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Log.e("VolleyError", "Error communicating with server: " + error.getMessage());
            Toast.makeText(security_page.this, "Unable to communicate with the server", Toast.LENGTH_LONG).show();
        });
        requestQueue.add(queueRequest);
    }

    // 当用户点击提交按钮时调用
    public void Submit(View view) {
        // 获取用户输入的Answer
        String answer = answerEditText.getText().toString().trim();
        String username = userNameEditText.getText().toString().trim();
        // 验证答案是否正确
        if (correctAnswerHash != null && BCrypt.checkpw(answer, correctAnswerHash)) {
            // 答案正确，跳转到 Homepage
            Intent intent = new Intent(this, Slideview.class);
            intent.putExtra("username", username);
            startActivity(intent);
        } else {
            // 答案不正确，显示错误提示
            Toast.makeText(this, "Incorrect answer", Toast.LENGTH_SHORT).show();
        }
    }
}
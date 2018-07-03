package cn.edu.nuc.rechargedemo.register;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.edu.nuc.rechargedemo.R;

/**
 * 注册
 * Created by Administrator on 2018/6/25.
 */
public class RegisterActivity extends AppCompatActivity {
    private EditText student_number_edit = null;
    private EditText register_psd = null;
    private EditText register_psd2 = null;
    private Button schoolBtn = null;
    private TextView schoolTv = null;
    private Button re_register = null;
    private String student_number;//学号
    private String password;//密码
    private String school;//学校

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        Log.i("RegisterActivity", "RegisterActivity中的onCreate方法");
        student_number_edit = (EditText) findViewById(R.id.student_number);
        register_psd = (EditText) findViewById(R.id.register_psd);
        register_psd2 = (EditText) findViewById(R.id.register_psd2);
        schoolBtn = (Button) findViewById(R.id.schoolBtn);
        schoolTv = (TextView) findViewById(R.id.school);
        re_register = (Button) findViewById(R.id.re_register);

        schoolBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, ChooseSchoolActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        re_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String checkResult = checkInfo();
                if (checkResult != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle("出错提示！");
                    builder.setMessage(checkResult);
                    builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            register_psd.setText("");
                            register_psd2.setText("");
                        }
                    });
                    builder.create().show();
                } else {
                    LoginForGet(student_number,password,school);
                }
            }
        });
    }
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.i("AAAA", "得到的信息：" + msg.obj.toString());
                    try {
                        JSONObject json = new JSONObject(msg.obj.toString());
                        int status = json.getInt("status");
                        if(status == 1){
                            showMessage("访学号已经注册");
                        }else if(status == 2){
                            showMessage("注册成功");
                            Intent intent = new Intent(RegisterActivity.this, ResultActivity.class);
                            //向一下个页面传值
                            intent.putExtra("student_number",student_number);
                            intent.putExtra("school",school);
                            intent.putExtra("password",password);
                            startActivity(intent);
                            finish();
                        }else {
                            showMessage("注册失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    showMessage("请求失败");
                    break;
                case 3:
                    showMessage("请求异常");
                    break;
                default:
                    break;
            }
        }
    };
    public String checkInfo() {
        student_number = student_number_edit.getText().toString().trim();
        if (student_number.isEmpty()) {
            return "请输入学号";
        }
        password = register_psd.getText().toString().trim();
        if (password.length()<6 || password.length()>15) {
            return "您设置的密码应该在6-15之间！";
        }
        if (!password.equals(register_psd2.getText().toString().trim())) {
            return "您两次输入的密码不一致！";
        }
        school = schoolTv.getText().toString().trim();
        if(school.isEmpty()){
            return "请选择学校";
        }
        return null;

    }
    /**
     * 方法：发送get网络请求
     */
    private void LoginForGet(final String studentnumber, final String password, final String school) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //用HttpClient发送请求，分为五步
                //第一步：创建HttpClient对象
                final String path = "http://192.168.1.75:8080/MyChongZhiXITong/RegisterServlet?studentnumber=" + studentnumber+ "&password=" + password+"&school="+school;
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(5000);
                    conn.connect();
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        StringBuffer sbf = new StringBuffer();
                        InputStream is = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        String strRead = null;
                        while ((strRead = reader.readLine()) != null) {
                            sbf.append(strRead);
                            sbf.append("\r\n");
                        }
                        reader.close();
                        Message msg = Message.obtain();
                        msg.what = 1;
                        msg.obj = sbf.toString();
                        handler.sendMessage(msg);
                    } else {
                        Message msg = Message.obtain();
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what = 3;
                    handler.sendMessage(msg);
                }
            }
        }).start();//这个start()方法不要忘记了

    }
    /**
     * 弹出toast提示
     *
     * @param str
     */
    private void showMessage(String str) {
        Toast.makeText(RegisterActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("RegisterActivity", "RegisterActivity中的onStart方法");


    }

    protected void onResume() {
        super.onResume();
        Log.i("RegisterActivity", "RegisterActivity中的onResume方法");


    }

    protected void onPause() {

        super.onPause();
        Log.i("RegisterActivity", "RegisterActivity中的onPause方法");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("RegisterActivity", "RegisterActivity中的onStop方法");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("RegisterActivity", "RegisterActivity中的onRestart方法");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("RegisterActivity", "RegisterActivity中的onDestroy方法");

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 0) {
            Bundle bundle = data.getExtras();//将intent对象中的数据包取出来
            String resultCollege = bundle.getString("college");//将数据包中的key为city的值取出来
            schoolTv.setText(resultCollege);//修改文本框中的内容
        }
    }
}

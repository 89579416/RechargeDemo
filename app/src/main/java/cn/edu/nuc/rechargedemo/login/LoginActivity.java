package cn.edu.nuc.rechargedemo.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import cn.edu.nuc.rechargedemo.R;
import cn.edu.nuc.rechargedemo.utils.SharedPreferencesUtils;

/**
 * Created by Administrator on 2018/6/25.
 */

public class LoginActivity extends AppCompatActivity {
    private EditText login_stunumber = null;
    private EditText login_psd = null;
    private CheckBox rememberPsd = null;

    private Button lo_login = null;
    private boolean isRememberPsd = false;

    private String userName = null;
    private String userPsd = null;
    // private TextView userInfo = null;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initView();
    }

    /**
     * new 一个Handler来处理数据
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.i("AAAA", "得到的信息：" + msg.obj.toString());
                    try {
                        //new 一个JSONObject对象
                        JSONObject json = new JSONObject(msg.obj.toString());
                        //从JSONObject对象里面取值
                        int status = json.getInt("status");
                        if (status == 0) {
                            showMessage("登陆成功");
                            //登陆成功，保存学号，密码，是否记住状态
                            SharedPreferencesUtils.setValue(context, "name", userName);
                            SharedPreferencesUtils.setValue(context, "psd", userPsd);
                            SharedPreferencesUtils.setValue(context, "isRememberPsd", rememberPsd.isChecked());
                            Intent intent = new Intent(LoginActivity.this, FunctionActivity.class);
                            //向一下个页面传值
                            intent.putExtra("student_number", userName);
                            intent.putExtra("password", userPsd);
                            startActivity(intent);
                            finish();
                        } else {
                            showMessage("登陆失败，请检查学号和密码是否正确");
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



    /**
     * 初始化
     */
    private void initView() {
        setContentView(R.layout.login);
        login_stunumber = (EditText) findViewById(R.id.login_stunumber);
        login_psd = (EditText) findViewById(R.id.login_psd);
        rememberPsd = (CheckBox) findViewById(R.id.rememberpsd);
        lo_login = (Button) findViewById(R.id.lo_login);
        //从 SharedPreferences 中取出 是否保存账号密码的状态
        isRememberPsd = (Boolean) SharedPreferencesUtils.getValue(context, "isRememberPsd", false);
        //如果状态为true，
        if (isRememberPsd) {
            //则取出账号和密码并显示
            userName = (String) SharedPreferencesUtils.getValue(context, "name", "");
            userPsd = (String) SharedPreferencesUtils.getValue(context, "psd", "");
            // 显示到输入框
            login_stunumber.setText(userName);
            login_psd.setText(userPsd);
            rememberPsd.setChecked(true);
        }
        lo_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取得输入框里的值
                userName = login_stunumber.getText().toString().trim();
                userPsd = login_psd.getText().toString().trim();
                //非空判断，如为空则显示提示信息并终止往下执行
                if (userName.isEmpty()) {
                    showMessage("请输入学号");
                    return;
                }
                //非空判断，如为空则显示提示信息并终止往下执行
                if (userPsd.isEmpty()) {
                    showMessage("请输入密码");
                    return;
                }
                //传入参数调用后台API接口
                LoginForGet(userName, userPsd);
            }
        });
    }

    /**
     * 方法：发送get网络请求
     */
    private void LoginForGet(final String studentnumber, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                //get请求，参数跟在地址后面
                final String path = "http://192.168.1.75:8080/MyChongZhiXITong/LoginServlet?studentnumber=" + studentnumber + "&password=" + password;
                Log.i("AAAA","path="+path);
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(5000);
                    conn.connect();
                    int code = conn.getResponseCode();
                    if (code == 200) {//请求成功，状态为200
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
                        msg.what = 1;//通过该参数作后续处理
                        msg.obj = sbf.toString();//传递参数，在handler里面接收并作处理
                        handler.sendMessage(msg);
                    } else {//请求失败，其他状态
                        Message msg = Message.obtain();
                        msg.what = 2;//通过该参数作后续处理
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    // 请求发生异常，如没有网络，服务器异常
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what = 3;//通过该参数作后续处理
                    handler.sendMessage(msg);
                }
            }
        }).start();//这个start()方法不要忘记了

    }

    /**
     * 发送post请求
     *
     * @param studentnumber
     * @param userPsd
     */
    public void LoginForPost(final String studentnumber, final String userPsd) {
        final String path = "http://192.168.1.75:8080/MyChongZhiXITong/LoginServlet";
        new Thread() {
            public void run() {
                try {
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setRequestMethod("POST");
                    conn.setReadTimeout(5000);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    String data = "userName=" + URLEncoder.encode(userName, "utf-8") + "&userPsd=" + URLEncoder.encode(userPsd, "utf-8");
                    conn.setRequestProperty("Content-Length", String.valueOf(data.length()));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(data.getBytes());
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what = 3;
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 弹出toast提示
     *
     * @param str
     */
    private void showMessage(String str) {
        Toast.makeText(LoginActivity.this, str, Toast.LENGTH_SHORT).show();
    }
}




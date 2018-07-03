package cn.edu.nuc.rechargedemo.login;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.edu.nuc.rechargedemo.R;
import cn.edu.nuc.rechargedemo.utils.SharedPreferencesUtils;

/**
 * 充值
 * Created by Administrator on 2018/6/25.
 */

public class RechargeActivity extends Activity {
    private EditText money = null;
    private RadioButton card = null;
    private RadioButton alipay = null;
    private RadioButton wechat = null;
    private Button recharge = null;
    private String pay;
    private SharedPreferences loginPreference = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recharge);
        loginPreference = getSharedPreferences("login", Context.MODE_PRIVATE);
        money=(EditText)findViewById(R.id.money);
        card=(RadioButton)findViewById(R.id.card);
        alipay=(RadioButton)findViewById(R.id.alipay);
        wechat=(RadioButton)findViewById(R.id.wechat);
        recharge=(Button)findViewById(R.id.recharge);

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card.setChecked(true);
                alipay.setChecked(false);
                wechat.setChecked(false);
            }
        });
        alipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card.setChecked(false);
                alipay.setChecked(true);
                wechat.setChecked(false);
            }
        });
        wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                card.setChecked(false);
                alipay.setChecked(false);
                wechat.setChecked(true);
            }
        });

        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //请求接口充值
                pay = money.getText().toString().trim();
                if(!pay.isEmpty()) {
                    //获取登陆名和密码
                    String userName = (String) SharedPreferencesUtils.getValue(RechargeActivity.this,"name","");
                    String password =  (String) SharedPreferencesUtils.getValue(RechargeActivity.this,"psd","");
                    Log.i("AAAA","userName="+userName);
                    //如果登陆名不为空
                    if(!userName.isEmpty()){
                        rechargeForGet(userName,password,pay);
                    }
                }else {
                    showMessage("请输入金额");
                }
            }
        });

    }

    /**
     * 充值 方法：发送get网络请求
     */
    private void rechargeForGet(final String studentnumber,final String password,final String pay) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //用HttpClient发送请求，分为五步
                //第一步：创建HttpClient对象  RechargeServlet
                final String path = "http://192.168.1.75:8080/MyChongZhiXITong/RechargeServlet?studentnumber=" + studentnumber+ "&password=" + password+ "&pay=" + pay+ "&type=chongzhi";
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
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what = 3;
                    handler.sendMessage(msg);
                }
            }
        }).start();//这个start()方法不要忘记了

    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.i("AAAA","充值得到的信息："+msg.obj.toString());//{"status":0,"balances":200,"studentnumber":1001}
                    try {
                        //将返回的信息转成json对象后再解析出来
                        JSONObject json = new JSONObject(msg.obj.toString());
                        int status = json.getInt("status");
                        if(status == 1){
                            showMessage("充值失败");
                        }else {
                            showMessage("充值成功");
                            //充值成功后关闭该Activity回到上一个页面就可以点击按钮去查询余额了
                            finish();
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
     * 弹出toast提示
     * @param str
     */
    private void showMessage(String str){
        Toast.makeText(RechargeActivity.this,str,Toast.LENGTH_SHORT).show();
    }
}

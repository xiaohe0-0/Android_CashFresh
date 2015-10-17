package com.example.lh.android_cashfresh;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private EditText et_period;
    private EditText et_amount;
    private TextView text_state;
    private Button btn_ok;

    private int myPeriod;
    private int myAmount;
    private int totalANum;
    private Vibrator vibrator;
    private boolean vibrateSign = false;
    private int checkPeriod = 7;
    private int checkAmount = 10000;
    private int freshTime = 5000;
    private Handler handler;
    private String statusStr = "Starting";

    private final String urlPath = "https://list.lu.com/list/bianxiantong";
    private final String EXISTCODE = "YES";
    private final String DISCODE = "NO";
    private final String DISFINDCODE = "NO FIT";
    private final String NOTCONNECTED = "网络连接错误";
    private final String URLPATH = "https://list.lufax.com/list/bianxiantong?minMoney=&maxMoney=&minDays=&maxDays=&minRate=&maxRate=&mode=&trade=&isCx=&currentPage=1&orderType=days&orderAsc=true";
    private final String STARTPATH = "https://list.lu.com/list/all";
    private final String ASTATRTPATH = "特惠项目";
    private final String keyWordStr = "稳盈-变现通";
    private final String keyWordAPeriod = "投资期限";
    private final String keyWordANum = "product-amount";
    private final String keyWordANum2 = "num-style";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView)this.findViewById(R.id.webView);
        et_amount = (EditText) this.findViewById(R.id.edit_amount);
        et_period = (EditText)this.findViewById(R.id.edit_period);
        btn_ok = (Button)this.findViewById(R.id.btn_ok);
        text_state = (TextView)this.findViewById(R.id.text_state);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        webView.getSettings().setJavaScriptEnabled(true);
        WebSettings settings = webView.getSettings();
        settings.setUseWideViewPort(true);//设定支持viewport
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);//设定支持缩放
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.loadUrl(urlPath);

        btn_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String getPeriod = et_period.getText().toString().trim();
                String getAmount = et_amount.getText().toString().trim();

                try {
                    checkAmount = Integer.parseInt(getAmount);
                    checkPeriod = Integer.parseInt(getPeriod);
                    vibrateSign = false;
                    handler = new Handler();
                    handler.post(runnable);
                    Toast toast = Toast
                            .makeText(getApplicationContext(), "成功将投资金额修改为"
                                    + checkAmount + "元，投资时间修改为" + checkPeriod + "天，开始刷新", Toast.LENGTH_LONG);
                    toast.show();
                    btn_ok.setClickable(false);
                } catch (Exception ex) {
                    Toast toast = Toast
                            .makeText(getApplicationContext(), "输入有误", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    if (vibrateSign) {
                        long[] pattern = { 150, 400, 150, 400 }; // 停止 开启 停止 开启
                        vibrator.vibrate(pattern, 2); // 重复两次上面的pattern
                       // vibrator.cancel();
                        // 如果只想震动一次，index设为-1
                        handler.postDelayed(stopVibrate,1500);
                    } else {
                        vibrator.cancel();
                    }
                    SimpleDateFormat formatter = new SimpleDateFormat(
                            "HH:mm:ss");
                    Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
                    String curTime = formatter.format(curDate);
                    text_state.setText(curTime + " " + statusStr);
                    webView.reload();
                    break;
                default:
                    break;
            }
        };
    };
    private Runnable runnable = new Runnable() {
        public void run() {
            if(!vibrateSign) {
                new Thread() {
                    public void run() {
                        statusStr = getWebCon(URLPATH);
                        mHandler.sendEmptyMessage(1);
                    };
                }.start();
                handler.postDelayed(this, freshTime);
            }
            else{
                btn_ok.setClickable(true);
            }
        }
    };

    private Runnable stopVibrate = new Runnable() {
        @Override
        public void run() {
            vibrator.cancel();
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getWebCon(String url_path) {
       // StringBuffer sb = new StringBuffer();
        try {
            java.net.URL url = new java.net.URL(url_path);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    url.openStream(), "UTF-8"));
            String line;
            String tmp;
            int spos, endPos;
            String periodStr;
            String subStr;
            while ((line = in.readLine()) != null) {
                if (line.indexOf(STARTPATH) > 0) {
                    while ((tmp = in.readLine()) != null) {
                        if (tmp.indexOf(keyWordStr) > 0)
                        {
                            int start = 0;
                            if ((start = tmp.indexOf("<em>")) > 0) {
                                int end = tmp.indexOf("</em>");
                                String numStr = tmp.substring(start + 5,
                                        end - 1);
                                totalANum = Integer.parseInt(numStr);
                                //return keyWordStr + ": " + EXISTCODE + ": " +
                                //numStr;
                            } else {
                                totalANum = 0;
                                return DISCODE;
                            }
                            break;
                        }
                    }
                    // break;
                }
                Thread.sleep(3);
                if (line.indexOf(ASTATRTPATH) > 0) {
                    int count = 0;
                   vibrateSign = false;
                    while ((tmp = in.readLine()) != null && count < totalANum) {
                        if (tmp.indexOf(keyWordStr) > 0) {
                            spos = tmp.indexOf(keyWordStr);
                            //sb.append(tmp.substring(spos, spos + 17) + " : \n");
                            //sb.append(tmp);
                            String readStr;
                            while ((readStr = in.readLine()) != null) {
                                if (readStr.indexOf(keyWordAPeriod) > 0) {
                                    periodStr = in.readLine().trim();

                                    spos = periodStr.indexOf("p") + 2;
                                    endPos = periodStr.indexOf("天");
                                    subStr = periodStr.substring(spos,
                                            endPos);
                                    myPeriod = Integer.parseInt(subStr.trim());
                                    //sb.append(subStr + "天\t    ");
                                    //sb.append(periodStr+"\t");
                                }
                                if (readStr.indexOf(keyWordANum) > 0) {
                                    tmp = in.readLine();
                                    while(tmp.indexOf(keyWordANum2) < 0){
                                        tmp = in.readLine();
                                    }
                                    periodStr = tmp.trim();
                                    spos = periodStr.indexOf("sty") + 7;
                                    endPos = periodStr.indexOf(".");
                                    myAmount = Integer.parseInt(periodStr
                                            .substring(spos, endPos).replace(
                                                    ",", ""));
                                    //sb.append(myAmount + "元\n");

                                    if (myAmount <= checkAmount
                                            && myPeriod <= checkPeriod) {
                                        vibrateSign = true;
                                    }
                                    break;
                                }
                            }
                            count++;
                            Thread.sleep(2);
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        //return keyWordStr + ": " + EXISTCODE + ": " + totalANum + "\n"
                //+ sb.toString();

        if(vibrateSign) return EXISTCODE;
        return DISFINDCODE;
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        vibrator.cancel();
    }
}

package com.example.android_cashfresh;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	private TextView tv;
	private RadioGroup rBtnG_time;
	private RadioButton rBtn_time5;
	private RadioButton rBtn_time15;
	private RadioButton rBtn_time25;
	private Button btn_period;
	private Button btn_amount;
	private EditText et_period;
	private EditText et_amount;

	private int myPeriod;
	private int myAmount;
	private int totalANum;
	private Vibrator vibrator;
	private boolean vibrateSign = false;
	private int checkPeriod = 7;
	private int checkAmount = 10000;
	private int freshTime = 5000;
	private Handler handler;
	private String statusStr = "";

	public static String EXISTCODE = "YES";
	public static String DISCODE = "NO";
	public static String ERRORCODE = "ERROR";
	public static String NOTCONNECTED = "网络连接错误";
	public static String URLPATH = "https://list.lufax.com/list/bianxiantong?minMoney=&maxMoney=&minDays=&maxDays=&minRate=&maxRate=&mode=&trade=&isCx=&currentPage=1&orderType=days&orderAsc=true";
	//public static String URLPATH = "https://list.lu.com/list/fuying?minMoney=&maxMoney=&minDays=&maxDays=&minRate=&maxRate=&mode=&subType=&instId=&haitongGrade=&fundGroupId=&trade=&isCx=&currentPage=1&orderType=days&orderAsc=true";
	//public static String STARTPATH = "https://list.lu.com/list/touzi";
	public static String STARTPATH = "https://list.lu.com/list/all";
	public static String ASTATRTPATH = "特惠项目";
	public static String keyWordStr = "稳盈-变现通";
	//public static String keyWordStr = "富盈人生";
	public static String keyWordAPeriod = "投资期限";
	public static String keyWordANum = "product-amount";
	public static String keyWordANum2 = "num-style";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView) this.findViewById(R.id.textView1);
		rBtnG_time = (RadioGroup) this.findViewById(R.id.radioGroup2);
		rBtn_time5 = (RadioButton) this.findViewById(R.id.radio_time5);
		rBtn_time15 = (RadioButton) this.findViewById(R.id.radio_time15);
		rBtn_time25 = (RadioButton) this.findViewById(R.id.radio_time25);
		btn_period = (Button) this.findViewById(R.id.btn_period);
		btn_amount = (Button) this.findViewById(R.id.btn_amount);
		et_period = (EditText) this.findViewById(R.id.edit_period);
		et_amount = (EditText) this.findViewById(R.id.edit_amount);

		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		btn_period.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String getStr = et_period.getText().toString();
				try {
					checkPeriod = Integer.parseInt(getStr);
					Toast toast = Toast
							.makeText(getApplicationContext(), "成功将投资时间修改为"
									+ checkPeriod + "天", Toast.LENGTH_LONG);
					toast.show();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});

		btn_amount.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String getStr = et_amount.getText().toString();
				try {
					checkAmount = Integer.parseInt(getStr);
					Toast toast = Toast
							.makeText(getApplicationContext(), "成功将投资金额修改为"
									+ checkAmount + "元", Toast.LENGTH_LONG);
					toast.show();
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});

		rBtnG_time
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkid) {
						// TODO Auto-generated method stub
						if (checkid == rBtn_time5.getId()) {
							freshTime = 5000;
						} else if (checkid == rBtn_time15.getId()) {
							freshTime = 15000;
						} else if (checkid == rBtn_time25.getId()) {
							freshTime = 25000;
						}
					}
				});

		handler = new Handler();
		handler.postDelayed(runnable, freshTime);
	}

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (vibrateSign) {
					long[] pattern = { 50, 500, 50, 200 }; // 停止 开启 停止 开启
					vibrator.vibrate(pattern, 2); // 重复两次上面的pattern
					// 如果只想震动一次，index设为-1
				} else {
					vibrator.cancel();
				}
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日    HH:mm:ss  ");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
				String curTime = formatter.format(curDate);
				tv.setText(curTime + "\n" + statusStr + "\n");
				break;

			default:
				break;
			}
		};
	};
	private Runnable runnable = new Runnable() {
		public void run() {
			new Thread() {
				public void run() {
					statusStr = getWebCon(URLPATH);
					mHandler.sendEmptyMessage(1);
				};
			}.start();
			handler.postDelayed(this, freshTime);
		}

	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		vibrator.cancel();
	}

	public String getWebCon(String url_path) {
		StringBuffer sb = new StringBuffer();

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
								return keyWordStr + ": " + DISCODE;
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
							sb.append(tmp.substring(spos, spos + 17) + " : \n");
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
									sb.append(subStr + "天\t    ");
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
									sb.append(myAmount + "元\n");

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

		return keyWordStr + ": " + EXISTCODE + ": " + totalANum + "\n"
				+ sb.toString();
	}
}

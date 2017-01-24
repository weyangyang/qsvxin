﻿package com.qsvxin;

import java.io.File;
import java.io.IOException;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.igexin.sdk.PushManager;
import com.qavxin.slidemenu.SlidingMenu;
import com.qsvxin.bean.LoginBean;
import com.qsvxin.twocode.CaptureActivity;
import com.qsvxin.utils.ActivityManager;
import com.qsvxin.utils.CommonUtils;
import com.qsvxin.utils.FileMgr;
import com.qsvxin.utils.PreferenceUtils;
import com.qsvxin.utils.QsConstants;
import com.qsvxin.utils.TakePhoto;

public class MainUIActivity extends TabActivity implements OnClickListener {
	public static MainUIActivity instance = null;
	private SlidingMenu slideMenu;
	private TextView textv_welUserName;
	private Button btnRight;
	private TabHost mTabHost;
	private TabSpec spec;
	private ImageView iv_cursor;
	private int widgetW = 0, currIndex = 0;
	private TextView textv_mainTitle;
	private TextView textv_vOrderForm, textv_vGroupBuy, textv_vChat,
			textv_vMembers;
	public static final String TAB_ID_VORDER_FORM = "vOrderForm";
	private static final String TAB_ID_VGROUP_BUY = "vGroupBuy";
	private static final String TAB_ID_VCHAT = "vChat";
	private static final String TAB_ID_VMEMBERS = "vMembers";
	private TakePhoto mTakePhoto;
	private File headPhotoFile = null;
	
//	private TextDialog mUpdateDialog;
//	private Notification notification;
//	private RemoteViews remoteViews;
//	private NotificationManager notificationManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		ActivityManager.add(this);
		setContentView(R.layout.tab_activity_main);
		PreferenceUtils.getLoginLoginAfBean(this);
		// 启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		iv_cursor = (ImageView) findViewById(R.id.iv_cursor);
		initSlideMenu();
		initTitleBar();
		InitImageView();
		
		mTabHost = getTabHost();
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				changedTabShow(tabId);
				int arg0 = 0;
				if (widgetW == 0) {
					widgetW = mTabHost.getTabWidget().getWidth() / 4;
				}
				if (tabId.equals(TAB_ID_VORDER_FORM)) {
					arg0 = 0;
				} else if (tabId.equals(TAB_ID_VGROUP_BUY)) {
					arg0 = 1;
				} else if (tabId.equals(TAB_ID_VCHAT)) {
					arg0 = 2;
				} else if (tabId.equals(TAB_ID_VMEMBERS)) {
					arg0 = 3;
				}
				Animation animation = new TranslateAnimation(widgetW
						* currIndex, widgetW * arg0, 0, 0);
				currIndex = arg0;
				animation.setFillAfter(true);/* True:图片停在动画结束位置 */
				animation.setDuration(300);
				iv_cursor.startAnimation(animation);
				setImageViewWidth(widgetW);
			}
		});
		addVorderFormTab();
		addVgroupBuyTab();
		addVchatTab();
		addVmembersTab();
		setDefaultTab(0);

		long longLastUpdateTime = PreferenceUtils.getPrefLong(MainUIActivity.this, "lastUpdateTime", -1L);
		if ((longLastUpdateTime + (QsConstants.ONE_DAY)) < System
				.currentTimeMillis()) {
			PreferenceUtils.setPrefLong(MainUIActivity.this, "lastUpdateTime", System
					.currentTimeMillis());
			CommonUtils.checkVersion(this, false, false);
		}

		/**
		 * login
		 */
		// "{\"errcode\":10000,\"errmsg\":\"ok\",\"user\":{\"secretkey\":\"qPC8sd3VvK@BtyvIy9JrUw==\",\"nickname\":\"李君\",\"token\":\"meiyoutoken\",\"appid\":\"wxbd33effb7ab02764\",\"appsecret\":\"abb99fb43e432b7bce6b018d7dc3ed06\",\"gh\":\"gh_9744618c81ed\"}}"
		// String strJson =
		// "{\"errcode\":10000,\"errmsg\":\"ok\",\"data\":[{\"id\":2,\"openid\":\"oV7Hyt3hY61PktA-IHMFTbKqtBZ0\",\"isjoin\":true,\"isvip\":true,\"name\":\"李君\",\"phone\":\"15949441672\",\"sex\":\"男\",\"age\":20,\"birthday\":\"2014-04-01\",\"explain\":\"\",\"cardid\":1000000,\"rc\":6,\"uc\":0},{\"id\":3,\"openid\":\"oV7HytwO2bEJA5xOJ_T8x9urFBSw\",\"isjoin\":true,\"isvip\":false,\"name\":\"\",\"phone\":\"\",\"sex\":\"男\",\"age\":20,\"birthday\":\"2014-04-01\",\"explain\":\"\",\"cardid\":0,\"rc\":1,\"uc\":0},{\"id\":4,\"openid\":\"oV7Hyt6SQv8OZC4XSOL2nZHhLrVA\",\"isjoin\":true,\"isvip\":false,\"name\":\"\",\"phone\":\"\",\"sex\":\"男\",\"age\":20,\"birthday\":\"2014-04-01\",\"explain\":\"\",\"cardid\":0,\"rc\":0,\"uc\":0},{\"id\":5,\"openid\":\"oV7HytzgmiLdcCOJfhs0Py6G64n4\",\"isjoin\":true,\"isvip\":false,\"name\":\"\",\"phone\":\"\",\"sex\":\"男\",\"age\":20,\"birthday\":\"2014-04-01\",\"explain\":\"\",\"cardid\":0,\"rc\":0,\"uc\":0},{\"id\":6,\"openid\":\"oV7Hyt3zIVsI7zWIR3u8k2OmUXm0\",\"isjoin\":true,\"isvip\":false,\"name\":\"\",\"phone\":\"\",\"sex\":\"男\",\"age\":20,\"birthday\":\"2014-04-02\",\"explain\":\"\",\"cardid\":0,\"rc\":0,\"uc\":0},{\"id\":35,\"openid\":\"oV7Hyt3rJ6n7trnFUsYaHiNmKtVc\",\"isjoin\":false,\"isvip\":false,\"name\":\"\",\"phone\":\"\",\"sex\":\"男\",\"age\":20,\"birthday\":\"2014-04-29\",\"explain\":\"\",\"cardid\":0,\"rc\":0,\"uc\":0},{\"id\":41,\"openid\":\"oV7Hytx41Jc3S-4QmES_QNpOmkqE\",\"isjoin\":false,\"isvip\":false,\"name\":\"\",\"phone\":\"\",\"sex\":\"男\",\"age\":20,\"birthday\":\"2014-05-05\",\"explain\":\"\",\"cardid\":0,\"rc\":0,\"uc\":0},{\"id\":43,\"openid\":\"oV7Hyt5hlKTnV0iSBsKygB93u15Y\",\"isjoin\":false,\"isvip\":false,\"name\":\"\",\"phone\":\"\",\"sex\":\"男\",\"age\":20,\"birthday\":\"2014-05-06\",\"explain\":\"\",\"cardid\":0,\"rc\":0,\"uc\":0},{\"id\":48,\"openid\":\"oV7Hyt8uWPyIBFAtawCpjlXdC_Xw\",\"isjoin\":true,\"isvip\":false,\"name\":\"\",\"phone\":\"\",\"sex\":\"男\",\"age\":20,\"birthday\":\"2014-05-13\",\"explain\":\"\",\"cardid\":0,\"rc\":0,\"uc\":0},{\"id\":63,\"openid\":\"oV7Hyt0_f0jYguMksff2UnOy-Ojo\",\"isjoin\":false,\"isvip\":false,\"name\":\"\",\"phone\":\"\",\"sex\":\"男\",\"age\":20,\"birthday\":\"2014-05-18\",\"explain\":\"\",\"cardid\":0,\"rc\":0,\"uc\":0},{\"id\":82,\"openid\":\"oV7Hyt3Q7bm1K5eQ2Bx0kQiwGo4o\",\"isjoin\":true,\"isvip\":false,\"name\":\"\",\"phone\":\"\",\"sex\":\"男\",\"age\":20,\"birthday\":\"2014-05-21\",\"explain\":\"\",\"cardid\":0,\"rc\":0,\"uc\":0},{\"id\":83,\"openid\":\"oV7Hyt5qutSGYk25teEgIkx-NpdY\",\"isjoin\":false,\"isvip\":false,\"name\":\"\",\"phone\":\"\",\"sex\":\"男\",\"age\":20,\"birthday\":\"2014-05-21\",\"explain\":\"\",\"cardid\":0,\"rc\":0,\"uc\":0},{\"id\":3208,\"openid\":\"oV7Hyt2XoIQoaDuwfcE7uLnVL5Uw\",\"isjoin\":true,\"isvip\":false,\"name\":\"\",\"phone\":\"\",\"sex\":\"男\",\"age\":20,\"birthday\":\"2014-09-04\",\"explain\":\"\",\"cardid\":0,\"rc\":0,\"uc\":0},{\"id\":3580,\"openid\":\"oV7Hyt0st6Km2XdlHUvEeBHXbx_U\",\"isjoin\":true,\"isvip\":false,\"name\":\"\",\"phone\":\"\",\"sex\":\"男\",\"age\":20,\"birthday\":\"2014-09-13\",\"explain\":\"\",\"cardid\":0,\"rc\":0,\"uc\":0},{\"id\":3826,\"openid\":\"oPW6BuNEElhYwLs5vww1lmNY2PN8\",\"isjoin\":true,\"isvip\":false,\"name\":\"\",\"phone\":\"\",\"sex\":\"男\",\"age\":20,\"birthday\":\"2014-09-17\",\"explain\":\"\",\"cardid\":0,\"rc\":0,\"uc\":0},{\"id\":4086,\"openid\":\"oV7Hyt8_57wgmjXhyYzFJ0CaxHXc\",\"isjoin\":true,\"isvip\":true,\"name\":\"李世民\",\"phone\":\"13842349988\",\"sex\":\"男\",\"age\":20,\"birthday\":\"2014-09-22\",\"explain\":\"\",\"cardid\":1000001,\"rc\":0,\"uc\":0}]}";
		// String strJson =
		// "{\"errcode\":10000,\"errmsg\":\"ok\",\"data\":[{\"id\":19,\"openid\":\"oV7Hyt3hY61PktA-IHMFTbKqtBZ0\",\"money\":3100,\"creattime\":\"2014-09-05 10:50:46\",\"paytime\":\"1990-01-01 00:00:00\",\"distrtime\":\"1990-01-01 00:00:00\",\"finishtime\":\"1990-01-01 00:00:00\",\"ofstatus\":1,\"paystatus\":2,\"distrstatus\":2,\"paymode\":1,\"text\":\"测试数据\",\"buyername\":\"李君\",\"buyerphone\":\"15940441675\",\"buyeraddress\":\"沈阳市和平区长白西路\",\"coms\":[{\"comid\":1,\"norms\":[{\"name\":\"黑\",\"value\":0}],\"count\":1}]},{\"id\":21,\"openid\":\"oV7Hyt3hY61PktA-IHMFTbKqtBZ0\",\"money\":3100,\"creattime\":\"2014-09-19 18:12:17\",\"paytime\":\"1990-01-01 00:00:00\",\"distrtime\":\"1990-01-01 00:00:00\",\"finishtime\":\"1990-01-01 00:00:00\",\"ofstatus\":1,\"paystatus\":2,\"distrstatus\":2,\"paymode\":1,\"text\":\"\",\"buyername\":\"李君\",\"buyerphone\":\"15940441675\",\"buyeraddress\":\"沈阳市和平区长白西路\",\"coms\":[{\"comid\":1,\"norms\":[{\"name\":\"黑\",\"value\":0}],\"count\":1}]},{\"id\":22,\"openid\":\"oV7Hyt3hY61PktA-IHMFTbKqtBZ0\",\"money\":4688,\"creattime\":\"2014-09-19 18:12:32\",\"paytime\":\"1990-01-01 00:00:00\",\"distrtime\":\"1990-01-01 00:00:00\",\"finishtime\":\"1990-01-01 00:00:00\",\"ofstatus\":1,\"paystatus\":2,\"distrstatus\":2,\"paymode\":1,\"text\":\"\",\"buyername\":\"李君\",\"buyerphone\":\"15940441675\",\"buyeraddress\":\"沈阳市和平区长白西路\",\"coms\":[{\"comid\":2,\"norms\":[{\"name\":\"黑\",\"value\":0},{\"name\":\"16G\",\"value\":0}],\"count\":1}]},{\"id\":23,\"openid\":\"oV7Hyt3hY61PktA-IHMFTbKqtBZ0\",\"money\":4688,\"creattime\":\"2014-09-19 18:12:54\",\"paytime\":\"1990-01-01 00:00:00\",\"distrtime\":\"1990-01-01 00:00:00\",\"finishtime\":\"1990-01-01 00:00:00\",\"ofstatus\":1,\"paystatus\":2,\"distrstatus\":2,\"paymode\":1,\"text\":\"\",\"buyername\":\"李君\",\"buyerphone\":\"15940441675\",\"buyeraddress\":\"沈阳市和平区长白西路\",\"coms\":[{\"comid\":2,\"norms\":[{\"name\":\"黑\",\"value\":0},{\"name\":\"16G\",\"value\":0}],\"count\":1}]}]}";
		// String strJson
		// ="{\"errcode\":10000,\"errmsg\":\"ok\",\"data\":[{\"id\":255,\"title\":\"团购测试\"},{\"id\":305,\"title\":\"团购2\"}]}";
		// String strJson =
		// "{\"errcode\":10000,\"errmsg\":\"ok\",\"data\":[{\"id\":331,\"name\":\"李君\",\"phone\":\"15940441672\",\"count\":1,\"sn\":\"fbae1355-da17-4ae4-b5a3-380b55569774\",\"state\":1},{\"id\":375,\"name\":\"曹操\",\"phone\":\"1309999999\",\"count\":1,\"sn\":\"6d88b0a2-5624-4cbc-ad71-94944b8b3a49\",\"state\":1},{\"id\":376,\"name\":\"妹妹\",\"phone\":\"17888888888\",\"count\":1,\"sn\":\"c7a2a2db-cd3d-4569-9e4d-c66dadc78797\",\"state\":1}]}";
		// try {
		// ParserEngine.getInstance().tess(strJson);
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		headPhotoFile = new File(this.getFilesDir(), "bakcground.png");
		try {
			headPhotoFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileMgr.chmod(headPhotoFile.getAbsolutePath(), "777");
		mTakePhoto = new TakePhoto(this, new TakePhoto.PhotoResult() {
			@Override
			public void onPhotoResult(File outputPath) {
				PreferenceUtils.setPrefString(MainUIActivity.this,
						"background", headPhotoFile.getAbsolutePath());
				Toast.makeText(MainUIActivity.this, "背景更换成功", Toast.LENGTH_LONG).show();
			}
		}, headPhotoFile);
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mTakePhoto.onActivityResult(requestCode, resultCode, data);
	}

	/* 初始化动画 */
	private void InitImageView() {
		Matrix matrix = new Matrix();
		matrix.postTranslate(0, 0);
		iv_cursor.setImageMatrix(matrix);// 设置动画初始位置
	}

	/**
	 *  设置图片宽度
	 * @param width
	 */
	private void setImageViewWidth(int width) {
		if (width != iv_cursor.getWidth()) {
			android.widget.RelativeLayout.LayoutParams laParams = (android.widget.RelativeLayout.LayoutParams) iv_cursor
					.getLayoutParams();
			laParams.width = width;
			iv_cursor.setLayoutParams(laParams);
		}
	}

	private void addVmembersTab() {
		Intent intentCount = new Intent(this, VmembersActivity.class);
		Drawable countDrawable = getResources().getDrawable(
				R.drawable.tab_vmembers);
		View viewCount = inittabwidget(countDrawable,
				getResources().getString(R.string.str_vmembers));
		spec = mTabHost.newTabSpec(TAB_ID_VMEMBERS).setIndicator(viewCount)
				.setContent(intentCount);
		mTabHost.addTab(spec);
	}

	private void addVchatTab() {
		Intent intentCount = new Intent(this, VchatActivity.class);
		Drawable countDrawable = getResources().getDrawable(
				R.drawable.tab_vchat);
		View viewCount = inittabwidget(countDrawable,
				getResources().getString(R.string.str_vchat));
		spec = mTabHost.newTabSpec(TAB_ID_VCHAT).setIndicator(viewCount)
				.setContent(intentCount);
		mTabHost.addTab(spec);
	}

	private void addVgroupBuyTab() {
		Intent intentCount = new Intent(this, VgroupBuyActivity.class);
		Drawable countDrawable = getResources().getDrawable(
				R.drawable.tab_vgroupbuy);
		View viewCount = inittabwidget(countDrawable,
				getResources().getString(R.string.str_vgroup_buy));
		spec = mTabHost.newTabSpec(TAB_ID_VGROUP_BUY).setIndicator(viewCount)
				.setContent(intentCount);
		mTabHost.addTab(spec);
	}

	private void addVorderFormTab() {
		Intent intentCount = new Intent(this, VorderFormActivity.class);
		Drawable countDrawable = getResources().getDrawable(
				R.drawable.tab_vorderform);
		View viewCount = inittabwidget(countDrawable,
				getResources().getString(R.string.str_vorder_form));
		spec = mTabHost.newTabSpec(TAB_ID_VORDER_FORM).setIndicator(viewCount)
				.setContent(intentCount);
		mTabHost.addTab(spec);
	}

	TextView mTextView;

	private View inittabwidget(Drawable drawable, String message) {
		LayoutInflater mInflater = LayoutInflater.from(this);
		View view = mInflater.inflate(R.layout.tab_widget, null);
		ImageView mImageView = (ImageView) view.findViewById(R.id.tab_image);
		mImageView.setImageDrawable(drawable);
		TextView mTextView = (TextView) view.findViewById(R.id.tab_text);
		mTextView.setText(message);
		return view;
	}

	private void initTabText() {
		textv_vOrderForm = new TextView(this);
		textv_vOrderForm.setText("微订单");
		textv_vOrderForm.setTextColor(0xff707070);
		textv_vOrderForm.setGravity(Gravity.CENTER);
		textv_vGroupBuy = new TextView(this);
		textv_vGroupBuy.setText("微团购");
		textv_vGroupBuy.setTextColor(0xff707070);
		textv_vGroupBuy.setGravity(Gravity.CENTER);
		textv_vChat = new TextView(this);
		textv_vChat.setText("微聊");
		textv_vChat.setTextColor(0xff707070);
		textv_vChat.setGravity(Gravity.CENTER);
		textv_vMembers = new TextView(this);
		textv_vMembers.setText("微会员");
		textv_vMembers.setTextColor(0xff707070);
		textv_vMembers.setGravity(Gravity.CENTER);

	}

	public void setTitleContent(String title) {
		textv_mainTitle.setText(title);
	}

	public void setTitleContent(int strId) {
		textv_mainTitle.setText(strId);
	}

	private void initTitleBar() {
		textv_mainTitle = (TextView) findViewById(R.id.textv_mainTitle);
		btnRight = (Button) findViewById(R.id.btnRight);
		btnRight.setOnClickListener(this);
	}

	private void initSlideMenu() {
		// 设置滑动菜单的属性值
		slideMenu = new SlidingMenu(this);
		slideMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slideMenu.setMode(SlidingMenu.LEFT);
		slideMenu.setFadeDegree(0.6f);
		slideMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		// 设置滑动菜单的视图界面
		slideMenu.setMenu(R.layout.view_slidemenu);
		slideMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		int width = getWindowManager().getDefaultDisplay().getWidth();
		slideMenu.setBehindWidth(width * 2 / 3);
		textv_welUserName = (TextView) findViewById(R.id.textv_welUserName);
		String strUserName = LoginBean.getInstance().getStrNickname();
		if (TextUtils.isEmpty(strUserName)) {
			textv_welUserName.setText("游客");
		} else {
			textv_welUserName.setText(strUserName);
		}
		View changeChatBackground = findViewById(R.id.changeChatBackground);
		changeChatBackground.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTakePhoto.start();
			}
		});
		View myself = findViewById(R.id.myself);
		myself.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainUIActivity.this,
						MyselfDetailsActivity.class));
			}
		});
		View feedBack = findViewById(R.id.feedBack);
		feedBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainUIActivity.this,
						FeedBackActivity.class);
				startActivity(intent);
			}
		});
		View view = findViewById(R.id.two_code);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainUIActivity.this,
						CaptureActivity.class);
				startActivity(intent);
			}
		});
		View clearCache = findViewById(R.id.clear_cache);
		clearCache.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainUIActivity.this,
						ClearCacheActivity.class);
				startActivity(intent);
			}
		});
		View checkVersion = findViewById(R.id.checkVersion);
		checkVersion.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CommonUtils.checkVersion(MainUIActivity.this, true, true);
			}
		});
		View about = findViewById(R.id.layout_about);
		about.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainUIActivity.this, AboutActivity.class);
				startActivity(intent);
			}
		});
		View changeHand = findViewById(R.id.change_hand);
		final ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggle_hand);
		//TODO:SP判断
		    
		toggleButton.setChecked(PreferenceUtils.getPrefBoolean(MainUIActivity.this, "isPushTurnOn", true));
		changeHand.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			if (toggleButton.isChecked()) {
							toggleButton.setChecked(false);
					PreferenceUtils.setPrefBoolean(MainUIActivity.this, "isPushTurnOn", false);
					PushManager.getInstance().turnOffPush(MainUIActivity.this);
				}else{
					toggleButton.setChecked(true);
					PreferenceUtils.setPrefBoolean(MainUIActivity.this, "isPushTurnOn", true);
					PushManager.getInstance().turnOnPush(MainUIActivity.this);
				}
			}
		});
	}

	/**
	 * 主界面设置按钮点击事件
	 * 
	 * @param v
	 */
	public void btnSetting(View v) {
		if (!slideMenu.isMenuShowing()) {
			slideMenu.showMenu();
		} else {
			slideMenu.showContent();
		}
	}

	/**
	 * 在设置界面退出登录
	 * 
	 * @param v
	 */
	public void exitAppForSettings(View v) {
		Intent intent = new Intent(MainUIActivity.this,
				ExitApp4SetActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		instance = null;
		super.onDestroy();
	}

	private void changedTabShow(String tabId) {
		if (TAB_ID_VORDER_FORM.equals(tabId)) {
			setTitleContent("微订单");
			btnRight.setVisibility(View.VISIBLE);
			setIntTabId(1);
		} else if (TAB_ID_VGROUP_BUY.equals(tabId)) {
			setTitleContent("微团购");
			btnRight.setVisibility(View.GONE);
			setIntTabId(2);
		} else if (TAB_ID_VCHAT.equals(tabId)) {
			setTitleContent("微聊");
			btnRight.setVisibility(View.GONE);
			setIntTabId(3);
		} else if (TAB_ID_VMEMBERS.equals(tabId)) {
			setTitleContent("微会员");
			btnRight.setVisibility(View.GONE);
			setIntTabId(4);
		}
	}

	private int intTabId;

	public void setIntTabId(int intTabId) {
		this.intTabId = intTabId;
	}

	public int getIntTabId() {
		return intTabId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#dispatchKeyEvent(android.view.KeyEvent)
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
					&& event.getRepeatCount() == 0) {
				if (slideMenu.isMenuShowing()) {
					slideMenu.showContent();
					return true;
				} else {
					Intent intent = new Intent();
					intent.setClass(MainUIActivity.this, ExitAppActivity.class);
					startActivity(intent);
				}
			}
		}
		return super.dispatchKeyEvent(event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnRight:
			String str_secretKey = LoginBean.getInstance().getStrSecretKey();
			if (VorderFormActivity.mActivity != null) {
				VorderFormActivity.mActivity.initDataFromNet(str_secretKey);
			}
			break;

		default:
			break;
		}

	}
}
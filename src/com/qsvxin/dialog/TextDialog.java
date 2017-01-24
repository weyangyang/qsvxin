package com.qsvxin.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.qsvxin.R;

public class TextDialog extends Dialog implements OnClickListener {
	private Context context;
	private DialogCallback btnLeft, btnRight;
	private TextView tvDialogContent, tvDialogTitle;
	private Button btnDialogLeft, btnDialogRight;
	private String strTitle, strContent, strLeft, strRight;

	public TextDialog(Context context) {
		super(context, R.style.myDialog);
		this.context = context;
		setCancelable(false);
	}

	public TextDialog(Context context, boolean isCancel) {
		super(context, R.style.myDialog);
		this.context = context;
		setCancelable(isCancel);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_text);
		tvDialogContent = (TextView) findViewById(R.id.tvDialogContent);
		tvDialogTitle = (TextView) findViewById(R.id.tvDialogTitle);
		btnDialogLeft = (Button) findViewById(R.id.btnDialogLeft);
		btnDialogRight = (Button) findViewById(R.id.btnDialogRight);
		if (!TextUtils.isEmpty(strTitle)) {
			tvDialogTitle.setVisibility(View.VISIBLE);
			tvDialogTitle.setText(strTitle);
		}
		if (!TextUtils.isEmpty(strContent)) {
			tvDialogContent.setText(strContent);
		}
		if (!TextUtils.isEmpty(strLeft)) {
			btnDialogLeft.setText(strLeft);
		}
		if (!TextUtils.isEmpty(strRight)) {
			btnDialogRight.setText(strRight);
		}
		if (btnLeft != null) {
			btnDialogLeft.setVisibility(View.VISIBLE);
		}
		if (btnRight != null) {
			btnDialogRight.setVisibility(View.VISIBLE);
		}
		btnDialogLeft.setOnClickListener(this);
		btnDialogRight.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnDialogLeft:
			btnLeft.Click();
			break;
		case R.id.btnDialogRight:
			btnRight.Click();
			break;
		}
		this.dismiss();
	}

	public void setTitleText(String str) {
		strTitle = str;
	}
	public void setTitleText(int strId) {
		strTitle = context.getResources().getString(strId);
	}

	public void setRightText(String str) {
		strRight = str;
	}
	public void setRightText(int strId) {
		strRight = context.getResources().getString(strId);
	}

	public void setLeftText(String str) {
		strLeft = str;
	}
	public void setLeftText(int strId) {
		strLeft = context.getResources().getString(strId);
	}

	public void setContentText(String str) {
		strContent = str;
	}
	public void setContentText(int strId) {
		strContent = context.getResources().getString(strId);
	}

	public void setLeftCall(DialogCallback btnLeft) {
		this.btnLeft = btnLeft;
	}

	public void setRightCall(DialogCallback btnRight) {
		this.btnRight = btnRight;
	}

}

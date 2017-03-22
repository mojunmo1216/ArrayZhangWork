package com.haloai.hud.upgraderclient;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.haloai.hud.hudupgraderlib.HudUpgradeConstants;
import com.haloai.hud.hudupgraderlib.HudUpgraderClient;
import com.haloai.hud.hudupgraderlib.HudUpgraderClient.HudUpgradeResultType;
import com.haloai.hud.hudupgraderlib.HudUpgraderClient.HudUpgradingStatusNotifier;
import com.haloai.hud.upgraderclient.OpenFileDialog.CallbackBundle;

public class MainActivity extends Activity {

	private static int openfileDialogId = 0;
	private Context mContext;
	private HudUpgraderClient client;
	private String path;
	private String msgKey = "HudUpgradeResultType";
	private TextView mTv_time;
	private ProgressDialog proDialog;
	private boolean showProDialog = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTv_time = (TextView) findViewById(R.id.tv_time);
		mContext = this;
		client = new HudUpgraderClient(mContext);
		setProgressDialog();
	}

	private void setProgressDialog() {
		proDialog = new ProgressDialog(MainActivity.this);
		proDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		proDialog.setMessage("正在上传应用，请稍候...");
		proDialog.setTitle("上传应用");
		proDialog.setMax(100);
		proDialog.setProgress(0);
	}

	Handler showResultHaner = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case HudUpgradeConstants.APP_DATA_CODE:
				if (showProDialog) {
					proDialog.show();
					showProDialog = false;
				}
				proDialog.setProgress(msg.arg1);
				if (msg.arg1 == proDialog.getMax()) {
					proDialog.dismiss();
					showProDialog = true;
					showResult("上传完成，正在升级....");
				}
				break;
			case HudUpgradeConstants.APP_INFO_CODE:

				String result = msg.getData().getString(msgKey);
				Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT)
						.show();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == openfileDialogId) {
			Map<String, Integer> images = new HashMap<String, Integer>();
			// 下面几句设置各文件类型的图标， 需要你先把图标添加到资源文件夹
			images.put(OpenFileDialog.sRoot, R.drawable.filedialog_root); // 根目录图标
			images.put(OpenFileDialog.sParent, R.drawable.filedialog_folder_up); // 返回上一层的图标
			images.put(OpenFileDialog.sFolder, R.drawable.filedialog_folder); // 文件夹图标
			images.put(OpenFileDialog.sAPK, R.drawable.apk); // apk文件图标
			images.put(OpenFileDialog.sEmpty, R.drawable.filedialog_root);
			Dialog dialog = OpenFileDialog.createDialog(id, this, "打开文件",
					new CallbackBundle() {
						@Override
						public void callback(Bundle bundle) {
							path = bundle.getString("path");
							mTv_time.setText(path);
						}
					}, ".apk;", images);
			return dialog;
		}
		return null;
	}

	// 发送apk
	public void sendAPK(View v) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				if (path != null && !path.equals("")) {
					client.startAPKUpgrade(path,
							new HudUpgradingStatusNotifier() {

								@Override
								public void onUpgradingProgress(int progress) {

									Message msg = showResultHaner
											.obtainMessage();
									msg.what = HudUpgradeConstants.APP_DATA_CODE;
									msg.arg1 = progress;
									try {
										Thread.sleep(100);
									} catch (InterruptedException e) {
									
										e.printStackTrace();
									}
									showResultHaner.sendMessage(msg);
								}

								@Override
								public void onUpgradeResult(
										HudUpgradeResultType type) {

									switch (type) {
									case SERVER_CONNECT_FAILED:
										showResult("连接失败,请检查WIFI状态");
										break;
									case SERVER_CONNECTION_BROKEN:
										showResult("连接断开,请重试！");
										break;
									case UPGRADING_FILE_MISSING:
										showResult("文件不存在！");
										break;
									case UPGRADING_FILE_ILLEGAL:
										showResult("非法文件！");
										break;
									case UPGRADE_COMPLETED:
										showResult("更新升级完成！");
										break;
									case USER_CANCELLED:
										showResult("取消成功！");
										break;
									default:
										break;
									}
								}
							});
				}
			}
		}).start();
	}

	@SuppressWarnings("deprecation")
	public void checkAPK(View v) {
		MainActivity.this.showDialog(openfileDialogId);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		client.stopAPKUpgrade();
	}

	public void showResult(String string) {
		Message msg = new Message();
		msg.what = HudUpgradeConstants.APP_INFO_CODE;
		Bundle bundle = new Bundle();
		bundle.putString(msgKey, string);
		msg.setData(bundle);
		showResultHaner.sendMessage(msg);
	}
}

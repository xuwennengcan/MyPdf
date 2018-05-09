package com.example.mypdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

public class MainActivity extends Activity implements OnClickListener {

	private ImageView iv_back; // 退出界面按钮
	private ImageView iv_menu; // 打开菜单按钮
	private EditText et_pdf; // 内容编辑框
	private Button btn_pdf; // 生成文件按钮
	private Button btn_lookPdf; //查看PDF文件按钮
	
	private ProgressDialog myDialog; // 保存进度框
	private PopupWindow ppw_lookpdf; //查看PDF文件弹出框

	private static final int PDF_SAVE_START = 1;// 保存PDF文件的开始意图
	private static final int PDF_SAVE_RESULT = 2;// 保存PDF文件的结果开始意图

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	private Handler handler = new Handler(new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case PDF_SAVE_START:
				if (!myDialog.isShowing())
					myDialog.show();
				break;

			case PDF_SAVE_RESULT:
				if (myDialog.isShowing())
					myDialog.dismiss();
				Toast.makeText(MainActivity.this, "保存成功", Toast.LENGTH_SHORT)
						.show();
				break;
			}
			return false;
		}
	});

	/**
	 * 初始化操作
	 */
	private void init() {
		initView();
		initProgress();
		initPop();
	}

	/**
	 * 初始化popupwindow
	 */
	@SuppressWarnings("deprecation")
	private void initPop() {
		View view = LayoutInflater.from(this).inflate(
				R.layout.dialog_pdf_model, null);
		btn_lookPdf = (Button) view.findViewById(R.id.btn_ocr_look_pdf);
		btn_lookPdf.setOnClickListener(this);
		ppw_lookpdf = new PopupWindow(view,
				LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		ppw_lookpdf.setFocusable(true);
		ppw_lookpdf.setOutsideTouchable(true);
		ppw_lookpdf.setBackgroundDrawable(new BitmapDrawable());
		ppw_lookpdf.setTouchable(true);
	}

	/**
	 * 初始化识别进度框
	 */
	private void initProgress() {
		myDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
		myDialog.setIndeterminateDrawable(getResources().getDrawable(
				R.drawable.progress_ocr));
		myDialog.setMessage("正在保存PDF文件...");
		myDialog.setCanceledOnTouchOutside(false);
		myDialog.setCancelable(false);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		iv_back = (ImageView) findViewById(R.id.iv_pdf_back);
		iv_menu = (ImageView) findViewById(R.id.iv_pdf_menu);
		et_pdf = (EditText) findViewById(R.id.et_pdf);
		btn_pdf = (Button) findViewById(R.id.btn_pdf);
		iv_back.setOnClickListener(this);
		iv_menu.setOnClickListener(this);
		btn_pdf.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_pdf: //生成pdf
			turnToPdf();
			break;
		case R.id.iv_pdf_back://退出界面
			this.finish();
			break;
		case R.id.iv_pdf_menu://打开查看pdf
			openPdfDialog(v);
			break;
		case R.id.btn_ocr_look_pdf:
			 lookPdf();
			break;
		}
	}
	
	/**
	 * 查看PDF文件
	 */
	private void lookPdf() {
		ppw_lookpdf.dismiss();
		PdfCenterDialog dialog = new PdfCenterDialog();
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		dialog.show(ft, "");
	}

	/**
	 * 打开进入pdf文件的显示框
	 */
	private void openPdfDialog(View v) {
		ppw_lookpdf.showAsDropDown(v);
	}
	
	/**
	 * 识别结果转为PDF文件
	 */
	private void turnToPdf() {
		if (et_pdf.getText().toString().equals("")
				|| et_pdf.getText().toString() == null) {
			Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
			return;
		}
		File file = new File(PdfUtils.ADDRESS);
		if (!file.exists())
			file.mkdirs();
		long time = System.currentTimeMillis();
		Date date = new Date(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		final String pdf_address = PdfUtils.ADDRESS + File.separator + "PDF_"
				+ sdf.format(date) + ".pdf";
		handler.sendEmptyMessage(PDF_SAVE_START);
		new Thread(new Runnable() {
			@Override
			public void run() {
				Document doc = new Document();// 创建一个document对象
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(pdf_address); // pdf_address为Pdf文件保存到sd卡的路径
					PdfWriter.getInstance(doc, fos);
					doc.open();
					doc.setPageCount(1);
					doc.add(new Paragraph(et_pdf.getText().toString(),
							setChineseFont())); // result为保存的字符串
												// ,setChineseFont()为pdf字体
					// 一定要记得关闭document对象
					doc.close();
					fos.flush();
					fos.close();
					handler.sendEmptyMessage(PDF_SAVE_RESULT);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (DocumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	/**
	 * 设置PDF字体(较为耗时)
	 */
	public Font setChineseFont() {
		BaseFont bf = null;
		Font fontChinese = null;
		try {
			// STSong-Light : Adobe的字体
			// UniGB-UCS2-H : pdf 字体
			bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
					BaseFont.NOT_EMBEDDED);
			fontChinese = new Font(bf, 12, Font.NORMAL);
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fontChinese;
	}

}

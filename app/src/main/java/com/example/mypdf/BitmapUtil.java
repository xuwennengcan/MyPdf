package com.example.mypdf;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BitmapUtil {

	/**
	 * 通过文件路径获取文件的创建时间
	 */
		public static String getFileCreatedTime(String filePath) {
			String string = "";
			File file = new File(filePath);
			Date date = new Date(file.lastModified());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日 HH:mm:ss");
			string = sdf.format(date);
			return string;
		}

		/**
		 *  通过文件路径获取文件的大小,并自动格式化
		 */
		public static String getVideoLength(String filePath) {
			String string = "0B";
			DecimalFormat df = new DecimalFormat("#.00");
			File file = new File(filePath);
			long length = file.length();
			if (length == 0) { // 文件大小为0,直接返回0B
				return string;
			}
			if (length < 1024) { // 文件小于1KB,单位为 B
				string = df.format((double) length) + "B";
			} else if (length < 1048576) {// 文件小于1M,单 位为 KB
				string = df.format((double) length / 1024) + "K";
			} else if (length < 1073741824) {// 文件小于1G,单位为 MB
				string = df.format((double) length / 1048576) + "M";
			} else {
				string = df.format((double) length / 1073741824) + "G";
			}
			return string;
		}
		
}

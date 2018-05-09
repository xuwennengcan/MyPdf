package com.example.mypdf;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Environment;

public class PdfUtils {
	
	public static final String ADDRESS = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"MyPdf";

	public static class Config {
		public static final boolean DEVELOPER_MODE = false;
	}

	public static String[] getString() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_UNMOUNTED)) {
			return null;
		}
		List<File> fileList = new ArrayList<File>();
		String[] string = null;
		String path = ADDRESS;
		File file = new File(path);
		if (file.exists() && file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].getAbsolutePath().endsWith(".pdf")) {
					fileList.add(files[i]);
				}
			}
			Collections.sort(fileList, new FileComparator());
			string = new String[fileList.size()];
			for (int i = 0; i < string.length; i++) {
				string[i] = fileList.get(i).getAbsolutePath().toString();
			}
		}
		return string;
	}

	/**
	 * 将文件按时间升序排列
	 */
	static class FileComparator implements Comparator<File> {

		@Override
		public int compare(File lhs, File rhs) {
			if (lhs.lastModified() < rhs.lastModified()) {
				return 1;// 最后修改的照片在前
			} else {
				return -1;
			}
		}
	}
}

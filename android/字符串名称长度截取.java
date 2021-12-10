/**
	 * * 名称长度截取 * * @param fileName * @param textView * @param size (像素) * @return
	 */
	public static CharSequence getEllipsizeEnd(String fileName, TextView textView, int size) {
		if (fileName == null || textView == null) {
			return null;
		}
		return TextUtils.ellipsize(fileName, textView.getPaint(), size, TextUtils.TruncateAt.END);
	}

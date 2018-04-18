/**
	 * 图片变圆角
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) { 

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888); 
		Canvas canvas = new Canvas(output); 

		final int color = 0xff424242; 
		final Paint paint = new Paint(); 
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()); 
		final RectF rectF = new RectF(rect); 
		final float roundPx = pixels; 

		paint.setAntiAlias(true); 
		canvas.drawARGB(0, 0, 0, 0); 
		paint.setColor(color); 
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint); 

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); 
		canvas.drawBitmap(bitmap, rect, rect, paint); 
		return output; 
	}
public static int saveBitmap(String path,Bitmap bitmap){
		int result=-1;
		try {
			FileOutputStream fos=new FileOutputStream(new File(path));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
			result=1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
		
	}

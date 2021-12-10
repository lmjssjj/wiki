//给Canvas加上抗锯齿标志。
//有些地方不能用paint的，就直接给canvas加抗锯齿，更方便。
canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));  

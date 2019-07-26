try {
	UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");// 使用windows外观
} catch (Exception e) {
	e.printStackTrace();
}
this.setIconImage(Toolkit.getDefaultToolkit().getImage("src/images/nuu_icon.png"));//设置窗口icon


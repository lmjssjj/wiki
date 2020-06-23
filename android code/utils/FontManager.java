package com.nuumobile.fontsettings;

import android.util.Log;
import android.view.View;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class FontManager {

    public static String TAG = "FontManager";
    private String system_fonts = "/system/etc/system_fonts.xml";
    private String fallback_fonts = "/system/etc/fallback_fonts.xml";
    private String systemFontsDir = "/system/fonts/";//系统存放字体文件的路径

    public void ywsflsjtForSystem(View view) {
        String fontName = "ywsflsjt.ttf";
        runRootCommand("mount -o remount rw /system"); //重新挂载该路径为可读写模式
        //以下操作建议在线程中执行
        if (!checkFontTTFFile(fontName)) {
            copyFontTTF(fontName, systemFontsDir);
        }
        runRootCommand("chmod 777 " + new File(systemFontsDir, fontName).getAbsolutePath());//修改字体文件的权限
        String compareNodeValue = fontName.split("\\.")[0];
        boolean node = checkXmlNode("name", compareNodeValue, new File(system_fonts));
        if (node == false) { //没有该节点，在头部插入该节点
            addSystemFontNote(compareNodeValue);
            runRootCommand("chmod 777 " + system_fonts);
        }
        runRootCommand("reboot"); //要使修改后的字体生效需要重启系统
    }


    /**
     * 检查字体文件是否存在系统目录中
     *
     * @param fontName 字体文件名称
     * @return ture：已存在
     */
    private boolean checkFontTTFFile(String fontName) {
        File dir = new File(systemFontsDir);
        File[] files = dir.listFiles();   //字库列表
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.exists() && file.getName().equals(fontName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 拷贝字体文件
     *
     * @param fontName 字体文件名
     * @param destDir  目标目录
     */
    private void copyFontTTF(String fontName, String destDir) {
        try {
            //将Assets文件夹中的字体文件读出来
            InputStream inputStream = FontsApplication.getInstance().getAssets().open("fonts/" + fontName);
            //将字体文件写入到sd卡中 ,不能直接写入 /system/fonts/下，因为没有写文件的权限
            File file = new File(FontsApplication.getInstance().getCacheDir(), fontName);
            FileOutputStream fos = new FileOutputStream(file);
            int leng = 0;
            byte[] buffer = new byte[1024];
            while (-1 != (leng = inputStream.read(buffer))) {
                fos.write(buffer, 0, leng);
            }
            fos.flush();
            fos.close();
            inputStream.close();
            runRootCommand("cp " + file.getAbsolutePath() + " " + destDir); //使用命令进行文件拷贝
            Log.e(TAG, "copyFontTTF: 字体文件拷贝成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查指定的XML文件中是否有节点名称为  refNodeName 的值为  compareNodeValue 的节点
     *
     * @param refNodeName      参考的节点名称
     * @param compareNodeValue 被比较的节点名称下的值
     * @param file             XML文件
     * @return ture存在该节点
     */
    private boolean checkXmlNode(String refNodeName, String compareNodeValue, File file) {
        boolean result = false;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);   // 解析XML到内存中
            NodeList nodeList = doc.getElementsByTagName(refNodeName);
            for (int i = 0; i < nodeList.getLength(); i++) { //判断有没有该节点
                Node item = nodeList.item(i);
                String itemValue = item.getFirstChild().getNodeValue();
                if (itemValue.equals(compareNodeValue)) {
                    result = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, "checkXmlNode: 检查是否存在该节点　refNodeName=" + refNodeName + ",compareNodeValue=" + compareNodeValue + ",result=" + result);
        return result;
    }

    /**
     * 向system_fonts.xml文件增加一个节点
     *
     * @param nodeValue 节点的值
     */
    private void addSystemFontNote(String nodeValue) {
        try {
            File file = new File(system_fonts);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);   // 解析XML到内存中
            Node nodeFamilyset = doc.getElementsByTagName("familyset").item(0);
            Element elementFamily = doc.createElement("family");
            Element elementNameset = doc.createElement("nameset");
            Element elementName = doc.createElement("name");
            elementName.setTextContent(nodeValue);
            elementNameset.appendChild(elementName);//将name节点设置为nameset的子节点

            Element elementFileset = doc.createElement("fileset");
            Element elementFile = doc.createElement("file");
            elementFile.setTextContent(nodeValue + ".ttf");
            elementFileset.appendChild(elementFile);

            elementFamily.appendChild(elementNameset);
            elementFamily.appendChild(elementFileset);
            Node nodeFamily = doc.getElementsByTagName("family").item(0);
            nodeFamilyset.insertBefore(elementFamily, nodeFamily); //将节点elementFamily插入到节点nodeFamily之前
            //　保存到文件中
            saveXmlFile(doc, system_fonts);
            Log.e(TAG, "addSystemFontNote: 节点添加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存xml文件到指定路径
     *
     * @param doc      要保存的XML文档对象
     * @param destFile 目标路径
     * @throws TransformerException
     */
    private void saveXmlFile(Document doc, String destFile) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();//创建一个用来转换DOM对象的工厂对象并获得转换器对象
        DOMSource domSource = new DOMSource(doc); //定义要转换的源对象
        File temFile = new File(FontsApplication.getInstance().getCacheDir(), "tem.xml");
        StreamResult streamResult = new StreamResult(temFile); //定义要转换到的目标文件
        transformer.transform(domSource, streamResult);  //开始转换  ,
//                runRootCommand("rm " + system_fonts);
        runRootCommand("cat " + temFile.getAbsolutePath() + " > " + destFile);//复制文件内容
        Log.e(TAG, "saveXmlFile: 保存文件成功，" + destFile);
    }

    /**
     * 请求ROOT权限后执行命令（最好开启一个线程）
     *
     * @param cmd (pm install -r *.apk)
     * @return
     */
    public boolean runRootCommand(String cmd) {
        Process process = null;
        DataOutputStream os = null;
        BufferedReader br = null;
        StringBuilder sb = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            br = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));

            sb = new StringBuilder();
            String temp = null;
            while ((temp = br.readLine()) != null) {
                sb.append(temp + "\n");
                if ("Success".equalsIgnoreCase(temp)) {
                    return true;
                }
            }
            process.waitFor();
        } catch (Exception e) {
            Log.e(TAG, "异常：" + e.getMessage());
        } finally {
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                }
                if (br != null) {
                    br.close();
                }
                process.destroy();
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}

package com.liu.flfgfront.flfgutil;

import com.liu.flfgfront.flfgBase.autoService.autoService;

import fr.opensagres.poi.xwpf.converter.core.FileImageExtractor;
import fr.opensagres.poi.xwpf.converter.core.FileURIResolver;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter;
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WordUtil extends autoService {
  private static String UPLOAD_PATH = "File/image/upload";

  public static String word2007ToHtml(InputStream in) {
    try {
      File directory = new File(""); // 参数为空
      String courseFile = directory.getCanonicalPath(); // 标准的路径 ;
      // 附件存储路径
      String imagePath = courseFile + "/src/main/resources/htmlImg/";
      File imageFile = new File(imagePath);
      String filePath = "E:/";
      String htmlName = "123123.html";
      /* 判断是否为docx文件 */
      // 1)加载word文档生成XWPFDocument对象
      XWPFDocument document = null;
      document = new XWPFDocument(in);

      // 2)解析XHTML配置（这里设置IURIResolver来设置图片存放的目录）
      XHTMLOptions options = XHTMLOptions.create().URIResolver(new FileURIResolver(imageFile));
      options.setExtractor(new FileImageExtractor(imageFile));
      options.URIResolver(
          (uri) -> {
            File imgFile = new File(imagePath + uri); // 获取图片
            String imgName = System.currentTimeMillis() + "_" + imgFile.getName(); // 设置图片文件名
            return uploadImage(imgFile, imgName); // 返回图片url
          });
      options.setIgnoreStylesIfUnused(false);
      options.setFragment(true);
      // 3)将XWPFDocument转换成XHTML
      FileOutputStream out = new FileOutputStream(new File(filePath + htmlName));
      XHTMLConverter.getInstance().convert(document, out, options);
      return filePath + htmlName;
      // 也可以使用字符数组流获取解析的内容
    } catch (IOException e) {
      e.printStackTrace();
      return "";
    }
  }
  /**
   * 转换doc
   * @throws Exception
   */
  public static String doc(InputStream in) throws Exception{
    String htmlFilePath ="File/pdfTemp/upload/"+new Date().getTime();
    HWPFDocument wordDocument = new HWPFDocument(in);
    WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
    //解析word文档
    wordToHtmlConverter.processDocument(wordDocument);
    Document htmlDocument =wordToHtmlConverter.getDocument();
    File htmlFile = new File(htmlFilePath);
    OutputStream outStream = new FileOutputStream(htmlFile);
    DOMSource domSource = new DOMSource(htmlDocument);
    StreamResult streamResult = new StreamResult(outStream);
    TransformerFactory factory = TransformerFactory.newInstance();
    Transformer serializer = factory.newTransformer();
    serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
    serializer.setOutputProperty(OutputKeys.INDENT, "yes");
    serializer.setOutputProperty(OutputKeys.METHOD, "html");
    serializer.transform(domSource, streamResult);
    outStream.close();
    return htmlFilePath;
  }

  public static String changeStyle(StringBuffer buff){
    StringBuffer buffStyle = new StringBuffer();
    //截取样式代码
    buffStyle.append(buff.substring(buff.indexOf("<style type=\"text/css\">") +23 ,buff.indexOf("style",buff.indexOf("<style type=\"text/css\">") +23 )-2));
    System.out.println(buffStyle);
    //截取body代码
    String body = buff.substring(buff.indexOf("<body"),buff.indexOf("</body")+7);
    body = body.replaceAll("body","div");
    StringBuffer bodyBuffer = new StringBuffer(body);
    System.out.println(bodyBuffer);
    String[] split = buffStyle.toString().split("}");
    Map<String,String> styleMap = new HashMap<>();
    for (String s1 : split) {
      System.out.println(s1);
      String[] split1 = s1.split("\\{");
      styleMap.put(split1[0].substring(1),split1[1]);
    }
    Set<String> strings = styleMap.keySet();
    for (String key : strings) {
      System.out.print("key : "+key);
      System.out.println("   value : "+styleMap.get(key));
      //将嵌入样式转换为行内样式
      if(bodyBuffer.toString().contains(key)){
        int length = bodyBuffer.toString().split(key).length - 1 ;
        int temp = 0 ;
        for (int i = 0 ; i < length ; i++){
          //首先判断是否完全匹配这个样式的class标识
          //由于word转换为html的时候他会自动生成class的标识  比如 p1,p2,p3,p4,p10,p11这样的话使用contains方法
          //p1就会被p11匹配到，这样样式就会乱掉，所以在添加行内样式之前必须要进行完全匹配
          temp = bodyBuffer.indexOf(key,temp);
          String isComplete = bodyBuffer.substring(temp, temp + key.length() + 1);
          //这个地方key+" "意思是代表可能一个标签里面有多个class标识 比如 class = "p2 p3 p4"
          if(!isComplete.equals(key+"\"") && !isComplete.equals(key+" ")){
            //这种就代表不是完全匹配
            continue;
          }
          //这个是每次查询到的位置，判断此标签中是否添加了style标签
          String isContaionStyle = bodyBuffer.substring(temp,bodyBuffer.indexOf(">",temp));
          if(isContaionStyle.contains("style")){
            //代表已经存在此style，那么直接加进去就好了
            //首先找到style的位置
            int styleTemp = bodyBuffer.indexOf("style",temp);
            bodyBuffer.insert(styleTemp+7,styleMap.get(key));
          }else{
            //代表没有style，那么直接插入style
            int styleIndex = bodyBuffer.indexOf("\"",temp);
            bodyBuffer.insert(styleIndex+1," style=\""+styleMap.get(key)+"\"");
          }
          temp += key.length() + 1;
        }
      }
    }
    changePicture(bodyBuffer);
    return bodyBuffer.toString();
  }

  //更换图片的路径
  public static void changePicture(StringBuffer buffer){
    //查询一个有多少个图片
    int length = buffer.toString().split("<img src=\"").length -1 ;
    int temp = 0 ;
    for (int i = 0; i < length; i++) {
      temp = buffer.indexOf("<img src=\"",temp);
      String srcContent = buffer.substring(temp + 10, buffer.indexOf("style", temp + 10));
      //获取第三方文件服务器的路径,比如如下realSrc
      String realSrc = "";
      //将路径进行替换
      buffer.replace(temp + 10 ,buffer.indexOf("style",temp + 10),realSrc+"\"");
      temp ++;
    }
  }

  public static String uploadImage(File image, String imgName) {
    try {
      String name = imgName;
      InputStream inputStream = new FileInputStream(image);
      Path directory = Paths.get(UPLOAD_PATH);
      if (!Files.exists(directory)) {
        Files.createDirectories(directory);
      }
      long copy = Files.copy(inputStream, directory.resolve(name));
      return "/File/image/upload/" + name;
    } catch (Exception e) {
      return "上传失败";
    }
  }

}

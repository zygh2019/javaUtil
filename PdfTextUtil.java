package com.liu.flfgfront.flfgutil;

import cn.hutool.core.util.StrUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class PdfTextUtil {
    private static String UPLOAD_PATH = "File/pdfTemp/upload/";
    public static HashMap<String,String>  readPdfTextTest(MultipartFile file) throws IOException {
       String pdfName = System.currentTimeMillis()+".pdf"; // 设置图片文件名  
       String url =  uploadPdf(file.getInputStream(),pdfName);
       
       String urlHtml =  ("<iframe  style='height: 96%;position: absolute; width: 96%;' src=/flfgInfo/testPdf?src='"+"/"+url+"'></iframe>");
        //进行解析
        byte[] bytes = file.getBytes();
        //加载PDF文档
        PDDocument document = PDDocument.load(bytes);
        String strText = readText(document);
        HashMap<String,String> hash = new HashMap<>();
        hash.put("urlHtml",urlHtml);
        hash.put("urlText",strText);
        return hash;
    }

    public static String readText(PDDocument document) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        StringBuffer str = new StringBuffer();
        for (String a : StrUtil.removeAllLineBreaks(text).split(" ")) {
            if ("".equals(a)) {
                continue;
            }
            str.append(a);
        }
        return str.toString();
    }


    public static String uploadPdf(InputStream inputStream , String PdfName) {
        try {
            String name = PdfName;
            Path directory = Paths.get(UPLOAD_PATH);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            long copy = Files.copy(inputStream, directory.resolve(name));
            return UPLOAD_PATH +name;
        } catch (Exception e) {
            return "上传失败";
        }
    }

}

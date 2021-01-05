package com.liu.hy.controller;

import com.alibaba.fastjson.JSONObject;
import com.aspose.words.Document;
import com.aspose.words.License;
import com.aspose.words.SaveFormat;
import com.sinosoft.constant.Configuration;

import io.minio.MinioClient;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;

public class RocketMqUtils extends HyShareProprietor{
	public static void main(String[] args) {
		try {
			saveToRoot("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过url下载文件到本地并返回该文件的物理路径
	 *
	 * @param url      外链
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static String saveToRoot(String url) throws IOException {
			url = minioIp+"/"+bucketName +"/"+url;
			if(url.contains(" ")){
			url=	url.replace(" ", "%20");
			}
			System.out.println("我是文件的路径"+url);
			String suffix = url.substring(url.lastIndexOf(".")+1);
			String root = Configuration.getProperty("HyWs_URL");
			File f = new File(root+Calendar.getInstance().getTimeInMillis()+"."+suffix);
			//如果是doc doc走这个
			if("doc".equals(suffix)||"docx".equals(suffix)){

				//解析url
				URL httpurl = new URL(url);
				FileUtils.copyURLToFile(httpurl, f);
				return f.getPath();
			}else{
				//如果是其他就走这个
				URL httpurl = new URL(url);
				FileUtils.copyURLToFile(httpurl, f);
				String wordPath =root+Calendar.getInstance().getTimeInMillis()+".docx";
				System.out.println(wordPath);
				//图片插入至word忠
				return testNumbericRender(f.getPath(),wordPath);
			}
		


	}

	public static void SendMq(JSONObject json,String topic,String tag) throws MQClientException, InterruptedException {
		System.out.println("开始传给的json串"+json.toJSONString());
		DefaultMQProducer producer = null;
		try {
			producer = new DefaultMQProducer(RqMqGrop);
			//设置一个mq的ip地址
			producer.setNamesrvAddr(RqMqIp);
			//启动mq
			producer.start();
			//创建一个mq信息
			System.out.println("传给的json串"+json.toJSONString());
			byte[] messageBody = json.toJSONString().getBytes(RemotingHelper.DEFAULT_CHARSET);
			Message msg = new Message(topic ,tag,messageBody);
			//发送出去
			SendResult sendResult = producer.send(msg);
			System.out.println("成功给的json串"+json.toJSONString());
			LOG.info(sendResult + ">>>>>>>>>>>成功向--topic"+topic+"tag"+ tag+"的消息队列--->>>>>>>>>");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("传送失败可能因为mq服务没有启动");
			Thread.sleep(1000);
		}
		producer.shutdown();
	}

	@SuppressWarnings("deprecation")
	public static String minioGetUrl(String oldPath){
		String url = "";
		if("".equals(oldPath)){
			return url;
		}
		try {
			MinioClient minioClient = new MinioClient(minioIp, minioUserName, minioPass);

			String newFileName = String.valueOf(Calendar.getInstance().getTimeInMillis()) ;
			newFileName ="OA2RQ/"+newFileName+".pdf";
			LOG.info(">>>>>>>>>>>我要准备存入minio了地址为"+newFileName+"--->>>>>>>>>");

			minioClient.putObject(bucketName,newFileName, oldPath);
			url = minioClient.getObjectUrl(bucketName, newFileName);

			url=url.substring(url.indexOf(bucketName));
			String urlTemp = url;
			url =urlTemp.substring(url.indexOf("/")+1);

			LOG.info(">>>>>>>>>>>存入minio成功并获得了新的url"+url+"--->>>>>>>>>");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return url;

	}


	@SuppressWarnings("deprecation")
	public static  void HyReivew(MessageListener listener){
		try {
			System.out.println("我开启了这个流程1");
			DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(RqMqGrop);
			consumer.setNamesrvAddr(RqMqIp);
			//mq的基本设置
			consumer.setConsumeThreadMin(1);
			consumer.setConsumeThreadMax(4);
			consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
			consumer.subscribe(topicWs, wsTags);
			consumer.getUnitName();
			consumer.registerMessageListener(listener);
			consumer.start();
		} catch (MQClientException e) {
			e.printStackTrace();
		}
	}

	public static  void addFile(String print) {
		PrintStream stream=null;
		try {
			stream=new PrintStream(CHONGPATH);//写入的文件path
			stream.print(print);//写入的字符串
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}


	public static  String readFile() throws IOException {

		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(CHONGPATH)));
		String str = null;
		String a = null ;
		while ((str = in.readLine()) != null) {
			a=str;
		}
		//关闭流
		in.close();
		return a;
	}



	public static boolean isChongFu(String valueOf) throws IOException{
		boolean isSend =true;
		String token =	RocketMqUtils.readFile();
		if(token.equals(String.valueOf(valueOf))){
			isSend =false;
		}else{
			RocketMqUtils.addFile(String.valueOf(valueOf));
		}
		return isSend;

	}



	/**
	 * 对插件进行注册
	 * @return
	 */
	public static boolean getLicense() {
		boolean result = false;
		try {
			File file = new File(listenpdf); // 新建一个空白pdf文档
			InputStream is = new FileInputStream(file); // license.xml找个路径放即可。
			License aposeLic = new License();
			aposeLic.setLicense(is);
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * doc转pdf
	 * @param inPath
	 * @param outPath
	 */
	public static void doc2pdf(String inPath, String outPath) {
		if (!getLicense()) { // 验证License 若不验证则转化出的pdf文档会有水印产生
			return;
		}
		try {
			long old = System.currentTimeMillis();
			File file = new File(outPath); // 新建一个空白pdf文档
			FileOutputStream os = new FileOutputStream(file);
			Document doc = new Document(inPath); // Address是将要被转化的word文档
			doc.save(os, SaveFormat.PDF);// 全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF,
			// EPUB, XPS, SWF 相互转换
			long now = System.currentTimeMillis();
			System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒"); // 转化用时
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




	public static String testNumbericRender(String path, String wordPath)   {
		System.out.println("我开始生成");
		try {
			XWPFDocument document=new XWPFDocument();
//		    创建一个段落对象。
			System.out.println(1);
			XWPFParagraph paragraph=document.createParagraph();
//		    创建一个run。run具体是什么，我也不知道。但是run是这里面的最小单元了。
			System.out.println(2);
			XWPFRun run=paragraph.createRun();
//		    插入图片
			System.out.println(3);
			try {
				run.addPicture(new FileInputStream(path),
						XWPFDocument.PICTURE_TYPE_JPEG,
						"1.jpg",
						Units.toEMU(400),
						Units.toEMU(600));
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		    创建一个输出流 即是该文档的保存位置
			System.out.println(path);
			OutputStream outputStream=new FileOutputStream(wordPath);
			document.write(outputStream);
			outputStream.close();
			System.out.println(123);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("我报错了");
		}
		System.out.println(wordPath);

		return wordPath;
	}


}








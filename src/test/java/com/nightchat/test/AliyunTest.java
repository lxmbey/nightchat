package com.nightchat.test;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Calendar;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;

public class AliyunTest {
	public static void main(String[] args) {
		// Endpoint以杭州为例，其它Region请按实际情况填写。
		String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
		// 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
		String accessKeyId = "";
		String accessKeySecret = "";
		// 创建OSSClient实例。
		OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
		String bucketName = "";
		String objectName = "head/174079dd1a854f739c2ae6ff5c1804cd.png";
		Calendar now = Calendar.getInstance();
		now.add(Calendar.SECOND, 10);
		OSSObject obj = ossClient.getObject(bucketName, objectName);
		InputStream in = obj.getObjectContent();
		try {
			FileOutputStream out = new FileOutputStream("E://xxx.png");
			byte[] b = new byte[1024];
			int i = 0;
			while ((i = in.read(b)) != -1) {
				out.write(b, 0, i);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		// 关闭Client。
		ossClient.shutdown();
	}
}

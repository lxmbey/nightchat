package com.nightchat.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

import javax.imageio.ImageIO;

public class PngUtil {

	private static Random random = new Random();
	// private static String randString = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";//随机产生的字符串

	private static int width = 75;//图片宽
	private static int height = 30;//图片高
	private static int lineSize = 40;//干扰线数量

	/*
	 * 获得字体
	 */

	private static Font getFont() {
		return new Font("Fixedsys", Font.CENTER_BASELINE, 18);
	}

	/*
	 * 获得颜色
	 */
	private static Color getRandColor(int fc, int bc) {
		if (fc > 255)
			fc = 255;
		if (bc > 255)
			bc = 255;
		int r = fc + random.nextInt(bc - fc - 16);
		int g = fc + random.nextInt(bc - fc - 14);
		int b = fc + random.nextInt(bc - fc - 18);
		return new Color(r, g, b);
	}

	/**
	 * 生成随机图片
	 */
	public static String getRandCode(String randomCode) {
		//BufferedImage类是具有缓冲区的Image类,Image类是用于描述图像信息的类
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		Graphics g = image.getGraphics();//产生Image对象的Graphics对象,改对象可以在图像上进行各种绘制操作
		g.fillRect(0, 0, width, height);
		g.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, 18));
		g.setColor(getRandColor(110, 133));
		//绘制干扰线
		for (int i = 0; i <= lineSize; i++) {
			drowLine(g);
		}
		//绘制随机字符
		for (int i = 0; i < randomCode.length(); i++) {
			drowString(g, randomCode.substring(i, i + 1), i + 1);
		}
		g.dispose();
		String imgStr = "";
		try {
			imgStr = getImageStr(image);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imgStr;
	}

	/*
	 * 绘制字符串
	 */
	private static void drowString(Graphics g, String str, int i) {
		g.setFont(getFont());
		g.setColor(new Color(random.nextInt(101), random.nextInt(111), random.nextInt(121)));
		g.translate(random.nextInt(3), random.nextInt(3));
		g.drawString(str, 13 * i, 16);
	}

	/*
	 * 绘制干扰线
	 */
	private static void drowLine(Graphics g) {
		int x = random.nextInt(width);
		int y = random.nextInt(height);
		int xl = random.nextInt(13);
		int yl = random.nextInt(15);
		g.drawLine(x, y, x + xl, y + yl);
	}

	private static String getImageStr(BufferedImage image) {

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "JPEG", os);
			//			FileOutputStream outputStream = new FileOutputStream("D://xxx.png");
			//			outputStream.write(os.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		String imgStr = Base64.getEncoder().encodeToString(os.toByteArray());
		return imgStr;
	}
}
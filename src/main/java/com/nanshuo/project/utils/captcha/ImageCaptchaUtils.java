package com.nanshuo.project.utils.captcha;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * 图片验证码工具类
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/01/05 19:46:00
 */
public class ImageCaptchaUtils {

    /**
     * 图片的宽度
     */
    private int width = 150;

    /**
     * 图片的高度
     */
    private int height = 40;

    /**
     * 验证码个数
     */
    private int captchaCount = 4;

    /**
     * 验证码干扰线数
     */
    private int lineCount = 20;

    /**
     * 验证码
     */
    private String captcha = null;

    /**
     * 验证码图片Buffer
     */
    private BufferedImage bufferImg = null;

    public ImageCaptchaUtils() {
        generateCaptchaImages();
    }

    public ImageCaptchaUtils(int width, int height) {
        this.width = width;
        this.height = height;
        generateCaptchaImages();
    }

    public ImageCaptchaUtils(int width, int height, int captchaCount) {
        this.width = width;
        this.height = height;
        this.captchaCount = captchaCount;
        generateCaptchaImages();
    }

    public ImageCaptchaUtils(int width, int height, int captchaCount, int lineCount) {
        this.width = width;
        this.height = height;
        this.captchaCount = captchaCount;
        this.lineCount = lineCount;
        generateCaptchaImages();
    }

    Random random = new Random();

    /**
     * 生成验证码图片
     */
    private void generateCaptchaImages() {
        int fontWidth = width / captchaCount;// 字体的宽度
        int fontHeight = height - 5;// 字体的高度
        int codeY = height - 8;

        // 图像buffer
        bufferImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferImg.getGraphics();
        //Graphics2D g = buffImg.createGraphics();
        // 设置背景色
        g.setColor(getRandomColor(200, 250));
        g.fillRect(0, 0, width, height);
        // 设置字体
        Font font = getFont(fontHeight);
        g.setFont(font);

        // 设置干扰线
        for (int i = 0; i < lineCount; i++) {
            int xs = random.nextInt(width);
            int ys = random.nextInt(height);
            int xe = xs + random.nextInt(width);
            int ye = ys + random.nextInt(height);
            g.setColor(getRandomColor(1, 255));
            g.drawLine(xs, ys, xe, ye);
        }

        // 添加噪点
        float noise = 0.01f;// 噪声率
        int area = (int) (noise * width * height);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            bufferImg.setRGB(x, y, random.nextInt(255));
        }

        String str1 = getRandomStr(captchaCount);// 得到随机字符
        this.captcha = str1;
        for (int i = 0; i < captchaCount; i++) {
            String strRand = str1.substring(i, i + 1);
            g.setColor(getRandomColor(1, 255));
            g.drawString(strRand, i * fontWidth + 3, codeY);
        }
    }

    /**
     * 获取随机字符串
     *
     * @param n n
     * @return {@code String}
     */
    private String getRandomStr(int n) {
        String str1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder str2 = new StringBuilder();
        int len = str1.length() - 1;
        double r;
        for (int i = 0; i < n; i++) {
            r = (Math.random()) * len;
            str2.append(str1.charAt((int) r));
        }
        return str2.toString();
    }

    /**
     * 获取随机颜色
     *
     * @param fc fc
     * @param bc bc
     * @return {@code Color}
     */
    private Color getRandomColor(int fc, int bc) {// 给定范围获得随机颜色
        if (fc > 255) fc = 255;
        if (bc > 255) bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    /**
     * 获取随机字体
     *
     * @param size 尺寸
     * @return {@code Font}
     */
    private Font getFont(int size) {
        Random random = new Random();
        Font[] font = new Font[5];
        font[0] = new Font("Dialog", Font.PLAIN, size);
        font[1] = new Font("Serif", Font.PLAIN, size);
        font[2] = new Font("Sans-serif", Font.PLAIN, size);
        font[3] = new Font("Monaco", Font.PLAIN, size);
        font[4] = new Font("Cursive", Font.PLAIN, size);
        return font[random.nextInt(5)];
    }

    /**
     * 生成验证码
     *
     * @param sos sos
     * @throws IOException ioexception
     */
    public void generateCaptcha(OutputStream sos) throws IOException {
        ImageIO.write(bufferImg, "png", sos);
        sos.close();
    }

    /**
     * 获取验证码
     *
     * @return {@code String}
     */
    public String getCaptcha() {
        return captcha.toLowerCase();
    }

}
package com.sdcloud.web.lar.util;

import java.awt.*;
import java.awt.print.*;

/**
 * 打印二维码
 * Created by 韩亚辉 on 2016/4/5.
 */
public class PrintUtils implements Printable {
    /**
     * @param gra       指明打印的图形环境
     * @param pf        指明打印页格式（页面大小以点为计量单位，1点为1英才的1/72，1英寸为25.4毫米。A4纸大致为595×842点）
     * @param pageIndex 指明页号
     **/


    public int print(Graphics gra, PageFormat pf, int pageIndex) throws PrinterException {
        System.out.println("pageIndex=" + pageIndex);
        Component c = null;
        //print string
        String str = "二维码";
        //转换成Graphics2D
        Graphics2D g2 = (Graphics2D) gra;
        //设置打印颜色为黑色
        g2.setColor(Color.black);
        //打印起点坐标
        double x = pf.getImageableX();
        double y = pf.getImageableY();
        switch (pageIndex) {
            case 0:
                //设置打印字体（字体名称、样式和点大小）（字体名称可以是物理或者逻辑名称）
                //Java平台所定义的五种字体系列：Serif、SansSerif、Monospaced、Dialog 和 DialogInput
                Font font = new Font("新宋体", Font.PLAIN, 9);
                g2.setFont(font);//设置字体
                //BasicStroke   bs_3=new   BasicStroke(0.5f);
                float[] dash1 = {2.0f};
                //设置打印线的属性。
                //1.线宽 2、3、不知道，4、空白的宽度，5、虚线的宽度，6、偏移量
                g2.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2.0f, dash1, 0.0f));
                //g2.setStroke(bs_3);//设置线宽
                float heigth = font.getSize2D();//字体高度
                System.out.println("x=" + x);
                Image src = Toolkit.getDefaultToolkit().getImage("E:\\1.png");
                g2.drawImage(src, (int) x, (int) y, c);
                int img_Height = src.getHeight(c);
                int img_width = src.getWidth(c);
                //System.out.println("img_Height="+img_Height+"img_width="+img_width) ;
                g2.drawString(str, (float) x, (float) y + 1 * heigth + img_Height);
                g2.drawLine((int) x, (int) (y + 1 * heigth + img_Height + 10), (int) x + 200, (int) (y + 1 * heigth + img_Height + 10));
                g2.drawImage(src, (int) x, (int) (y + 1 * heigth + img_Height + 11), c);
                return PAGE_EXISTS;

            default:
                return NO_SUCH_PAGE;
        }
    }

    public static void main(String[] args) {
        //    通俗理解就是书、文档
        Book book = new Book();
        //    设置成竖打
        PageFormat pf = new PageFormat();
        pf.setOrientation(PageFormat.PORTRAIT);
        //    通过Paper设置页面的空白边距和可打印区域。必须与实际打印纸张大小相符。
        Paper p = new Paper();
        p.setSize(590, 840);//纸张大小
        p.setImageableArea(10, 10, 590, 840);//A4(595 X 842)设置打印区域，其实0，0应该是72，72，因为A4纸的默认X,Y边距是72
        pf.setPaper(p);
        //    把 PageFormat 和 Printable 添加到书中，组成一个页面
        book.append(new PrintUtils(), pf);
        //获取打印服务对象
        PrinterJob job = PrinterJob.getPrinterJob();
        // 设置打印类
        job.setPageable(book);
        try {
            job.print();
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }
}

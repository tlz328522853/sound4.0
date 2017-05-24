package com.sdcloud.biz.lar.util;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.common.json.ParseException;

/**
 * 地图工具
 * 
 * @author victor
 *
 */
public class MapUtil {

	/**
	 * 判断当前位置是否在围栏内
	 * 
	 * @param mobilelocationEntity
	 * @param enclosureList
	 * @return
	 */
	public static boolean checkWithJdkGeneralPath(Point2D.Double point, List<Point2D.Double> polygon) {
		GeneralPath p = new GeneralPath();
		Point2D.Double first = polygon.get(0);
		p.moveTo(first.x, first.y);
		for (Point2D.Double d : polygon) {
			p.lineTo(d.x, d.y);
		}
		p.lineTo(first.x, first.y);
		p.closePath();
		return p.contains(point);
	}
	
	/**
	 * 验证点是否在区域当中
	 * @author jzc 2016年6月13日
	 * @param position
	 * @param aDouble
	 * @return
	 */
    @SuppressWarnings("unchecked")
	public static boolean validatePosition(String position,Point2D.Double aDouble){
    	List<Map<String, Double>> parse=null;
		try {
			parse = JSON.parse(position, List.class);
		} catch (Exception e) {
			return false;
		}
         List<Point2D.Double> doubleList = new ArrayList<>();
         if (parse!=null && parse.size() > 0) {
             for (Map<String, Double> map1 : parse) {
                 doubleList.add(new Point2D.Double(map1.get("lat"), map1.get("lng")));
             }
         }
        return MapUtil.checkWithJdkGeneralPath(aDouble, doubleList);
    	
    }

	/**
	 * 返回一个点是否在一个多边形区域内（测试暂时不可用）
	 * 
	 * @param point
	 * @param polygon
	 * @return
	 */
	public static boolean checkWithJdkPolygon(Point2D.Double point, List<Point2D.Double> polygon) {
		Polygon p = new Polygon();
		// java.awt.geom.GeneralPath
		final int TIMES = 10000000;
		for (Point2D.Double d : polygon) {
			int x = (int) d.x * TIMES;
			int y = (int) d.y * TIMES;
			p.addPoint(x, y);
		}
		int x = (int) point.x * TIMES;
		int y = (int) point.y * TIMES;
		return p.contains(x, y);
	}

	public static void main(String args[]) {
		
////		Point2D.Double point1 = new Point2D.Double(39.914009, 116.424673);//区域内
////		Point2D.Double point2 = new Point2D.Double(39.910377, 116.443414);//区域外
//		Point2D.Double point3 = new Point2D.Double(39.961627, 116.274625);//区域外
//		List<Point2D.Double> polygon = new ArrayList<Point2D.Double>();//东单地铁
////		polygon.add(new Point2D.Double(39.918277, 116.417506));
////		polygon.add(new Point2D.Double(39.910335, 116.418009));
////		polygon.add(new Point2D.Double(39.910778, 116.431268));
////		polygon.add(new Point2D.Double(39.918249, 116.430298));
//
//		polygon.add(new Point2D.Double(39.961627, 116.274625));
//		polygon.add(new Point2D.Double(39.961627, 116.367474));
//		polygon.add(new Point2D.Double(39.961627, 116.367474));
//		polygon.add(new Point2D.Double(39.988609, 116.367474));
//		boolean flag1 = MapUtil.checkWithJdkGeneralPath(point3, polygon);
////		boolean flag2 = MapUtil.checkWithJdkGeneralPath(point2, polygon);
//		System.out.println(flag1);
////		System.out.println(flag2);
		String position="[{'lng':119.986458,'lat':31.841852},{'lng':119.985021,'lat':31.835839},{'lng':119.97999,'lat':31.827434},{'lng':119.99271,'lat':31.825777},{'lng':119.995656,'lat':31.840195}]";
		Point2D.Double aDouble = new Point2D.Double(Double.parseDouble("31.830432380121728"), Double.parseDouble("119.99100576354863"));
		boolean flag=validatePosition(position, aDouble);
		System.out.println(flag);
	}

}

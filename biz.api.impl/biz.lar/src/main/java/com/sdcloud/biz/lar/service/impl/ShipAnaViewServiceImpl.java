package com.sdcloud.biz.lar.service.impl;

import com.sdcloud.api.lar.entity.ShipAnaView;
import com.sdcloud.api.lar.service.ShipAnaViewService;
import com.sdcloud.biz.lar.dao.ShipAnaViewDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by luorongjie on 2017/5/13.
 */
@Service
@Transactional(readOnly = true)
public class ShipAnaViewServiceImpl implements ShipAnaViewService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ShipAnaViewDao shipAnaViewDao;

    @Override
    public List<ShipAnaView> getShipAndReco(String month) throws Exception{
        logger.info("method: {},  params: {}",Thread.currentThread().getStackTrace()[1].getMethodName(),month);
        List<ShipAnaView> views = shipAnaViewDao.getShipAndReco(month);
        return convertView(views);
    }

    @Override
    public List<ShipAnaView> getBrand(String month) throws Exception{
        logger.info("method: {},  params: {}",Thread.currentThread().getStackTrace()[1].getMethodName(),month);
        List<ShipAnaView> views = shipAnaViewDao.getBrand(month);
        return convertView(views);
    }

    @Override
    public List<ShipAnaView> getSku(String month) throws Exception{
        logger.info("method: {},  params: {}",Thread.currentThread().getStackTrace()[1].getMethodName(),month);
        List<ShipAnaView> views = shipAnaViewDao.getSku(month);
        return convertView(views);
    }

    @Override
    public List<ShipAnaView> getStockfee(String month) throws Exception{
        logger.info("method: {},  params: {}",Thread.currentThread().getStackTrace()[1].getMethodName(),month);
        List<ShipAnaView> views = shipAnaViewDao.getStockfee(month);
        return convertView(views);
    }

    @Override
    public List<ShipAnaView> getStockPrice(String month) throws Exception{
        logger.info("method: {},  params: {}",Thread.currentThread().getStackTrace()[1].getMethodName(),month);
        List<ShipAnaView> views = shipAnaViewDao.getStockPrice(month);
        return convertView(views);
    }

    @Override
    public List<ShipAnaView> getDelivery(String month) throws Exception{
        logger.info("method: {},  params: {}",Thread.currentThread().getStackTrace()[1].getMethodName(),month);
        List<ShipAnaView> views = shipAnaViewDao.getDelivery(month);
        return convertView(views);
    }

    /**
     * 此转换方法，用于将SQL获得的集合，重新封装
     * @param views
     * @return
     */
    private List<ShipAnaView> convertView(List<ShipAnaView> views){
        Map<String,ShipAnaView> viewMap  = new HashMap<>();
        for(ShipAnaView view : views){
            if(null == viewMap.get(view.getName())){
                ShipAnaView v = new ShipAnaView();
                v.setNewTotals(new ArrayList<String>());
                v.setTotals(new ArrayList<String>());
                v.setProvs(new ArrayList<String>());
                v.setLessTotals(new ArrayList<String>());
                //v.getNewTotals().add(view.getNewTotal());
                if(null != view.getNewTotal() && Double.valueOf(view.getNewTotal()) >= 0){
                	v.getNewTotals().add(subPoint(view.getNewTotal()));
                	v.getLessTotals().add("0");
                }else {
                	v.getNewTotals().add("0");
                	v.getLessTotals().add(subPoint(view.getNewTotal()));
				}
                v.getTotals().add(subPoint(view.getTotal()));
                v.getProvs().add(convertProv(view.getProv()));
                v.setName(view.getName());

                viewMap.put(view.getName(),v);
            }else{
                //viewMap.get(view.getName()).getNewTotals().add(view.getNewTotal());
            	if(null != view.getNewTotal() && Double.valueOf(view.getNewTotal()) >0){
            		viewMap.get(view.getName()).getNewTotals().add(subPoint(view.getNewTotal()));
            		viewMap.get(view.getName()).getLessTotals().add("0");
                }else {
                	viewMap.get(view.getName()).getNewTotals().add("0");
                	viewMap.get(view.getName()).getLessTotals().add(subPoint(view.getNewTotal()));
				}
                viewMap.get(view.getName()).getTotals().add(subPoint(view.getTotal()));
                viewMap.get(view.getName()).getProvs().add(convertProv(view.getProv()));
            }
        }
        views = new ArrayList<>();
        Set set = viewMap.keySet();
        Iterator iter = set.iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            views.add(viewMap.get(key));
        }
        return views ;
    }

    /**
     * 将字符串 12.0000转换为 12
     * @param num
     * @return
     */
    private String subPoint(String num){
        if(num.contains("-")){
            num=num.replace("-","");
        }
        if(num.contains(".")) {
            int indexPoint = num.indexOf('.');
            num = num.substring(0, indexPoint);
        }
        return num;
    }

    /**
     * 去掉自治区，省，维吾尔族，回族
     * @param prov
     * @return
     */
    public String convertProv(String prov){
        if(prov.contains("省")){
            prov = prov.replace("省","");
        }
        if(prov.contains("自治区")){
            prov = prov.replace("自治区","");
        }
        if(prov.contains("回族")){
            prov = prov.replace("回族","");
        }
        if(prov.contains("维吾尔")){
            prov = prov.replace("维吾尔","");
        }
        if(prov.contains("壮族")){
            prov = prov.replace("壮族","");
        }
        if(prov.contains("特别行政区")){
            prov = prov.replace("特别行政区","");
        }
        return prov;
    }

    /**
     * TEST
     * @param args
     */
    public static  void main(String [] args){
        List<ShipAnaView> views = new ArrayList<>();
       /* views .add (new ShipAnaView("ship_stock","湖南","43","33"));
        views .add (new ShipAnaView("ship_stock","湖北","43","33"));
        views .add (new ShipAnaView("recy_stock","山东","43","33"));
        views .add (new ShipAnaView("recy_stock","山西","43","33"));
        views = convertView(views);*/
        System.out.print(views);

        //System.out.print(subPoint("323.1321231"));

    }


    @Override
    public List<ShipAnaView> getCollect(String month) throws Exception{
        logger.info("method: {},  params: {}",Thread.currentThread().getStackTrace()[1].getMethodName(),month);
        List<ShipAnaView> views = shipAnaViewDao.getCollect(month);
        return views;
    }

    @Override
    public List<ShipAnaView> getStockPriceCollect(String month) throws Exception{
        logger.info("method: {},  params: {}",Thread.currentThread().getStackTrace()[1].getMethodName(),month);
        List<ShipAnaView> views = shipAnaViewDao.getStockPriceCollect(month);
        return views;
    }
}

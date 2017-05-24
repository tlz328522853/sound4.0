package com.sdcloud.biz.lar.service.shipAnalysis;

import com.sdcloud.api.lar.service.shipAnalysis.AddService;
import com.sdcloud.biz.lar.dao.AddDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * Created by luorongjie on 2017/5/9.
 */
@Transactional
@Service
public class AddServiceImpl implements AddService {

    @Autowired
    private AddDao addDao;

    @Override
    public boolean addStock(Map<String,Object> stock){
        return addDao.addStock(stock) ==1;
    }

    @Override
    public boolean addBrand(Map<String, Object> brand) {
        return addDao.addBrand(brand) ==1;
    }

    @Override
    public boolean addSku(Map<String, Object> sku) {
        return addDao.addSku(sku) ==1;
    }
}

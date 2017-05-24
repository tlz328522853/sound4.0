package com.sdcloud.biz.lar.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.sdcloud.api.lar.entity.BaseEntity;
import com.sdcloud.api.lar.entity.SelectParam;
import com.sdcloud.api.lar.service.BaseService;
import com.sdcloud.biz.lar.dao.BaseDao;
import com.sdcloud.framework.common.UUIDUtil;
import com.sdcloud.framework.entity.LarPager;

/**
 * Created by 韩亚辉 on 2016/3/22.
 */
@Transactional(readOnly = true)
public abstract class BaseServiceImpl<T extends BaseEntity> implements BaseService<T> {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private BaseDao<T> baseDao;

    @Override
    public LarPager<T> findAll(LarPager<T> larPager) {
        List<T> list = baseDao.findAll(larPager);
        larPager.setResult(list);
        larPager.setTotalCount(this.totalCount(larPager));
        return larPager;
    }

    @Override
    @Transactional(readOnly = false)
    public Boolean save(T t) {
        Long uuid = UUIDUtil.getUUNum();
        Date date = new Date();
        if (t.getId() == null) {
            t.setId(uuid);
        }
        if (t.getCreateUser() == null) {
            t.setCreateUser(uuid);
        }
        if(t.getCreateDate() == null){
        	t.setCreateDate(date);
        }
        int count = baseDao.save(t);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Boolean update(T t) {
        if (t.getUpdateUser() == null) {
            t.setUpdateUser(UUIDUtil.getUUNum());
        }
        t.setUpdateDate(new Date());
        int count = baseDao.update(t);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Boolean delete(Long id) {
        int count = baseDao.delete(id);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Long totalCount(LarPager<T> var1) {
        return baseDao.totalCount(var1);
    }

    @Override
    public LarPager<T> findByOrgIds(LarPager<T> larPager, List<Long> list) {
        List<T> result = baseDao.findByOrgIds(larPager, list);
        larPager.setResult(result);
        larPager.setTotalCount(this.countByOrgIds(larPager, list));
        return larPager;
    }

    @Override
    public Long countByOrgIds(LarPager<T> larPager, List<Long> list) {
        return baseDao.countByOrgIds(larPager, list);
    }

    @Override
    public Boolean exist(Map<String, String> map, Long id) {
        List<T> count = baseDao.exist(map);
        Boolean flag = false;
        if (null != count && count.size() > 0) {
            if (count.size() > 1) {
                //说明重复
                flag = true;
            } else {
                for (T t : count) {
                    if (id == t.getId()) {
                        flag = false;
                    } else {
                        flag = true;
                    }
                }
            }
        } else {
            flag = false;
        }
        return flag;
    }

    @Override
    @Transactional(readOnly = false)
    public Boolean batchDelete(String ids) {
        List<Long> list = new ArrayList<>();
        if (ids != null && !"".equals(ids)) {
            String[] strs = ids.split(",");
            for (String str : strs) {
                list.add(Long.valueOf(str));
            }

        }
        int count = baseDao.batchDelete(list);
        System.err.println(count);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public LarPager<T> selectData(List<SelectParam> list) {
        List<T> result = baseDao.selectData(list);
        LarPager<T> larPager = new LarPager<>();
        larPager.setResult(result);
        larPager.setTotalCount(this.selectDataTotalCount(list));
        return larPager;
    }

    public Long selectDataTotalCount(List<SelectParam> list) {
        return baseDao.selectDataTotalCount(list);
    }

    @Override
    public T getById(Long id, Map<String, Object> map) {
        return baseDao.getById(id, map);
    }
}

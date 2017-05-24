package com.sdcloud.biz.lar.service.impl;

import com.sdcloud.api.lar.entity.Version;
import com.sdcloud.api.lar.service.VersionService;
import com.sdcloud.biz.lar.dao.VersionDao;
import com.sdcloud.framework.entity.LarPager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 韩亚辉 on 2016/4/21.
 */
@Service
@Transactional(readOnly = true)
public class VersionServiceImpl implements VersionService {
    @Autowired
    private VersionDao versionDao;

    @Transactional(readOnly = false)
    @Override
    public Boolean save(Version version) {
        int count = versionDao.save(version);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public LarPager<Version> findAll(LarPager<Version> larPager) {
        List<Version> list=versionDao.findAll(larPager);
        larPager.setResult(list);
        larPager.setTotalCount(versionDao.totalCount(larPager));
        return larPager;
    }

    @Override
    public Version getNewVersion(int source) {
        return versionDao.getNewVersion(source);
    }
}

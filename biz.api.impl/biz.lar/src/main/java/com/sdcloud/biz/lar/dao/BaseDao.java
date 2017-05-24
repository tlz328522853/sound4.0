package com.sdcloud.biz.lar.dao;

import com.sdcloud.api.lar.entity.BaseEntity;
import com.sdcloud.api.lar.entity.SelectParam;
import com.sdcloud.framework.entity.LarPager;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 基础的dao
 * Created by 韩亚辉 on 2016/3/22.
 */
@Repository
public interface BaseDao<T> {
    List<T> findAll(@Param("larPager") LarPager<T> larPager);

    int save(T t);

    int update(T t);

    int delete(Long id);

    Long totalCount(@Param("larPager") LarPager<T> larPager);

    List<T> findByOrgIds(@Param("larPager") LarPager<T> larPager, @Param("ids") List<Long> ids);

    Long countByOrgIds(@Param("larPager") LarPager<T> larPager, @Param("ids") List<Long> list);

    /**
     * 根据条件查询数据数量
     *
     * @param map 键值对
     * @return
     */
    List<T> exist(@Param("map") Map<String, String> map);

    int batchDelete(@Param("ids") List<Long> list);

    List<T> findByIds(@Param("ids") List<Long> ids);

    int batchSave(@Param("list") List<T> result);

    <T extends BaseEntity> List<T> selectData(@Param("list") List<SelectParam> list);

    Long selectDataTotalCount(@Param("list") List<SelectParam> list);

    T getById(@Param("id") Long id, @Param("map") Map<String, Object> map);
}

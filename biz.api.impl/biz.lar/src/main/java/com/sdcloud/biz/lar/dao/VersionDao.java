package com.sdcloud.biz.lar.dao;

import com.sdcloud.api.lar.entity.Version;
import com.sdcloud.framework.entity.LarPager;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.w3c.dom.ls.LSParser;

import java.util.List;

/**
 * Created by 韩亚辉 on 2016/4/21.
 */
@Repository
public interface VersionDao {
    @Insert("insert into lar_version (id,url,version_no,source,version,create_date,create_user) values (#{id},#{url},#{versionNo},#{source},#{version},#{createDate},#{createUser})")
    int save(Version version);

    List<Version> findAll(@Param("larPager") LarPager<Version> larPager);

    //@Select("select id as 'id',url as 'url',max(version_no) as 'versionNo',source as 'source',version as 'version' from lar_version where source=#{source}")
    @Select("select id as 'id',url as 'url',version_no as 'versionNo',source as 'source',version as 'version' from lar_version where source=#{source} ORDER BY create_date desc LIMIT 0,1")
    Version getNewVersion(@Param("source") int source);

    long totalCount(@Param("larPager") LarPager<Version> larPager);
}

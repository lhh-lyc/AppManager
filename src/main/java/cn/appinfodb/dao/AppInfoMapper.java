package cn.appinfodb.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.appinfodb.pojo.AppCategory;
import cn.appinfodb.pojo.AppInfo;
import cn.appinfodb.pojo.AppInfoExample;

public interface AppInfoMapper {
    long countByExample(AppInfoExample example);

    int deleteByExample(AppInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(AppInfo record);

    int insertSelective(AppInfo record);

    List<AppInfo> selectByExample(AppInfoExample example);

    AppInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") AppInfo record, @Param("example") AppInfoExample example);

    int updateByExample(@Param("record") AppInfo record, @Param("example") AppInfoExample example);

    int updateByPrimaryKeySelective(AppInfo record);

    int updateByPrimaryKey(AppInfo record);
    
    //查询app_info，获取所有APP信息，保存到list中
    public List<AppInfo> getAppInfo() throws Exception;
    //添加APP信息
    public int addApp(AppInfo appInfo) throws Exception;
    //根据parendid获取CategoryName
    public List<AppCategory> getAppCategoryByParentId(Long parentId) throws Exception;
    //通过apkname获取APP信息
    public AppInfo getAppInfoByAPKName(String apkname) throws Exception;
}
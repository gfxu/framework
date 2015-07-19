/**
 * 
 */
package com.fccfc.framework.web.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fccfc.framework.cache.core.CacheConstant;
import com.fccfc.framework.cache.core.CacheException;
import com.fccfc.framework.cache.core.CacheHelper;
import com.fccfc.framework.common.ServiceException;
import com.fccfc.framework.db.core.DaoException;
import com.fccfc.framework.web.bean.area.AreaPojo;
import com.fccfc.framework.web.dao.area.AreaDao;
import com.fccfc.framework.web.service.AreaService;

/**
 * <Description> <br>
 * 
 * @author 王伟<br>
 * @version 1.0<br>
 * @taskId <br>
 * @CreateDate 2015年1月25日 <br>
 * @since V1.0<br>
 * @see com.fccfc.framework.web.service.impl <br>
 */
@Service
public class AreaServiceImpl implements AreaService {

    /**
     * areaDao
     */
    @Resource
    private AreaDao areaDao;

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.web.service.AreaService#queryAllAreaPojo()
     */
    @Override
    public List<AreaPojo> queryAllAreaPojo() throws ServiceException {
        try {
            return areaDao.selectAreaList(null, -1, -1);
        }
        catch (DaoException e) {
            throw new ServiceException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.fccfc.framework.web.service.AreaService#queryAreaAndParents(java.lang.Integer)
     */
    @Override
    public List<AreaPojo> queryAreaAndParents(Integer areaId) throws ServiceException {
        List<AreaPojo> areaList = new ArrayList<AreaPojo>();
        try {
            AreaPojo area = (AreaPojo) CacheHelper.getCache().getValue(CacheConstant.AREA, areaId.toString());
            if (area != null) {
                areaList.add(area);
                setParentArea(areaList, area);
            }
        }
        catch (CacheException e) {
            throw new ServiceException(e);
        }
        return areaList;
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param areaList <br>
     * @param area <br>
     */
    private void setParentArea(List<AreaPojo> areaList, AreaPojo area) {
        AreaPojo parent = area.getParent();
        if (parent != null) {
            areaList.add(parent);
            setParentArea(areaList, parent);
        }
    }

    /**
     * 
     * Description: <br> 
     *  
     * @author yang.zhipeng <br>
     * @taskId <br>
     * @param areaId <br>
     * @return <br>
     * @throws ServiceException <br>
     */
    @Override
    public List<Integer> queryAreaAndParentIds(Integer areaId) throws ServiceException {
        List<AreaPojo> areaList = queryAreaAndParents(areaId);
        List<Integer> areaIds = new ArrayList<Integer>(areaList.size());
        for (AreaPojo area : areaList) {
            areaIds.add(area.getAreaId());
        }
        return areaIds;
    }

}

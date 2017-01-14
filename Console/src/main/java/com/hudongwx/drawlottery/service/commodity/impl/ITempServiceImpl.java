package com.hudongwx.drawlottery.service.commodity.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hudongwx.drawlottery.common.constants.CommonConstants;
import com.hudongwx.drawlottery.common.constants.LangConstants;
import com.hudongwx.drawlottery.common.exceptions.ServiceException;
import com.hudongwx.drawlottery.common.utils.TimerUtil;
import com.hudongwx.drawlottery.dao.CommodityMapper;
import com.hudongwx.drawlottery.dao.CommodityTemplateMapper;
import com.hudongwx.drawlottery.pojo.Commodity;
import com.hudongwx.drawlottery.pojo.CommodityTemplate;
import com.hudongwx.drawlottery.service.commodity.CommodityService;
import com.hudongwx.drawlottery.service.commodity.ITempService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 商品模板相关 service 实现类.
 * Date: 2017/1/6 0006
 * Time: 10:42
 *
 * @author <a href="http://userwu.github.io">wuhongxu</a>.
 * @version 1.0.0
 */
@Service
public class ITempServiceImpl implements ITempService {

    @Resource
    private CommodityMapper commodityMapper;

    @Resource
    private CommodityTemplateMapper tempMapper;

    @Resource
    private LangConstants langConstants;

    @Resource
    private CommodityService commodityService;

    @Resource
    private CommonConstants commonConstants;


    /**
     * 根据商品 id 得到商品.
     *
     * @param id 商品id
     * @return 商品
     */
    @Override
    public CommodityTemplate getCommodityById(long id) {
        return tempMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据id集合获取模板
     *
     * @param ids id集合
     * @return
     */
    @Override
    public List<CommodityTemplate> getCommodityById(List<Integer> ids) {
        return tempMapper.selectByIds(ids);
    }

    /**
     * 添加商品.
     *
     * @param temp 商品实体类
     * @return 状态
     */
    @Override
    public int addCommodityTemplate(CommodityTemplate temp) {

        //字段校验
        if (null == temp.getName())
            throw new ServiceException(langConstants.getLang(langConstants.TITLE_IS_NOT_NULL));
        if (null == temp.getGenre())
            throw new ServiceException(langConstants.getLang(langConstants.GENRE_NOT_NULL));
        if (null == temp.getCommodityTypeId())
            throw new ServiceException(langConstants.getLang(langConstants.TYPE_NOT_NULL));

        //字段补充
        if (null == temp.getGroundTime())
            temp.setGroundTime(System.currentTimeMillis());

        temp.setValid(1);
        final int result = tempMapper.insertAuto(temp);
        //定时上架
        TimerUtil.registry(() -> {
            commodityService.groundCommodity(temp.getId(), temp.getBuyTotalNumber());
            return false;
        }, temp.getGroundTime() - System.currentTimeMillis());

        return result;
        //temp.setCoverImgUrl("http://pic93.nipic.com/file/20160318/20584984_105122996275_2.jpg");

    }

    /**
     * 修改商品信息.
     *
     * @param commodityTemplate 修改实体类
     * @return 状态
     */
    @Override
    public int updateCommodity(CommodityTemplate commodityTemplate) {
        return 0;
    }

    /**
     * 批量删除商品.
     *
     * @param commodityId 删除商品的id集合
     * @return 状态
     */
    @Override
    public int deleteCommodity(List<Integer> commodityId) {
        return commodityMapper.batchDelete(commodityId);
    }

    /**
     * 批量上架商品模板.
     *
     * @param commodityIds 商品id集合
     * @return 状态
     */
    @Override
    public int ground(List<Integer> commodityIds) {
        if (commodityMapper.checkState(commodityIds, Commodity.ON_SALE) < commodityIds.size()) {
            commodityMapper.updateState(commodityIds, Commodity.ON_SALE, null, null);
            return tempMapper.updateState(commodityIds, Commodity.ON_SALE, new Date().getTime(), null);
        }
        throw new ServiceException(langConstants.getLang(langConstants.ALREADY_GROUND));
    }

    /**
     * 批量下架商品.
     *
     * @param commodityIds 商品id集合
     * @return 状态
     */
    @Override
    public int undercarriage(List<Integer> commodityIds) {
        if (commodityMapper.checkState(commodityIds, Commodity.DID_SALE) < commodityIds.size())
            return commodityMapper.updateState(commodityIds, Commodity.DID_SALE, null, new Date().getTime());
        throw new ServiceException(langConstants.getLang(langConstants.ALREADY_UN_GROUND));
    }

    /**
     * 通过模糊搜索得到字段结果（限制了结果条数）
     *
     * @param name 商品名
     * @return 匹配集合
     */
    @Override
    public List<String> getNames(String name) {
        return commodityMapper.selectNames(name);
    }

    /**
     * 获取模板列表
     *
     * @param currentPage 当前页数
     * @param maxPageSize 最大页数
     * @param type        分类
     * @param genre       类型
     * @param order       排序字段
     * @param direction   方向
     * @return 匹配分页
     */
    @Override
    public PageInfo<CommodityTemplate> getTemplates(int currentPage, int maxPageSize, List<Integer> type, List<Integer> genre, int order, int direction) {
        PageHelper.startPage(currentPage, maxPageSize);
        List<CommodityTemplate> list = tempMapper.selectTemps(type, genre, order, direction, commonConstants.getVALID());
        return new PageInfo<>(list);
    }

    /**
     * 上架模板（根据模板新建一个商品）
     *
     * @param list 模板id
     */
    @Override
    public void groundNew(List<Integer> list) {
        final List<CommodityTemplate> temps = getCommodityById(list);
        for (CommodityTemplate temp : temps) {

        }
    }

}
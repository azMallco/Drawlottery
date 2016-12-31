package com.hudongwx.drawlottery.mobile.service.user;

import com.hudongwx.drawlottery.mobile.entitys.ShoppingCart;

import java.util.List;
import java.util.Map;

/**
 * 开发公司：hudongwx.com<br/>
 * 版权：294786949@qq.com<br/>
 * <p>
 *
 * @author Kiter
 * @version 1.0, 2016/12/31 <br/>
 * @desc <p>
 * <p>
 * 创建　kiter　2016/12/31 15:55　<br/>
 * <p>
 *          购物车service
 * <p>
 * @email 346905702@qq.com
 */
public interface IShoppingCartService {

    //添加商品到购物车
    boolean addCommodityToCart(ShoppingCart shopping);

    //删除购物车里的商品
    boolean deleteCommodity(ShoppingCart shopping);

    //修改购物车里的商品信息
    boolean updateCommodity(ShoppingCart shoppingCart);

    //查看用户购物车
    List<Map<String,Object>> selectByAccount(Long accountId);
}

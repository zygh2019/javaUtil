package com.sinosoft.website.currencyService.CurrencyServiceImp;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sinosoft.website.currencyService.CurrencyServcie;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 通用service实现层
 *
 * @param <E>
 * @param <PK>
 * @author liushengbin
 * @since 2020-06-010 16:59:44
 */
public abstract class CurrencyServcieImp<E, PK> implements CurrencyServcie<E, PK> {

	@Autowired
    protected Mapper<E> mapper;

    /**
     * 插入不为null的
     *
     * @return
     */
    @Override
    public Integer insertNotNull(E entity) {

        return mapper.insertSelective(entity);
    }

    /**
     * 覆盖插入,
     *
     * @param entity
     * @return
     */
    @Override
    public Integer insertAll(E entity) {

        return mapper.insert(entity);
    }

    /**
     * 更新不为null的,
     *
     * @param entity
     * @return
     */
    @Override
    public Integer updateNotNull(E entity) {

        return mapper.updateByPrimaryKeySelective(entity);
    }

    /**
     * 覆盖更新
     *
     * @param entity
     * @return
     */
    @Override
    public Integer updateAll(E entity) {

        return mapper.updateByPrimaryKey(entity);
    }

    /**
     * 根据主键id删除
     *
     * @param id
     * @return
     */
    @Override
    public Integer deleteById(PK id) {

        return mapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据对象中的参数进行删除
     *
     * @param entity
     * @return
     */
    @Override
    public Integer delete(E entity) {

        return mapper.delete(entity);
    }

    /**
     * 有参根据实体类参数查询
     *
     * @param entity
     * @return
     */
    @Override
    public List<E> selectAll(E entity) {

        return mapper.select(entity);
    }

    /**
     * 无参查询所有
     *
     * @return
     */
    @Override
    public List<E> selectAll() {

        return mapper.selectAll();
    }

    /**
     * 根据example查询
     *
     * @param example
     * @return
     */
    @Override
    public List<E> selectByExample(Example example) {

        return mapper.selectByExample(example);
    }

    /**
     * 根据主键id查询
     *
     * @param id
     * @return
     */
    @Override
    public E selectById(PK id) {

        return mapper.selectByPrimaryKey(id);
    }

    /**
     * 根据条件查询一条数据
     *
     * @param entity
     * @return
     */
    @Override
    public E selectOne(E entity) {

        return mapper.selectOne(entity);
    }

    /**
     * 根据条件查询一条数据
     *
     * @param entity
     * @return
     */
	@Override
    public PageInfo<E> pageList(E entity, Integer page, Integer limit) {
        PageHelper.startPage(page, limit).setOrderBy("create_Time desc");
        PageInfo<E> pageInfo = new PageInfo<>(mapper.select(entity));
        return pageInfo;
    }
}

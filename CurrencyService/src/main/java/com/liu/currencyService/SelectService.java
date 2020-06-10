package com.liu.currencyService;

import java.util.List;

import tk.mybatis.mapper.entity.Example;
/**
 * 查询
 * @author liushengbin
 * @since 2020-06-010 16:59:44
 * @param <E> 
 * @param <PK> 
 */
public interface SelectService<E, PK> {
	/**
	 * 根据条件查询
	 * @param entity
	 * @return
	 */
	List<E> selectAll(E entity);
	/**
	 * 无条件查所有
	 * @param entity
	 * @return
	 */
	List<E> selectAll();
	/**
	 * 根据通用mapper的Example查询
	 * @param entity
	 * @return
	 */
	List<E> selectByExample(Example example);
	/**
	 * 根据id查询
	 * @param entity
	 * @return
	 */
	E selectById(PK id);
	/**
	 * 根据实体类的参数查询单个
	 * @param entity
	 * @return
	 */
	E selectOne(E entity);
	
}

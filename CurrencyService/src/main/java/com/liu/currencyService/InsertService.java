package com.liu.currencyService;
/**
 * 插入
 * @author liushengbin
 * @since 2020-06-010 16:59:44
 * @param <E> 实体类
 * @param <PK> 主键id
 */
public interface InsertService<E, PK> {
	/**
	 * 插入不为null的
	 * @param record
	 * @return
	 */
	Integer insertNotNull(E entity);
	/**
	 * 覆盖插入,
	 * @param entity
	 * @return
	 */
	Integer  insertAll(E entity);
}

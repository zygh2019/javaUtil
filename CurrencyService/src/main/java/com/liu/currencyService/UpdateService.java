package com.liu.currencyService;
/**
 * 更新
 * @author liushengbin
 * @since 2020-06-010 16:59:44
 * @param <E>
 */
public interface UpdateService<E> {
	/**
	 * 根据主键id 更新实体类中部位空的值
	 * @param entity
	 * @return
	 */
	Integer UpdateNotNull(E entity);
	/**
	 * 根据主键id 覆盖更新
	 * @param entity
	 * @return
	 */
	Integer UpdateAll(E entity);
}

package com.sinosoft.website.currencyService;
/**
 * 删除
 * @author liushengbin
 * @since 2020-06-010 16:59:44
 * @param <E>
 * @param <PK>
 */
public interface DeleteService<E,PK>  {
	/**
	 * 根据主键id删除
	 * @return
	 */
	Integer 	deleteById(PK id);
	/**
	 * 根据条件进行删除
	 * @param entity
	 * @return
	 */
	Integer		delete(E entity);
}

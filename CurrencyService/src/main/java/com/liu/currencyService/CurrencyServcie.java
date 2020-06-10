package com.liu.currencyService;
/**
 * 可进行扩展
 * @author zkr
 *
 * @param <E>
 * @param <PK>
 */
public interface CurrencyServcie<E, PK> extends
InsertService<E, PK>,
UpdateService<E>,
DeleteService<E,PK>,
SelectService<E, PK> {}

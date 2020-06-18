package com.sinosoft.website.currencyService;

/**
 * 可进行扩展
 * @author liushengbin
 *
 * @param <E>
 * @param <PK>
 */
public interface CurrencyServcie<E, PK> extends
        InsertService<E, PK>,
        UpdateService<E>,
        DeleteService<E,PK>,
        SelectService<E, PK> {}

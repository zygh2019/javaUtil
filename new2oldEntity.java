package com.sinosoft.website.util;

import java.lang.reflect.Field;

public class new2oldEntity {
    /** 刘胜彬
     * 将newobject的对象的值 赋值给oldobject 必须属性名字相同否则无效
     * @param newObject  新的对象
     * @param oldObject  要赋值的对象
     * @return  将赋值好的对象进行返回
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    public static Object objectClone(Object oldObject,Object newObject ) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException{
        Field[] oldFields = oldObject.getClass().getDeclaredFields();
        Field[] newFields  = newObject.getClass().getDeclaredFields();
        for (Field oldField : oldFields){
            String old1 = oldField.getName();
            oldField.setAccessible(true);
            for (Field newField : newFields) {
                newField.setAccessible(true);
                String new1= newField.getName();
                if(old1.toUpperCase().equals(new1.toUpperCase())){
                    oldField.set(oldObject, newField.get(newObject));
                }
            }
        }
        return oldObject;
    }
}

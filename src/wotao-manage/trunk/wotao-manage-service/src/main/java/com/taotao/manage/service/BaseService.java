package com.taotao.manage.service;

import java.util.Date;
import java.util.List;

import com.github.abel533.entity.Example;
import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.manage.pojo.BasePojo;

public abstract class BaseService<T extends BasePojo> {

    public abstract Mapper<T> getMapper();

    /**
     * 根据id查询数据
     * 
     * @param id
     * @return
     */
    public T queryById(Long id) {
        return this.getMapper().selectByPrimaryKey(id);
    }

    /**
     * 查询所有数据
     * 
     * @return
     */
    public List<T> queryAll() {
        return this.getMapper().select(null);
    }

    /**
     * 根据条件查询一条数据，如果有多条数据会抛出异常
     * 
     * @param record
     * @return
     */
    public T queryOne(T record) {
        return this.getMapper().selectOne(record);
    }

    /**
     * 根据条件查询数据列表
     * 
     * @param record
     * @return
     */
    public List<T> queryListByWhere(T record) {
        return this.getMapper().select(record);
    }

    /**
     * 分页条件查询
     * 
     * @param page
     * @param rows
     * @param record
     * @return
     */
    public PageInfo<T> queryPageListByWhere(Integer page, Integer rows, T record) {
        // 设置分页条件
        PageHelper.startPage(page, rows);
        List<T> list = this.queryListByWhere(record);
        return new PageInfo<T>(list);
    }

    /**
     * 新增数据，返回成功的条数
     * 
     * @param record
     * @return
     */
    public Integer save(T record) {
        record.setCreated(new Date());
        record.setUpdated(record.getCreated());
        return this.getMapper().insert(record);
    }

    /**
     * 新增数据，使用不为null的字段，返回成功的条数
     * 
     * @param record
     * @return
     */
    public Integer saveSelective(T record) {
        record.setCreated(new Date());
        record.setUpdated(record.getCreated());
        return this.getMapper().insertSelective(record);
    }

    /**
     * 修改数据，返回成功的条数
     * 
     * @param record
     * @return
     */
    public Integer update(T record) {
        record.setUpdated(new Date());
        return this.getMapper().updateByPrimaryKey(record);
    }

    /**
     * 修改数据，使用不为null的字段，返回成功的条数
     * 
     * @param record
     * @return
     */
    public Integer updateSelective(T record) {
        record.setUpdated(new Date());
        return this.getMapper().updateByPrimaryKeySelective(record);
    }

    /**
     * 根据id删除数据
     * 
     * @param id
     * @return
     */
    public Integer deleteById(Long id) {
        return this.getMapper().deleteByPrimaryKey(id);
    }

    /**
     * 批量删除
     * 
     * @param clazz
     * @param property
     * @param values
     * @return
     */
    public Integer deleteByIds(Class<T> clazz, String property, List<Object> values) {
        Example example = new Example(clazz);
        example.createCriteria().andIn(property, values);
        return this.getMapper().deleteByExample(example);
    }

    /**
     * 根据条件做删除
     * 
     * @param record
     * @return
     */
    public Integer deleteByWhere(T record) {
        return this.getMapper().delete(record);
    }

}

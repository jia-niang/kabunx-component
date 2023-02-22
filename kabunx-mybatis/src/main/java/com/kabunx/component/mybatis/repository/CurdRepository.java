package com.kabunx.component.mybatis.repository;

import com.kabunx.component.common.dto.Page;
import com.kabunx.component.common.dto.Pagination;
import com.kabunx.component.common.dto.SimplePagination;
import com.kabunx.component.mybatis.example.Example;
import com.kabunx.component.mybatis.exception.ModelException;
import com.kabunx.component.mybatis.exception.ModelNotFoundException;
import com.kabunx.component.mybatis.exception.MultipleRecordsFoundException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 通用CURD接口
 */
public interface CurdRepository<E, C extends Example> {
    /**
     * @param entity domain entity
     * @return 主键ID
     */
    Long create(E entity);

    /**
     * @param entity domain entity
     * @return 是否更新成功
     */
    Boolean update(E entity) throws ModelException;

    /**
     * @param id primary key
     * @return 是否删除成功
     */
    Boolean delete(Long id);

    /**
     * @param example 查询条件
     * @return 结果集
     */
    List<E> findAllByExample(C example);

    /**
     * 查询总数
     *
     * @param example 查询条件
     * @return 总数
     */
    long count(C example);

    /**
     * @param example 查询条件
     * @return 结果集
     */
    default List<E> findAllByExample(C example, Integer limit) {
        example.setOrderByClause("id limit " + limit);
        return findAllByExample(example);
    }

    /**
     * @param example 查询条件
     * @return 一条数据
     */
    @Nullable
    default E first(C example) {
        example.setOrderByClause("id limit 1");
        List<E> entities = findAllByExample(example);
        return CollectionUtils.isEmpty(entities) ? null : entities.get(0);
    }

    /**
     * @param example 查询条件
     * @return 第一条数据
     * @throws ModelNotFoundException 异常
     */
    @NonNull
    default E firstOrFail(C example) throws ModelNotFoundException {
        E record = first(example);
        if (Objects.isNull(record)) {
            throw new ModelNotFoundException();
        }
        return record;
    }

    /**
     * @param example  查询条件
     * @param supplier 回调函数
     * @return 一条数据
     */
    @NonNull
    default E firstOrElse(C example, Supplier<E> supplier) {
        E row = first(example);
        return Objects.nonNull(row) ? row : supplier.get();
    }

    /**
     * @param example 查询条件
     * @return 最新一条数据
     */
    @Nullable
    default E latest(C example) {
        example.setOrderByClause("id desc limit 1");
        List<E> entities = findAllByExample(example);
        return CollectionUtils.isEmpty(entities) ? null : entities.get(0);
    }

    /**
     * @param example 查询条件
     * @return 唯一一条数据
     * @throws ModelNotFoundException        未找到异常
     * @throws MultipleRecordsFoundException 多条数据异常
     */
    @NonNull
    default E sole(C example) throws ModelNotFoundException, MultipleRecordsFoundException {
        example.setOrderByClause("id limit 2");
        List<E> entities = findAllByExample(example);
        if (CollectionUtils.isEmpty(entities)) {
            throw new ModelNotFoundException();
        }
        if (entities.size() > 1) {
            throw new MultipleRecordsFoundException();
        }
        return entities.get(0);
    }

    /**
     * @param example 查询条件
     * @param page    分页信息
     * @return 分页数据
     */
    default Pagination<E> paginate(C example, Page page) {
        long total = count(example);
        if (total == 0) {
            return new Pagination<>(page.getPage(), total, null);
        }
        if (StringUtils.hasText(example.getOrderByClause())) {
            example.setOrderByClause(example.getOrderByClause() + " limit " + page.from() + ", " + page.getPageSize());
        } else {
            example.setOrderByClause("id limit " + page.from() + ", " + page.getPageSize());
        }
        List<E> entities = findAllByExample(example);
        return new Pagination<>(page.getPage(), total, entities);
    }

    /**
     * 可以用在向下滑动获取更多
     *
     * @param example 条件查询
     * @param page    分页信息
     * @return 简单分页数据
     */
    default SimplePagination<E> simplePaginate(C example, Page page) {
        if (StringUtils.hasText(example.getOrderByClause())) {
            example.setOrderByClause(example.getOrderByClause() + " limit " + page.from() + ", " + page.morePageSize());
        } else {
            example.setOrderByClause("id limit " + page.from() + ", " + page.getPageSize());
        }
        List<E> entities = findAllByExample(example);
        SimplePagination<E> simplePagination = new SimplePagination<>();
        if (entities.size() > page.getPageSize()) {
            simplePagination.setHasMore(true);
            simplePagination.setList(entities.subList(0, page.getPageSize()));
        } else {
            simplePagination.setHasMore(false);
            simplePagination.setList(entities);
        }
        return simplePagination;
    }

    default <R> List<E> listConvert(List<R> records) {
        return records.stream().map(this::convert).collect(Collectors.toList());
    }

    <R> E convert(R record);
}

package ${packageName}.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${packageName}.exception.BusinessException;
import ${packageName}.common.ErrorCode;
import ${packageName}.constant.PageConstant;
import ${packageName}.model.dto.${dataKey}.${upperDataKey}AddRequest;
import ${packageName}.utils.ThrowUtils;
import ${packageName}.mapper.${upperDataKey}Mapper;
import ${packageName}.mapper.${upperDataKey}CollectMapper;
import ${packageName}.mapper.${upperDataKey}PraiseMapper;
import ${packageName}.model.dto.${dataKey}.${upperDataKey}QueryRequest;
import ${packageName}.model.dto.${dataKey}.${upperDataKey}UpdateRequest;
import ${packageName}.model.domain.${upperDataKey};
import ${packageName}.model.domain.${upperDataKey}Collect;
import ${packageName}.model.domain.${upperDataKey}Praise;
import ${packageName}.model.domain.User;
import ${packageName}.model.vo.${dataKey}.${upperDataKey}VO;
import ${packageName}.model.dto.IdRequest;
import ${packageName}.model.vo.user.UserVO;
import ${packageName}.service.${upperDataKey}Service;
import ${packageName}.model.enums.sort.${upperDataKey}SortFieldEnums;
import ${packageName}.service.UserService;
import ${packageName}.utils.SqlUtils;
import ${packageName}.constant.UserConstant;
import org.springframework.beans.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ${dataName}服务实现
 *
 * @author ${author}
 * @Date ${date}
 */
@Service
@Slf4j
public class ${upperDataKey}ServiceImpl extends ServiceImpl<${upperDataKey}Mapper, ${upperDataKey}> implements ${upperDataKey}Service {

    @Resource
    private UserService userService;
    @Resource
    private ${upperDataKey}Mapper ${dataKey}Mapper;
    // todo 如果后续需要点赞或收藏可自行添加
    //@Resource
    //private ${upperDataKey}PraiseMapper ${dataKey}PraiseMapper;
    //@Resource
    //private ${upperDataKey}CollectMapper ${dataKey}CollectMapper;

    /**
    * 添加${dataName}
    *
    * @param ${dataKey}AddRequest ${dataName}添加请求
    * @param request               请求
    * @return long
    */
    @Override
    public long add${upperDataKey}(${upperDataKey}AddRequest ${dataKey}AddRequest, HttpServletRequest request) {
        // todo 在此处将实体类和 DTO 进行转换
        ${upperDataKey} ${dataKey} = new ${upperDataKey}();
        BeanUtils.copyProperties(${dataKey}AddRequest, ${dataKey});
        // 数据校验
        valid${upperDataKey}(${dataKey}, true);
        // todo 填充值
        User loginUser = userService.getLoginUser(request);
        ${dataKey}.setCreateBy(loginUser.getId());
        // 写入数据库
        int insert = ${dataKey}Mapper.insert(${dataKey});
        ThrowUtils.throwIf(insert <= 0, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long new${upperDataKey}Id = ${dataKey}.getId();
        // 返回新写入的数据 id
        return ${dataKey}.getId();
    }

    /**
    * 删除${dataName}
    *
    * @param deleteRequest 删除请求
    * @param request       请求
    * @return long
    */
    @Override
    public long delete${upperDataKey}(IdRequest deleteRequest, HttpServletRequest request) {
        long id = deleteRequest.getId();
        onlyMeOrAdministratorCanDo(request, id);
        ThrowUtils.throwIf(${dataKey}Mapper.deleteById(id) <= 0, ErrorCode.OPERATION_ERROR);
        return id;
    }

    /**
     * 校验数据
     *
     * @param ${dataKey}
     * @param add      对创建的数据进行校验
     */
    @Override
    public void valid${upperDataKey}(${upperDataKey} ${dataKey}, boolean add) {
        ThrowUtils.throwIf(${dataKey} == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值，自行修改为正确的属性
        //String title = ${dataKey}.getTitle();

        // 修改数据时的校验规则
        if (!add) {
            // todo 补充参数不为空校验规则
            //ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
        }
        // todo 修改和添加的公共规则
        //if (StringUtils.isNotBlank(title)) {
        //    ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
        //}
    }

    /**
     * 获取查询条件
     *
     * @param ${dataKey}QueryRequest
     * @return
     */
    @Override
    public LambdaQueryWrapper<${upperDataKey}> getQueryWrapper(${upperDataKey}QueryRequest ${dataKey}QueryRequest) {
        LambdaQueryWrapper<${upperDataKey}> queryWrapper = new LambdaQueryWrapper<>();
        if (${dataKey}QueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = ${dataKey}QueryRequest.getId();
        Long notId = ${dataKey}QueryRequest.getNotId();
        String title = ${dataKey}QueryRequest.getTitle();
        String content = ${dataKey}QueryRequest.getContent();
        String searchText = ${dataKey}QueryRequest.getSearchText();
        String sortField = ${dataKey}QueryRequest.getSortField();
        String sortOrder = ${dataKey}QueryRequest.getSortOrder();
        Long userId = ${dataKey}QueryRequest.getCreateBy();
        //List<String> tagList = ${dataKey}QueryRequest.getTags();
        // todo 补充需要的查询条件
        // 从多字段中搜索
        //if (StringUtils.isNotBlank(searchText)) {
        //    // todo 需要拼接查询条件
        //    queryWrapper.and(qw -> qw.like(${upperDataKey}::getTitle, searchText).or().like(${upperDataKey}::getContent, searchText));
        //}
        // 模糊查询
        //queryWrapper.like(StringUtils.isNotBlank(title), ${upperDataKey}::getTitle, title);
        //queryWrapper.like(StringUtils.isNotBlank(content), ${upperDataKey}::getContent, content);
        // JSON 数组查询
        //if (CollUtil.isNotEmpty(tagList)) {
        //    for (String tag : tagList) {
        //        queryWrapper.like(${upperDataKey}::getTags, "\"" + tag + "\"");
        //    }
        //}
        // 精确查询
        //queryWrapper.ne(ObjectUtils.isNotEmpty(notId), ${upperDataKey}::getId, notId);
        //queryWrapper.eq(ObjectUtils.isNotEmpty(id), ${upperDataKey}::getId, id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), ${upperDataKey}::getCreateBy, userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(PageConstant.SORT_ORDER_ASC), isSortField(sortField));
        return queryWrapper;
    }

    /**
    * 是否为排序字段
    *
    * @param sortField 排序字段
    * @return {@code SFunction<${upperDataKey}, ?>}
    */
    private SFunction<${upperDataKey}, ?> isSortField(String sortField) {
        if (Objects.equals(sortField, "")) {
            sortField = ${upperDataKey}SortFieldEnums.UPDATE_TIME.name();
        }
        if (SqlUtils.validSortField(sortField)) {
            return ${upperDataKey}SortFieldEnums.fromString(sortField).map(${upperDataKey}SortFieldEnums::getFieldGetter).orElseThrow(() -> new BusinessException(ErrorCode.PARAMS_ERROR, "排序字段非法"));
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "排序字段无效，未知错误");
        }
    }

    /**
     * 获取${dataName}封装
     *
     * @param idRequest
     * @param request
     * @return
     */
    @Override
    public ${upperDataKey}VO get${upperDataKey}VO(IdRequest idRequest, HttpServletRequest request) {
        long id = idRequest.getId();
        // 查询数据库
        ${upperDataKey} ${dataKey} = ${dataKey}Mapper.selectById(id);
        ThrowUtils.throwIf(${dataKey} == null, ErrorCode.NOT_FOUND_ERROR);
        // 对象转封装类
        ${upperDataKey}VO ${dataKey}VO = ${upperDataKey}VO.objToVo(${dataKey});

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = ${dataKey}.getCreateBy();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        ${dataKey}VO.setUser(userVO);
        return ${dataKey}VO;
    }

    /**
    * “获取列表” 页
    *
    * @param ${dataKey}QueryRequest
    * @return {@link Page }<{@link ${upperDataKey} }>
    */
    @Override
    public Page<${upperDataKey}> getListPage(${upperDataKey}QueryRequest ${dataKey}QueryRequest) {
        long current = ${dataKey}QueryRequest.getCurrent();
        long size = ${dataKey}QueryRequest.getPageSize();
        if (size == 0L) {
            size = PageConstant.PAGE_SIZE;
        }
        if (current == 0L) {
            current = PageConstant.CURRENT_PAGE;
        }
        // 查询数据库
        return this.page(new Page<>(current, size), this.getQueryWrapper(${dataKey}QueryRequest));
    }

    /**
     * 分页获取${dataName}封装
     *
     * @param ${dataKey}Page
     * @param request
     * @return
     */
    @Override
    public Page<${upperDataKey}VO> get${upperDataKey}VOPage(Page<${upperDataKey}> ${dataKey}Page, HttpServletRequest request) {
        List<${upperDataKey}> ${dataKey}List = ${dataKey}Page.getRecords();
        Page<${upperDataKey}VO> ${dataKey}VOPage = new Page<>(${dataKey}Page.getCurrent(), ${dataKey}Page.getSize(), ${dataKey}Page.getTotal());
        if (CollUtil.isEmpty(${dataKey}List)) {
            return ${dataKey}VOPage;
        }
        // 对象列表 => 封装对象列表
        List<${upperDataKey}VO> ${dataKey}VOList = ${dataKey}List.stream().map(${dataKey} -> {
            return ${upperDataKey}VO.objToVo(${dataKey});
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = ${dataKey}List.stream().map(${upperDataKey}::getCreateBy).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
        //Map<Long, Boolean> ${dataKey}IdHasPraiseMap = new HashMap<>();
        //Map<Long, Boolean> ${dataKey}IdHasCollectMap = new HashMap<>();
        //User loginUser = userService.getLoginUserPermitNull(request);
        //if (loginUser != null) {
        //    Set<Long> ${dataKey}IdSet = ${dataKey}List.stream().map(${upperDataKey}::getId).collect(Collectors.toSet());
        //    loginUser = userService.getLoginUser(request);
        //    // 获取点赞
        //    LambdaQueryWrapper<${upperDataKey}Praise> ${dataKey}PraiseQueryWrapper = new LambdaQueryWrapper<>();
        //    ${dataKey}PraiseQueryWrapper.in(${upperDataKey}Praise::getId, ${dataKey}IdSet);
        //    ${dataKey}PraiseQueryWrapper.eq(${upperDataKey}Praise::getCreateBy, loginUser.getId());
        //    List<${upperDataKey}Praise> ${dataKey}${upperDataKey}PraiseList = ${dataKey}ThumbMapper.selectList(${dataKey}ThumbQueryWrapper);
        //    ${dataKey}${upperDataKey}ThumbList.forEach(${dataKey}${upperDataKey}Praise -> ${dataKey}IdHasPraiseMap.put(${dataKey}${upperDataKey}Praise.get${upperDataKey}Id(), true));
        //    // 获取收藏
        //    LambdaQueryWrapper<${upperDataKey}Collect> ${dataKey}CollectQueryWrapper = new LambdaQueryWrapper<>();
        //    ${dataKey}CollectQueryWrapper.in(${upperDataKey}Collect::getId, ${dataKey}IdSet);
        //    ${dataKey}CollectQueryWrapper.eq(${upperDataKey}Collect::getCreateBy, loginUser.getId());
        //    List<${upperDataKey}Collect> ${dataKey}CollectList = ${dataKey}CollectMapper.selectList(${dataKey}CollectQueryWrapper);
        //    ${dataKey}CollectList.forEach(${dataKey}Collect -> ${dataKey}IdHasCollectMap.put(${dataKey}Collect.get${upperDataKey}Id(), true));
        //}
        // 填充信息
        ${dataKey}VOList.forEach(${dataKey}VO -> {
            Long userId = ${dataKey}VO.getCreateBy();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            ${dataKey}VO.setUser(userService.getUserVO(user));
        });
        // endregion

        ${dataKey}VOPage.setRecords(${dataKey}VOList);
        return ${dataKey}VOPage;
    }

    /**
    * 更新${dataName}
    *
    * @param ${dataKey}UpdateRequest 更新后请求
    * @param request           请求
    * @return long
    */
    @Override
    public long update${upperDataKey}(${upperDataKey}UpdateRequest ${dataKey}UpdateRequest, HttpServletRequest request) {
        // 本人和管理员可修改
        User user = userService.getLoginUser(request);
        if (!UserConstant.ADMIN_ROLE.equals(user.getUserRole()) && !Objects.equals(user.getId(), ${dataKey}UpdateRequest.getCreateBy())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        long id = ${dataKey}UpdateRequest.getId();
        // 获取数据
        ${upperDataKey} old${upperDataKey} = ${dataKey}Mapper.selectById(id);
        ThrowUtils.throwIf(old${upperDataKey} == null, ErrorCode.NOT_FOUND_ERROR);
        // todo 设置值
        //old${upperDataKey}.setTitle(${dataKey}UpdateRequest.getTitle());
        //old${upperDataKey}.setContent(${dataKey}UpdateRequest.getContent());
        // 参数校验
        valid${upperDataKey}(old${upperDataKey}, false);
        // 更新
        ${dataKey}Mapper.updateById(old${upperDataKey});
        return id;
    }

    /**
    * 处理分页和验证
    *
    * @param ${dataKey}QueryRequest ${dataName}查询请求
    * @param request          请求
    * @return {@code Page<${upperDataKey}VO>}
    */
    @Override
    public Page<${upperDataKey}VO> handlePaginationAndValidation(${upperDataKey}QueryRequest ${dataKey}QueryRequest, HttpServletRequest request) {
        long current = ${dataKey}QueryRequest.getCurrent();
        long size = ${dataKey}QueryRequest.getPageSize();
        if (size == 0L) {
            size = PageConstant.PAGE_SIZE;
        }
        if (current == 0L) {
            current = PageConstant.CURRENT_PAGE;
        }
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<${upperDataKey}> ${dataKey}Page = this.page(new Page<>(current, size), this.getQueryWrapper(${dataKey}QueryRequest));
        return this.get${upperDataKey}VOPage(${dataKey}Page, request);
    }

    /**
    * 只有本人或管理员可以执行
    *
    * @param request 请求
    * @param id      id
    */
    @Override
    public void onlyMeOrAdministratorCanDo(HttpServletRequest request, Long id) {
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        ${upperDataKey} old${upperDataKey} = ${dataKey}Mapper.selectById(id);
        ThrowUtils.throwIf(old${upperDataKey} == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可操作（这里假设"编辑"和"删除"操作的权限是一样的）
        if (!old${upperDataKey}.getCreateBy().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "你当前暂无该权限！");
        }
    }

    /**
    * 批量删除${dataName}
    *
    * @param ids ids
    * @return {@link List }<{@link Long }>
    */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> delete${upperDataKey}Batch(List<Long> ids) {
        // 再次确认参数
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除的ID列表不能为空");
        }
        // 执行批量删除
        this.removeByIds(ids); // MyBatis-Plus 提供的批量删除方法
        // 如果有其他相关的删除操作（比如关联表数据等），可以在这里进行处理
        return ids;
    }

}

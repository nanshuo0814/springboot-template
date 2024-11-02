package ${packageName}.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${packageName}.exception.BusinessException;
import ${packageName}.common.ErrorCode;
import ${packageName}.constant.PageConstant;
import ${packageName}.utils.ThrowUtils;
import ${packageName}.mapper.${upperDataKey}Mapper;
import ${packageName}.mapper.${upperDataKey}FavourMapper;
import ${packageName}.mapper.${upperDataKey}ThumbMapper;
import ${packageName}.model.dto.${dataKey}.${upperDataKey}QueryRequest;
import ${packageName}.model.domain.${upperDataKey};
import ${packageName}.model.domain.${upperDataKey}Favour;
import ${packageName}.model.domain.${upperDataKey}Thumb;
import ${packageName}.model.domain.User;
import ${packageName}.model.vo.${dataKey}.${upperDataKey}VO;
import ${packageName}.model.vo.user.UserVO;
import ${packageName}.service.${upperDataKey}Service;
import ${packageName}.model.enums.sort.${upperDataKey}SortFieldEnums;
import ${packageName}.service.UserService;
import ${packageName}.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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
    private ${upperDataKey}ThumbMapper ${dataKey}ThumbMapper;
    @Resource
    private ${upperDataKey}FavourMapper ${dataKey}FavourMapper;

    /**
     * 校验数据
     *
     * @param ${dataKey}
     * @param add      对创建的数据进行校验
     */
    @Override
    public void valid${upperDataKey}(${upperDataKey} ${dataKey}, boolean add) {
        ThrowUtils.throwIf(${dataKey} == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        String title = ${dataKey}.getTitle();
        // 创建数据时，参数不能为空
        if (add) {
            // todo 补充校验规则
            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则
        if (StringUtils.isNotBlank(title)) {
            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
        }
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
        List<String> tagList = ${dataKey}QueryRequest.getTags();
        Long userId = ${dataKey}QueryRequest.getCreateBy();
        // todo 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like(${upperDataKey}::getTitle, searchText).or().like(${upperDataKey}::getContent, searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), ${upperDataKey}::getTitle, title);
        queryWrapper.like(StringUtils.isNotBlank(content), ${upperDataKey}::getContent, content);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like(${upperDataKey}::getTags, "\"" + tag + "\"");
            }
        }
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), ${upperDataKey}::getId, notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), ${upperDataKey}::getId, id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), ${upperDataKey}::getCreateBy, userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(PageConstant.SORT_ORDER_ASC), isSortField(sortField));
        return queryWrapper;
    }

    /**
    * 是否为排序字段
    *
    * @param sortField 排序字段
    * @return {@code SFunction<Post, ?>}
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
     * @param ${dataKey}
     * @param request
     * @return
     */
    @Override
    public ${upperDataKey}VO get${upperDataKey}VO(${upperDataKey} ${dataKey}, HttpServletRequest request) {
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
        Map<Long, Boolean> ${dataKey}IdHasThumbMap = new HashMap<>();
        Map<Long, Boolean> ${dataKey}IdHasFavourMap = new HashMap<>();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            Set<Long> ${dataKey}IdSet = ${dataKey}List.stream().map(${upperDataKey}::getId).collect(Collectors.toSet());
            loginUser = userService.getLoginUser(request);
            // 获取点赞
            LambdaQueryWrapper<${upperDataKey}Thumb> ${dataKey}ThumbQueryWrapper = new LambdaQueryWrapper<>();
            ${dataKey}ThumbQueryWrapper.in(${upperDataKey}Thumb::getId, ${dataKey}IdSet);
            ${dataKey}ThumbQueryWrapper.eq(${upperDataKey}Thumb::getCreateBy, loginUser.getId());
            List<${upperDataKey}Thumb> ${dataKey}${upperDataKey}ThumbList = ${dataKey}ThumbMapper.selectList(${dataKey}ThumbQueryWrapper);
            ${dataKey}${upperDataKey}ThumbList.forEach(${dataKey}${upperDataKey}Thumb -> ${dataKey}IdHasThumbMap.put(${dataKey}${upperDataKey}Thumb.get${upperDataKey}Id(), true));
            // 获取收藏
            LambdaQueryWrapper<${upperDataKey}Favour> ${dataKey}FavourQueryWrapper = new LambdaQueryWrapper<>();
            ${dataKey}FavourQueryWrapper.in(${upperDataKey}Favour::getId, ${dataKey}IdSet);
            ${dataKey}FavourQueryWrapper.eq(${upperDataKey}Favour::getCreateBy, loginUser.getId());
            List<${upperDataKey}Favour> ${dataKey}FavourList = ${dataKey}FavourMapper.selectList(${dataKey}FavourQueryWrapper);
            ${dataKey}FavourList.forEach(${dataKey}Favour -> ${dataKey}IdHasFavourMap.put(${dataKey}Favour.get${upperDataKey}Id(), true));
        }
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

}

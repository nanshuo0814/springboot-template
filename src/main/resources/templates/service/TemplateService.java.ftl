package ${packageName}.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import ${packageName}.model.dto.IdRequest;
import ${packageName}.model.dto.${dataKey}.${upperDataKey}AddRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import ${packageName}.model.dto.${dataKey}.${upperDataKey}QueryRequest;
import ${packageName}.model.dto.${dataKey}.${upperDataKey}UpdateRequest;
import ${packageName}.model.domain.${upperDataKey};
import ${packageName}.model.vo.${dataKey}.${upperDataKey}VO;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * ${dataName}服务
 *
 * @author ${author}
 * @Date ${date}
 */
public interface ${upperDataKey}Service extends IService<${upperDataKey}> {

    /**
     * 校验数据
     *
     * @param ${dataKey}
     * @param add 对创建的数据进行校验
     */
    void valid${upperDataKey}(${upperDataKey} ${dataKey}, boolean add);

    /**
     * 获取查询条件
     *
     * @param ${dataKey}QueryRequest
     * @return
     */
    LambdaQueryWrapper<${upperDataKey}> getQueryWrapper(${upperDataKey}QueryRequest ${dataKey}QueryRequest);
    
    /**
     * 获取${dataName}封装
     *
     * @param idRequest
     * @param request
     * @return
     */
    ${upperDataKey}VO get${upperDataKey}VO(IdRequest idRequest, HttpServletRequest request);

    /**
     * 分页获取${dataName}封装
     *
     * @param ${dataKey}Page
     * @param request
     * @return
     */
    Page<${upperDataKey}VO> get${upperDataKey}VOPage(Page<${upperDataKey}> ${dataKey}Page, HttpServletRequest request);

    /**
    * 更新${dataName}
    *
    * @param ${dataKey}UpdateRequest 更新后请求
    * @param request           请求
    * @return long
    */
    long update${upperDataKey}(${upperDataKey}UpdateRequest ${dataKey}UpdateRequest, HttpServletRequest request);

    /**
    * 添加${dataName}
    *
    * @param ${dataKey}AddRequest ${dataName}添加请求
    * @param request               请求
    * @return long
    */
    long add${upperDataKey}(${upperDataKey}AddRequest ${dataKey}AddRequest, HttpServletRequest request);

    /**
    * 删除${dataName}
    *
    * @param deleteRequest 删除请求
    * @param request       请求
    * @return int
    */
    long delete${upperDataKey}(IdRequest deleteRequest, HttpServletRequest request);

    /**
    * “获取列表” 页
    *
    * @param ${dataKey}QueryRequest
    * @return {@link Page }<{@link ${upperDataKey} }>
    */
    Page<${upperDataKey}> getListPage(${upperDataKey}QueryRequest ${dataKey}QueryRequest);

    /**
    * 处理分页和验证
    *
    * @param ${dataKey}QueryRequest
    * @param request                 请求
    * @return {@link Page }<{@link ${upperDataKey}VO }>
    */
    Page<${upperDataKey}VO> handlePaginationAndValidation(${upperDataKey}QueryRequest ${dataKey}QueryRequest, HttpServletRequest request);

    /**
    * 只有我或管理员可以做
    *
    * @param request 请求
    * @param id      id
    */
    void onlyMeOrAdministratorCanDo(HttpServletRequest request, Long id);

    /**
    * 批量删除${dataName}
    *
    * @param ids  ids
    * @return {@link List }<{@link Long }>
    */
    List<Long> delete${upperDataKey}Batch(List<Long> ids);
}

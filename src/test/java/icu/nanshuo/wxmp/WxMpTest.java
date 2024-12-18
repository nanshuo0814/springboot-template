package icu.nanshuo.wxmp;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.material.WxMpMaterialFileBatchGetResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 微信公众号相关测试
 *
 * @author 南烁
 * @date 2024/12/18
 */
@Slf4j
@SpringBootTest
class WxMpTest {

    @Resource
    private WxMpService wxMpService;

    /**
     * 公众号素材列表获取图片文件的 mediaId
     *
     * @see icu.nanshuo.wxmp.WxMpConstant#WX_MP_CODE_MEDIA_ID
     * @see icu.nanshuo.wxmp.WxMpConstant#WX_QR_CODE_FILE_NAME
     */
    @Test
    void getMaterialList() throws WxErrorException {
        // 具体的看：https://developers.weixin.qq.com/doc/offiaccount/Asset_Management/Get_materials_list.html
        WxMpMaterialFileBatchGetResult image = wxMpService.getMaterialService().materialFileBatchGet("image",0 , 20);
        log.info("image: {}", image);
    }

}
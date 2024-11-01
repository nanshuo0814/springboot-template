package icu.nanshuo.manager;

import com.upyun.RestManager;
import com.upyun.UpException;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * upyun经理
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/10/12
 */
@Component
public class UpyunManager {

    @Resource
    private RestManager restManager;

    /**
     * 上传文件
     *
     * @param filePath 文件路径
     * @param file     文件
     * @return {@link String }
     * @throws UpException up异常
     * @throws IOException ioexception
     */
    public Response uploadFile(String filePath, File file) throws UpException, IOException {
        return restManager.writeFile(filePath, file, null);
    }

}

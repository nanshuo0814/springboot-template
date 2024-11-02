package icu.nanshuo.generator;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 代码生成器（controller、mapper、xml、service、impl、dto、enums等）
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/11/01
 */
public class CodeGenerator {

    /**
     * 用法：修改生成参数和生成路径，注释掉不需要的生成逻辑，然后运行即可
     *
     * @param args
     * @throws TemplateException
     * @throws IOException
     */
    public static void main(String[] args) throws TemplateException, IOException {
        // todo 指定生成参数
        String packageName = "icu.nanshuo";
        String dataName = "聊天";
        String dataKey = "chat";
        String upperDataKey = "Chat";
        // todo 是否需要生成swagger注解（@ApiModel、@ApiModelProperty、@Api、@ApiOperation），默认为 false，如果需要，可以设置为 true
        boolean generateSwaggerAnnotation = true;

        // 封装生成参数
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("author", "<a href=\"https://github.com/nanshuo0814\">南烁</a>");
        dataModel.put("packageName", packageName);
        dataModel.put("dataName", dataName);
        dataModel.put("dataKey", dataKey);
        dataModel.put("upperDataKey", upperDataKey);
        dataModel.put("date", DateUtil.format(new java.util.Date(), "yyyy/MM/dd"));

        // 生成路径默认值
        String projectPath = System.getProperty("user.dir");
        // 参考路径，可以自己调整下面的 outputPath
        String inputPath = projectPath + File.separator + "src/main/resources/templates/模板名称.java.ftl";
        String outputPath = String.format("%s/src/main/java/generator/包名/%s类后缀.java", projectPath, upperDataKey);
        System.out.println("开始生成...");
        long startTime = System.currentTimeMillis();
        // 1、生成 Controller
        // 指定生成路径
        inputPath = projectPath + File.separator + "src/main/resources/templates/controller/TemplateController.java.ftl";
        outputPath = String.format("%s/src/main/java/generator/controller/%sController.java", projectPath, upperDataKey);
        // 生成
        long controllerStartTime = System.currentTimeMillis();
        doGenerate(inputPath, outputPath, dataModel, generateSwaggerAnnotation);
        long controllerEndTime = System.currentTimeMillis();
        System.out.println("生成 Controller 成功，文件路径：" + outputPath + " ---耗时：" + (controllerEndTime - controllerStartTime) + "ms");

        // 2、生成 Service 接口和实现类
        // 生成 Service 接口
        inputPath = projectPath + File.separator + "src/main/resources/templates/service/TemplateService.java.ftl";
        outputPath = String.format("%s/src/main/java/generator/service/%sService.java", projectPath, upperDataKey);
        long serviceStartTime = System.currentTimeMillis();
        doGenerate(inputPath, outputPath, dataModel, generateSwaggerAnnotation);
        long serviceEndTime = System.currentTimeMillis();
        System.out.println("生成 Service 接口成功，文件路径：" + outputPath + " ---耗时：" + (serviceEndTime - serviceStartTime) + "ms");
        // 生成 Service 实现类
        inputPath = projectPath + File.separator + "src/main/resources/templates/service/impl/TemplateServiceImpl.java.ftl";
        outputPath = String.format("%s/src/main/java/generator/service/impl/%sServiceImpl.java", projectPath, upperDataKey);
        long serviceImplStartTime = System.currentTimeMillis();
        doGenerate(inputPath, outputPath, dataModel, generateSwaggerAnnotation);
        long serviceImplEndTime = System.currentTimeMillis();
        System.out.println("生成 Service 实现类成功，文件路径：" + outputPath + " ---耗时：" + (serviceImplEndTime - serviceImplStartTime) + "ms");

        // 3、生成数据模型封装类（包括 DTO 和 VO）
        // 生成 DTO
        inputPath = projectPath + File.separator + "src/main/resources/templates/model/dto/TemplateAddRequest.java.ftl";
        outputPath = String.format("%s/src/main/java/generator/model/dto/" + dataKey + "/%sAddRequest.java", projectPath, upperDataKey);
        long dtoStartTime = System.currentTimeMillis();
        doGenerate(inputPath, outputPath, dataModel, generateSwaggerAnnotation);
        inputPath = projectPath + File.separator + "src/main/resources/templates/model/dto/TemplateQueryRequest.java.ftl";
        outputPath = String.format("%s/src/main/java/generator/model/dto/" + dataKey + "/%sQueryRequest.java", projectPath, upperDataKey);
        doGenerate(inputPath, outputPath, dataModel, generateSwaggerAnnotation);
        //inputPath = projectPath + File.separator + "src/main/resources/templates/model/dto/TemplateEditRequest.java.ftl";
        //outputPath = String.format("%s/src/main/java/generator/model/dto/" + dataKey + "/%sEditRequest.java", projectPath, upperDataKey);
        //doGenerate(inputPath, outputPath, dataModel, generateSwaggerAnnotation);
        inputPath = projectPath + File.separator + "src/main/resources/templates/model/dto/TemplateUpdateRequest.java.ftl";
        outputPath = String.format("%s/src/main/java/generator/model/dto/" + dataKey + "/%sUpdateRequest.java", projectPath, upperDataKey);
        doGenerate(inputPath, outputPath, dataModel, generateSwaggerAnnotation);
        long dtoEndTime = System.currentTimeMillis();
        System.out.println("生成 DTO 成功，文件路径：" + outputPath + " ---耗时：" + (dtoEndTime - dtoStartTime) + "ms");
        // 生成 VO
        inputPath = projectPath + File.separator + "src/main/resources/templates/model/vo/TemplateVO.java.ftl";
        outputPath = String.format("%s/src/main/java/generator/model/vo/" + dataKey + "/%sVO.java", projectPath, upperDataKey);
        long voStartTime = System.currentTimeMillis();
        doGenerate(inputPath, outputPath, dataModel, generateSwaggerAnnotation);
        long voEndTime = System.currentTimeMillis();
        System.out.println("生成 VO 成功，文件路径：" + outputPath + " ---耗时：" + (voEndTime - voStartTime) + "ms");
        // 生成排序sort enums
        inputPath = projectPath + File.separator + "src/main/resources/templates/model/enums/sort/TemplateSortFieldEnums.java.ftl";
        outputPath = String.format("%s/src/main/java/generator/model/enums/sort/%sSortFieldEnums.java", projectPath, upperDataKey);
        long sortStartTime = System.currentTimeMillis();
        doGenerate(inputPath, outputPath, dataModel, generateSwaggerAnnotation);
        long sortEndTime = System.currentTimeMillis();
        System.out.println("生成排序 sort enums 成功，文件路径：" + outputPath + " ---耗时：" + (sortEndTime - sortStartTime) + "ms");
        long endTime = System.currentTimeMillis();
        System.out.println("生成完毕，总耗时：" + (endTime - startTime) + "ms");
    }

    /**
     * 生成文件
     *
     * @param inputPath  模板文件输入路径
     * @param outputPath 输出路径
     * @param model      数据模型
     * @throws IOException
     * @throws TemplateException
     */
    public static void doGenerate(String inputPath, String outputPath, Map<String, Object> model, boolean generateSwaggerAnnotation) throws IOException, TemplateException {
        // new 出 Configuration 对象，参数为 FreeMarker 版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);

        // 指定模板文件所在的路径
        File templateDir = new File(inputPath).getParentFile();
        configuration.setDirectoryForTemplateLoading(templateDir);

        // 设置模板文件使用的字符集
        configuration.setDefaultEncoding("utf-8");

        // 创建模板对象，加载指定模板
        String templateName = new File(inputPath).getName();
        Template template = configuration.getTemplate(templateName);

        // 如果不生成Swagger注解，移除相关内容
        if (!generateSwaggerAnnotation) {
            StringWriter writer = new StringWriter();
            template.process(model, writer);
            String templateText = writer.toString();

            // 按行拆分原始文本
            String[] lines = templateText.split("\n");
            StringBuilder filteredText = new StringBuilder();

            for (String line : lines) {
                // swagger注解：@ApiModel, @ApiModelProperty, @ApiOperation, @Api
                if (!line.contains("@ApiModel") && !line.contains("@ApiModelProperty") && !line.contains("@ApiOperation") && !line.contains("@Api")) {
                    filteredText.append(line).append("\n");
                }
            }

            // 去除最后多余的换行符（如果有）
            if (filteredText.length() > 0 && filteredText.charAt(filteredText.length() - 1) == '\n') {
                filteredText.deleteCharAt(filteredText.length() - 1);
            }

            template = new Template(templateName, new StringReader(filteredText.toString()), configuration);
        }


        // 文件不存在则创建文件和父目录
        if (!FileUtil.exist(outputPath)) {
            FileUtil.touch(outputPath);
        }

        // 生成
        Writer out = new FileWriter(outputPath);
        template.process(model, out);

        // 生成文件后别忘了关闭哦
        out.close();
    }
}
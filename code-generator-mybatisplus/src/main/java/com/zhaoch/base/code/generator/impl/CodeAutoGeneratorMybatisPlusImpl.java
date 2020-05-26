package com.zhaoch.base.code.generator.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.zhaoch.base.code.generator.CodeAutoGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link CodeAutoGenerator} 实现
 *
 * @author Zhaochen
 * create time: 2019/8/4 20:32
 */
@Slf4j
@Component(value = "codeAutoGenerator")
public class CodeAutoGeneratorMybatisPlusImpl implements CodeAutoGenerator {

    @Value("${code.author}")
    private String author;
    @Value("${code.path}")
    private String path;
    @Value(value = "${code.module.parent}")
    private String moduleParent;
    @Value(value = "${code.module.name}")
    private String moduleName;
    @Value(value = "${code.swagger.enable}")
    private boolean enableSwagger2;

    @Value(value = "${code.entity.lombok.enable}")
    private boolean entityLombokEnable;
    @Value(value = "${code.entity.super}")
    private String superEntityClass;
    @Value(value = "${code.entity.super.columns}")
    private String superEntityClassColumns;
    @Value(value = "${code.entity.logicDeleteColumn}")
    private String entityLogicDeleteColumn;
    @Value(value = "${code.entity.versionColumn}")
    private String entityVersionColumn;

    @Value(value = "${code.controller.super}")
    private String superControllerClass;

    @Value(value = "${database.driver}")
    private String dbDriverName;
    @Value(value = "${database.url}")
    private String dbUrl;
    @Value(value = "${database.username}")
    private String dbUsername;
    @Value(value = "${database.password}")
    private String dbPassword;

    @Value(value = "${database.table.includes}")
    private String dbTableIncludes;
    @Value(value = "${database.table.excludes}")
    private String dbTableExcludes;

    /**
     *
     */
    private static final String SRC_JAVA_PATH = "/src/main/java";
    private static final String SRC_RESOURCE_PATH = "/src/main/resources";
    private static final String SPLIT = ",";

    @Override
    public void generate() {
        log.info("通用代码生成 开始");

        // 初始化代码自动生成器
        AutoGenerator autoGenerator = new AutoGenerator();

        // 全局配置
        this.buildGlobalConfig(autoGenerator);

        // 数据源配置
        this.buildDatasourceConfig(autoGenerator);

        // 生成模板引擎配置
        this.buildTemplateConfig(autoGenerator);

        // 生成包配置
        this.buildPackageConfig(autoGenerator);

        // 生成策略配置
        this.buildStrategyConfig(autoGenerator);

        // 自定义配置
        this.buildInjectionConfig(autoGenerator);

        // 开始生成
        autoGenerator.execute();
        log.info("通用代码生成 完成");
        log.warn("注意：当前版本 mapper.xml 输出两份, 分别在 resource 和 mapper class 目录下, 需手动删除其中一个");
    }

    private void buildInjectionConfig(AutoGenerator autoGenerator) {
        InjectionConfig injectionConfig = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 自定义模板生成文件输出配置
        // 自定义 mapper.xml 模板输出位置
        // 如果模板引擎是 freemarker
        // String mapperXmlTemplatePath = "/templates/mapper.xml.ftl";
        // 如果模板引擎是 velocity
        String mapperXmlTemplatePath = "/templates/mapper.xml.vm";
        //
        List<FileOutConfig> fileOutConfigList = new ArrayList<>();
        // 自定义配置会被优先输出, 注意 mapper.xml 会输出两份，需要手动删除
        fileOutConfigList.add(new FileOutConfig(mapperXmlTemplatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名，如果 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return path + SRC_RESOURCE_PATH + "/mapper/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });
        injectionConfig.setFileOutConfigList(fileOutConfigList);

        autoGenerator.setCfg(injectionConfig);
        log.info("\n自定义配置:\n{}", JSONUtil.formatJsonStr(JSONUtil.toJsonStr(injectionConfig)));
    }

    private void buildStrategyConfig(AutoGenerator autoGenerator) {
        StrategyConfig strategy = new StrategyConfig();
        // 数据库表名映射策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setTablePrefix(this.moduleName + "_");
        // 数据库表字段名映射策略
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        // 数据库表包含、排除配置
        if (StringUtils.isEmpty(this.dbTableIncludes)) {
            strategy.setInclude();
        } else {
            strategy.setInclude(this.dbTableIncludes.split(SPLIT));
        }
        if (!StringUtils.isEmpty(this.dbTableExcludes)) {
            strategy.setExclude(this.dbTableExcludes.split(SPLIT));
        }

        // Entity 生成是否采用 Lombok 模式
        strategy.setEntityLombokModel(this.entityLombokEnable);
        // Entity 逻辑删除字段
        if (!StringUtils.isEmpty(this.entityLogicDeleteColumn)) {
            strategy.setLogicDeleteFieldName(this.entityLogicDeleteColumn);
        }
        // Entity 乐观锁 version 字段
        if (!StringUtils.isEmpty(this.entityVersionColumn)) {
            strategy.setVersionFieldName(this.entityVersionColumn);
        }
        // Entity 指定父类
        if (!StringUtils.isEmpty(this.superEntityClass)) {
            strategy.setSuperEntityClass(this.superEntityClass);
        }
        // Entity 指定父类中已包含字段
        if (!StringUtils.isEmpty(this.superEntityClassColumns)) {
            strategy.setSuperEntityColumns(this.superEntityClassColumns.split(SPLIT));
        }

        // Controller 指定父类
        if (!StringUtils.isEmpty(this.superControllerClass)) {
            strategy.setSuperControllerClass(superControllerClass);
        }
        // Controller 生成是否采用 Rest 风格
        strategy.setRestControllerStyle(true);
        // Controller Mapping 地址生成是否使用连接符，true: xxx-data, false: xxxData
        strategy.setControllerMappingHyphenStyle(true);

        autoGenerator.setStrategy(strategy);
        log.info("\n代码生成策略配置:\n{}", JSONUtil.formatJsonStr(JSONUtil.toJsonStr(strategy)));
    }

    private void buildPackageConfig(AutoGenerator autoGenerator) {
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setModuleName(this.moduleName);
        packageConfig.setParent(this.moduleParent);
        // Entity 包名称, 同理可配置 mapper、service、controller; 可以是 abc.xyz 多层结构
        packageConfig.setEntity("bean");
        autoGenerator.setPackageInfo(packageConfig);
        log.info("\n代码生成包配置:\n{}", JSONUtil.formatJsonStr(JSONUtil.toJsonStr(packageConfig)));
    }

    private void buildTemplateConfig(AutoGenerator autoGenerator) {
        TemplateConfig templateConfig = new TemplateConfig();
        // 默认模板引擎是 VelocityTemplateEngine
        // autoGenerator.setTemplateEngine(new FreemarkerTemplateEngine());
        // autoGenerator.setTemplateEngine(new VelocityTemplateEngine());
        // 自定义 entity、mapper、service、controller 生成模板路径
        // 注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        // templateConfig.setEntity("templates/entity2.java");
        // templateConfig.setService();
        // templateConfig.setController();
        autoGenerator.setTemplate(templateConfig);
        log.info("\n代码模板配置:\n{}", JSONUtil.formatJsonStr(JSONUtil.toJsonStr(templateConfig)));
    }

    private void buildDatasourceConfig(AutoGenerator autoGenerator) {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl(this.dbUrl);
        dataSourceConfig.setDriverName(this.dbDriverName);
        dataSourceConfig.setUsername(this.dbUsername);
        dataSourceConfig.setPassword(this.dbPassword);
        autoGenerator.setDataSource(dataSourceConfig);
        log.info("\n数据源配置:\n{}", JSONUtil.formatJsonStr(JSONUtil.toJsonStr(dataSourceConfig)));
    }

    private void buildGlobalConfig(AutoGenerator autoGenerator) {
        // 全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        // 代码输出目录
        globalConfig.setOutputDir(this.path + SRC_JAVA_PATH);
        // 代码作者
        globalConfig.setAuthor(this.author);
        // 代码输出后，是否打开输出目录
        globalConfig.setOpen(false);
        // 代码注释是否开启 swagger2 模式
        globalConfig.setSwagger2(this.enableSwagger2);
        autoGenerator.setGlobalConfig(globalConfig);
        log.info("\n全局配置:\n{}", JSONUtil.formatJsonStr(JSONUtil.toJsonStr(globalConfig)));
    }

}

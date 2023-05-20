package com.jwt.freecloud.generate;


import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * 代码生成器
 */
public class InitCode {


    public static void main(String[] args) {
        // 代码生成器
        AutoGenerator autoGenerator = new AutoGenerator();

        // 全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        globalConfig.setFileOverride(true);
        globalConfig.setOutputDir(projectPath + "/src/main/java");
        globalConfig.setAuthor("jwt");
        globalConfig.setOpen(false);
        globalConfig.setServiceName("%sService");  //去掉接口上的I
        globalConfig.setBaseResultMap(true);
        autoGenerator.setGlobalConfig(globalConfig);


        // 数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
//        dataSourceConfig.setUrl("jdbc:mysql://192.168.10.132:3306/free_cloud?serverTimezone=GMT%2B8");
        dataSourceConfig.setUrl("jdbc:mysql://127.0.0.1:3306/cloud?serverTimezone=GMT%2B8");
        dataSourceConfig.setDriverName("com.mysql.cj.jdbc.Driver");
        dataSourceConfig.setUsername("root");
        dataSourceConfig.setPassword("123456");
        //加入自定义类型转换
        dataSourceConfig.setTypeConvert(new MySqlTypeConvertCustom());
        autoGenerator.setDataSource(dataSourceConfig);

        // 包配置
        PackageConfig packageConfig = new PackageConfig();
        //packageConfig.setParent("com.jwt.freecloud");
        packageConfig.setEntity("common.entity");
        //packageConfig.setController("controller");
       //packageConfig.setMapper("dao");
        //packageConfig.setService("service");
        //packageConfig.setServiceImpl("service.impl");
        autoGenerator.setPackageInfo(packageConfig);


        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true);

        strategy.setInclude("activity", "file_user", "file_origin", "file_user_origin","file_share", "file_recycle",
                                "user", "user_transfer","user_level","user_join","user_share");


        autoGenerator.setStrategy(strategy);

        autoGenerator.execute();
    }
}


/**
 * 自定义类型转换
 */
class MySqlTypeConvertCustom extends MySqlTypeConvert implements ITypeConvert {
    @Override
    public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
        String t = fieldType.toLowerCase();
        if (t.contains("tinyint(1)")) {
            return DbColumnType.INTEGER;
        }
        return super.processTypeConvert(globalConfig, fieldType);
    }
}




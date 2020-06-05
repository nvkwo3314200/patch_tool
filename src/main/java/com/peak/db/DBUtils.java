package com.peak.db;

import com.peak.db.sqlserver.SqlserverService;
import com.peak.util.DateUtils;
import com.peak.util.PathUtil;
import com.peak.util.PropertyUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBUtils {
    private static final Logger logger = Logger.getLogger(DBUtils.class);
    private static final String SUBFIX = ".sql";
    private static final String DROP_TEMPLAT = "if exists(select * from sys.all_objects where name='%s')\n" +
            "drop %s dbo.%s;\n" +
            "go";
    private static boolean changeToActualModifyDate;
    private static DBService service = null;
    static {
        service = new SqlserverService();
        changeToActualModifyDate = String.valueOf(Boolean.FALSE).equalsIgnoreCase(PropertyUtil.getValue("modify_date"));
    }

    public static void generatePatchFile() {
        QueryParam param = new QueryParam();
        Date date = DateUtils.parse(PropertyUtil.getValue("sqlServer.effDate"));
        param.setObjName(PropertyUtil.getValue("sqlServer.obj.name"));
        param.setLastUpdateTime(date);
        param.setTypeList(new ArrayList<>());
        param.getTypeList().add(ObjectEnum.Function3);
        param.getTypeList().add(ObjectEnum.Function2);
        param.getTypeList().add(ObjectEnum.Function);
        param.getTypeList().add(ObjectEnum.Trigger);
        param.getTypeList().add(ObjectEnum.Procedure);
        param.getTypeList().add(ObjectEnum.View);
        generatePatchFile(param);
    }

    private static void generatePatchFile(QueryParam param) {
        List<DBObjectBean> list = service.listDBObject(param);
        logger.info("size=" + list.size());
        String path = PathUtil.generatePatchPath("script/");
        File patchDir = new File(path);
        patchDir.mkdirs();
        logger.info("path=" + patchDir.getAbsolutePath());
        try {
            FileUtils.deleteDirectory(patchDir);
            patchDir.mkdirs();
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.forEach(item -> {
            File file = new File(path + item.getName() + SUBFIX);
            resetContentText(item);
            try {
                FileUtils.write(file, item.getContentText());
                if(item.getLastUpdateTime() != null && changeToActualModifyDate) {
                    file.setLastModified(item.getLastUpdateTime().getTime());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        if(Boolean.TRUE.toString().toLowerCase().equals(PropertyUtil.getValue("write.onefile"))) {
            File oneFile = new File(path + "all_script" + System.currentTimeMillis() + SUBFIX);
            for(File file : patchDir.listFiles()) {
                try {
                    String content = FileUtils.readFileToString(file, "UTF-8");
                    FileUtils.write(oneFile, content, true);
                    FileUtils.write(oneFile, "\ngo\n\n", true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void resetContentText(DBObjectBean item) {
        if(item.getType() == null) return ;
        String dropTemplate = String.format(DROP_TEMPLAT, item.getName(), getTypeName(item.getType()), item.getName());
        item.setContentText(dropTemplate +"\n\n"+ item.getContentText());
    }

    private static String getTypeName(String type) {
        for (ObjectEnum objectEnum : ObjectEnum.values()) {
            if(objectEnum.getCode().equalsIgnoreCase(type)) {
                return objectEnum.getName();
            }
        }
        return null;
    }
}

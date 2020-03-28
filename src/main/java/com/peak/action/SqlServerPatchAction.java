package com.peak.action;

import com.peak.util.DateUtils;
import com.peak.util.JDBCUtil;
import com.peak.util.PropertyUtil;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 把目标文件夹的sql在db中执行
 */
public class SqlServerPatchAction {
    private static final Logger logger = Logger.getLogger(SqlServerPatchAction.class);
    private static String filePath = PropertyUtil.getValue("sqlServer.script.dir");
    public static void main(String[] args) throws IOException {
        runScript();
    }
    public static void runScript() {
        File file = new File(filePath);
        Date date = DateUtils.parse(PropertyUtil.getValue("sqlServer.effDate"));
        List<File> fileList = Arrays.asList(file.listFiles());
        // fileList.sort((o1, o2) -> (int)(o1.lastModified() - o2.lastModified()));
        for(File item : fileList) {
            if(item.getName().lastIndexOf(".sql") > -1) {
                if(item.lastModified() > date.getTime()) {
                    try {
                        List<String> scriptList = FileUtils.readLines(item, "utf-8");
                        StringBuffer sbScript = new StringBuffer();
                        for (int i = 0; i < scriptList.size(); i++) {
                            if(i > 6) {
                                sbScript.append(scriptList.get(i)).append("\n");
                            }
                        }
                        System.out.println(sbScript);
                       JDBCUtil.runScript(sbScript.toString(), item.getName());
                    } catch (IOException e) {
                        logger.error(e);
                    }
                }
            }
        }
        JDBCUtil.release();
    }

}

package com.peak.db.sqlserver;

import com.peak.db.DBObjectBean;
import com.peak.db.DBService;
import com.peak.db.ObjectEnum;
import com.peak.db.QueryParam;
import com.peak.util.DateUtils;
import com.peak.util.JDBCUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class SqlserverService implements DBService {
    private static final Logger logger = Logger.getLogger(SqlserverService.class);
    @Override
    public List<DBObjectBean> listDBObject(QueryParam queryParam) {
        return JDBCUtil.queryDBObject(getQuerySql(queryParam));
    }

    private String getQuerySql(QueryParam queryParam) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT o.name name, m.definition definition, o.modify_date, o.type ");
        sb.append("FROM sys.sql_modules AS m ");
        sb.append("INNER JOIN sys.all_objects AS o ON m.object_id = o.object_id ");
        sb.append("WHERE o.[type] in ");
        sb.append(getObjectType(queryParam.getTypeList()));
        sb.append("and o.modify_date >= '");
        sb.append(DateUtils.format(queryParam.getLastUpdateTime()));
        sb.append("' ");
        System.out.println(sb);
        return sb.toString();
    }

    private String getObjectType(List<ObjectEnum> typeList) {
        StringBuilder sb = new StringBuilder();
        sb.append(" ('");
        if(typeList != null) {
            List<String> list = typeList.stream().map(ObjectEnum::getCode).collect(Collectors.toList());
            sb.append(StringUtils.join(list, "','"));
        }
        sb.append("') ");
        return sb.toString();
    }
}

package com.peak.db;

import java.util.List;

public interface DBService {
    List<DBObjectBean> listDBObject(QueryParam queryParam);
}

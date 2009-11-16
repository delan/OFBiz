/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ofbiz.sql;

import java.util.List;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilGenerics;

public final class SQLInsert<P extends InsertPlan<P>> extends SQLStatement<SQLInsert<P>, P> {
    private final TableName tableName;
    private final InsertSource source;
    private final List<String> columns;

    public SQLInsert(TableName tableName, InsertSource source, List<String> columns) {
        this.tableName = tableName;
        this.source = source;
        this.columns = columns;
    }

    @SuppressWarnings("unchecked")
    public <PP extends P> PP plan(Planner<?, ?, ?, ?, ?, ?> planner) {
        return (PP) planner.plan(this);
    }

    public StringBuilder appendTo(StringBuilder sb) {
        sb.append("INSERT INTO ");
        tableName.appendTo(sb);
        if (columns != null && !columns.isEmpty()) {
            sb.append(" (");
            StringUtil.append(sb, columns, null, null, ", ");
            sb.append(')');
        }
        sb.append(' ');
        source.appendTo(sb);
        return sb;
    }
}

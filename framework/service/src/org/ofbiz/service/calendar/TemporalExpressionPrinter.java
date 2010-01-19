/*******************************************************************************
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
 *******************************************************************************/
package org.ofbiz.service.calendar;

import org.ofbiz.service.calendar.TemporalExpressions.DateRange;
import org.ofbiz.service.calendar.TemporalExpressions.DayInMonth;
import org.ofbiz.service.calendar.TemporalExpressions.DayOfMonthRange;
import org.ofbiz.service.calendar.TemporalExpressions.DayOfWeekRange;
import org.ofbiz.service.calendar.TemporalExpressions.Difference;
import org.ofbiz.service.calendar.TemporalExpressions.Frequency;
import org.ofbiz.service.calendar.TemporalExpressions.HourRange;
import org.ofbiz.service.calendar.TemporalExpressions.Intersection;
import org.ofbiz.service.calendar.TemporalExpressions.MinuteRange;
import org.ofbiz.service.calendar.TemporalExpressions.MonthRange;
import org.ofbiz.service.calendar.TemporalExpressions.Null;
import org.ofbiz.service.calendar.TemporalExpressions.Substitution;
import org.ofbiz.service.calendar.TemporalExpressions.TimeOfDayRange;
import org.ofbiz.service.calendar.TemporalExpressions.Union;

/** Temporal expression pretty printer. */
@SuppressWarnings("deprecation")
public class TemporalExpressionPrinter implements TemporalExpressionVisitor {
    protected final TemporalExpression expression;
    protected final StringBuilder sb = new StringBuilder();
    protected int indentSize = 2;
    protected int currentIndent = 0;
    
    public TemporalExpressionPrinter(TemporalExpression expression) {
        this.expression = expression;
    }

    public TemporalExpressionPrinter(TemporalExpression expression, int indentSize) {
        this.expression = expression;
        if (indentSize > 0) {
            this.indentSize = indentSize;
        }
    }

    protected void appendExpression(TemporalExpression expression) {
        appendIndent();
        this.sb.append(expression);
        this.sb.append("\n");
    }

    protected void appendIndent() {
        for (int i = 0; i < this.currentIndent; i++) {
            this.sb.append(" ");
        }
    }

    protected void indent() {
        this.currentIndent += this.indentSize;
    }

    @Override
    public String toString() {
        this.expression.accept(this);
        return this.sb.toString();
    }

    protected void unIndent() {
        this.currentIndent -= this.indentSize;
    }

    @Override
    public void visit(DateRange expr) {
        appendExpression(expr);
    }

    @Override
    public void visit(DayInMonth expr) {
        appendExpression(expr);
    }

    @Override
    public void visit(DayOfMonthRange expr) {
        appendExpression(expr);
    }

    @Override
    public void visit(DayOfWeekRange expr) {
        appendExpression(expr);
    }

    @Override
    public void visit(Difference expr) {
        appendIndent();
        this.sb.append("Difference [");
        this.sb.append(expr.getId());
        this.sb.append("]:\n");
        indent();
        appendIndent();
        this.sb.append("Include:\n");
        indent();
        expr.included.accept(this);
        unIndent();
        appendIndent();
        this.sb.append("Exclude:\n");
        indent();
        expr.excluded.accept(this);
        unIndent();
        unIndent();
    }

    @Override
    public void visit(Frequency expr) {
        appendExpression(expr);
    }

    @Override
    public void visit(HourRange expr) {
        appendExpression(expr);
    }

    @Override
    public void visit(Intersection expr) {
        appendIndent();
        this.sb.append("Intersection [");
        this.sb.append(expr.getId());
        this.sb.append("]:\n");
        indent();
        for (TemporalExpression member: expr.expressionSet) {
            member.accept(this);
        }
        unIndent();
    }

    @Override
    public void visit(MinuteRange expr) {
        appendExpression(expr);
    }

    @Override
    public void visit(MonthRange expr) {
        appendExpression(expr);
    }

    @Override
    public void visit(Null expr) {
        appendExpression(expr);
    }

    @Override
    public void visit(Substitution expr) {
        appendIndent();
        this.sb.append("Substitution [");
        this.sb.append(expr.getId());
        this.sb.append("]:\n");
        indent();
        appendIndent();
        this.sb.append("Include:\n");
        indent();
        expr.included.accept(this);
        unIndent();
        appendIndent();
        this.sb.append("Exclude:\n");
        indent();
        expr.excluded.accept(this);
        unIndent();
        appendIndent();
        this.sb.append("Substitute:\n");
        indent();
        expr.substitute.accept(this);
        unIndent();
        unIndent();
    }

    @Override
    public void visit(TimeOfDayRange expr) {
        appendExpression(expr);
    }

    @Override
    public void visit(Union expr) {
        appendIndent();
        this.sb.append("Union [");
        this.sb.append(expr.getId());
        this.sb.append("]:\n");
        indent();
        for (TemporalExpression member: expr.expressionSet) {
            member.accept(this);
        }
        unIndent();
    }
}

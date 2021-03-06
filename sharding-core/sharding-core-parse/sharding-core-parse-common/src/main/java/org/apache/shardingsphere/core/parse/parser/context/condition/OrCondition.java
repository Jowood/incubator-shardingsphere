/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.core.parse.parser.context.condition;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.shardingsphere.core.parse.parser.clause.condition.NullCondition;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Or conditions.
 *
 * @author maxiaoguang
 */
@NoArgsConstructor
@Getter
@ToString
public final class OrCondition {
    
    private final List<AndCondition> andConditions = new ArrayList<>();
    
    public OrCondition(final Condition condition) {
        add(condition);
    }
    
    /**
     * Add condition.
     *
     * @param condition condition
     */
    public void add(final Condition condition) {
        if (andConditions.isEmpty()) {
            andConditions.add(new AndCondition());
        }
        andConditions.get(0).getConditions().add(condition);
    }
    
    /**
     * Optimize or condition.
     *
     * @return or condition
     */
    public OrCondition optimize() {
        for (AndCondition each : andConditions) {
            if (each.getConditions().get(0) instanceof NullCondition) {
                OrCondition result = new OrCondition();
                result.add(new NullCondition());
                return result;
            }
        }
        return this;
    }
    
    /**
     * Find conditions by column.
     * 
     * @param column column
     * @return conditions
     */
    public List<Condition> findConditions(final Column column) {
        List<Condition> result = new LinkedList<>();
        for (AndCondition each : andConditions) {
            result.addAll(Collections2.filter(each.getConditions(), new Predicate<Condition>() {
                
                @Override
                public boolean apply(final Condition input) {
                    return input.getColumn().equals(column);
                }
            }));
        }
        return result;
    }
}

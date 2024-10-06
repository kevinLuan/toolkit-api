/*
 * Copyright © 2024 Taskflow, Inc.
 *
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.feiliu.taskflow.toolkit.page;

import java.util.List;

/**
 * @author shoushen.luan
 * @since 2022-10-23
 */
@FunctionalInterface
public interface ResultFunc<T> {
    /**
     * 获取返回结果
     *
     * @param result 数据库查询返回list
     * @param paged  分页实例信息
     */
    void apply(List<T> result, Paged<T> paged);
}
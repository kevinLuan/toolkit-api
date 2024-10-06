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
package cn.feiliu.taskflow.toolkit.scan.device;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * 打印机设备信息
 *
 * @author shoushen.luan
 * @since 2022-11-15
 */
@Data
public class PrinterDevice {
    private static final String       DEVICE_NAME = "DEVICENAME";
    private static final String       MAC         = "MAC";
    private static final String       IP          = "IP";
    private static final String       ID          = "ID";
    private final Map<String, String> infos       = new HashMap<>();

    public void addInfo(String name, String value) {
        this.infos.put(name, value);
    }

    public String getInfo(String name) {
        return infos.get(name);
    }

    @Override
    public String toString() {
        String id = infos.get(ID);
        String deviceName = infos.get(DEVICE_NAME);
        String mac = infos.get(MAC);
        String ip = infos.get(IP);
        return "id:" + id + "|device:" + deviceName + "|mac:" + mac + "|ip:" + ip;
    }

    public boolean isPrinterDevice() {
        return "GP 80 Printer".equals(this.infos.get(DEVICE_NAME));
    }

    public String getMac() {
        return this.infos.get(MAC);
    }

    public String getIP() {
        return this.infos.get(IP);
    }

}

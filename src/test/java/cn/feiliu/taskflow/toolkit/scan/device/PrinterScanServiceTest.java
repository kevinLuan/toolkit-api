/*
 * Copyright 2024 Taskflow, Inc.
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

import lombok.SneakyThrows;
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;

/**
 * @author SHOUSHEN.LUAN
 * @since 2023-02-13
 */
public class PrinterScanServiceTest {
    @Test
    @SneakyThrows
    public void test() {
        try (PrinterScanService scanService = new PrinterScanService()) {
            scanService.sendDatagram();
            Thread.sleep(1000);
            System.out.println("扫描打印机设备...");
            scanService.getPrinters().forEach((device) -> {
                System.out.println(device.toString());
            });
        }
    }

    @Test
    public void testEmpty() {
        PrinterDevice printerDevice = new PrinterDevice();
        assertThat(printerDevice.isPrinterDevice()).isFalse();
    }
}

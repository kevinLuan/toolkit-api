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

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.SocketException;
import java.util.Collection;

/**
 * Test class for PrinterScanService
 *
 * @author SHOUSHEN.LUAN
 * @since 2023-02-13
 */
public class PrinterScanServiceTests {

    private PrinterScanService scanService;

    @Before
    public void setUp() throws SocketException {
        scanService = new PrinterScanService();
    }

    @After
    public void tearDown() throws Exception {
        if (scanService != null) {
            scanService.close();
        }
    }

    @Test
    public void testScanPrinters() throws InterruptedException, IOException {
        scanService.sendDatagram();
        Thread.sleep(1000);

        Collection<PrinterDevice> printers = scanService.getPrinters();
        assertNotNull("Printer list should not be null", printers);

        for (PrinterDevice device : printers) {
            assertNotNull("Printer device should not be null", device);
            System.out.println(device.toString());
        }
    }

    @Test
    public void testEmptyPrinterDevice() {
        PrinterDevice printerDevice = new PrinterDevice();
        assertFalse("Empty printer device should not be a printer device", printerDevice.isPrinterDevice());
    }
}

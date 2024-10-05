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
    public void testEmpty(){
        PrinterDevice printerDevice=new PrinterDevice();
        assertThat(printerDevice.isPrinterDevice()).isFalse();
    }
}

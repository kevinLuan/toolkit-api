package cn.feiliu.taskflow.scan.device;

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
    private static final String DEVICE_NAME = "DEVICENAME";
    private static final String MAC = "MAC";
    private static final String IP = "IP";
    private static final String ID = "ID";
    private final Map<String, String> infos = new HashMap<>();

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

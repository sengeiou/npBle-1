package demo.nopointer.ble.bleModule.measure;

import demo.nopointer.ble.bleModule.BleCfg;
import npble.nopointer.util.BleUtil;

/**
 * 检测数据的解析对象
 */
public class DevMeasureUtil implements BleCfg {


    private DevMeasureUtil() {
    }

    public static void receiveData(byte data[]) {
        int flag = BleUtil.byte2IntLR(data[3], data[4]);

        if (flag == MEASURE_DATA_FLAG) {
            //心率数据 包含app端测试的 也包含三设备端测试的
        }
    }



    public static void main(String[]args){
        String mac  ="D6:60:C9:0E:E1:FB";
        System.out.println(mac.matches("^[A-F0-9]{2}(:[A-F0-9]{2}){5}$"));
    }


}
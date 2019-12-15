package lib.ycble.ble;

import android.content.Context;

import java.util.UUID;

import npble.nopointer.exception.BleUUIDNullException;
import npble.nopointer.ble.conn.NpBleAbsConnManager;
import npble.nopointer.ota.absimpl.xc.no.nordicsemi.android.BleManagerCallbacks;

public class NpBleManager extends NpBleAbsConnManager implements BleUUIDCfg {
    /**
     * The manager constructor.
     * <p>
     * After constructing the manager, the callbacks object must be set with
     * {@link #setGattCallbacks(BleManagerCallbacks)}.
     * <p>
     * To connect a device, call {@link #connect(BluetoothDevice)}.
     *
     * @param context the context.
     */
    private NpBleManager(Context context) {

        super(context);
    }

    @Override
    public void loadCfg() {
        try {
            setNotificationCallback(U_SER,U_notify);
            enableNotifications(U_SER,U_notify);
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x13,19,12,15});
            writeCharacteristic(U_SER,U_write,new byte[]{0x13,19,12,14});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x13,19,12,13});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x13,19,12,12});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
//            writeCharacteristic(U_SER,U_write,new byte[]{0x51,0x01});
        } catch (BleUUIDNullException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    @Override
    protected void onDataReceive(byte[] data, UUID uuid) {

    }


    private static NpBleManager instance = null;

    public static NpBleManager getInstance(Context context) {
        if (instance == null) {
            instance = new NpBleManager(context);
        }
        return instance;
    }





}
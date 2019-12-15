package npble.nopointer.ble.conn;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.data.Data;
import no.nordicsemi.android.log.LogContract;
import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.log.Logger;
import npble.nopointer.ble.conn.callbacks.NpBleCallback;
import npble.nopointer.ble.conn.callbacks.NpDataReceivedCallback;
import npble.nopointer.ble.conn.callbacks.NpDataSentCallback;
import npble.nopointer.exception.BleUUIDNullException;
import npble.nopointer.log.ycBleLog;
import npble.nopointer.util.BleUtil;

public abstract class NpBleAbsConnManager extends BleManager<NpBleCallback> {


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
    public NpBleAbsConnManager(@NonNull Context context) {
        super(context);
        setGattCallbacks(npBleCallback);
    }


    private BluetoothGatt mBluetoothGatt = null;

    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return mGattCallback;
    }


    public void connDevice(String mac) {
        refreshDeviceCache();
        BluetoothDevice bluetoothDevice = BleUtil.isInConnList(mac, getContext());
        if (bluetoothDevice == null) {
            bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mac);
        }
        ycBleLog.e("bluetoothDevice : " + bluetoothDevice.getName() + "///" + bluetoothDevice.getAddress());
        if (bluetoothDevice != null) {
            connect(bluetoothDevice)
                    .retry(3, 100)
                    .useAutoConnect(false)
                    .enqueue();
        }
    }

    /**
     * 必须的特征uuid
     */
    private HashSet<UUID> mustUUIDList = null;

    /**
     * 设置设备的uuid 用来区分是否是本项目的设备
     *
     * @param uuids
     */
    public void setMustUUID(UUID... uuids) {
        if (mustUUIDList == null) {
            mustUUIDList = new HashSet<>();
        }
        if (mustUUIDList != null && uuids != null) {
            for (UUID uuid : uuids) {
                mustUUIDList.add(uuid);
            }
        }
    }

    protected void addMustUUID(UUID uuid) {
        mustUUIDList.add(uuid);
    }

    protected void clearMustUUID() {
        if (mustUUIDList != null) {
            mustUUIDList.clear();
        }
    }


    @Override
    protected boolean shouldClearCacheWhenDisconnected() {
        return true;
    }


    /**
     * BluetoothGatt callbacks object.
     */
    private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {
        @Override
        protected void initialize() {
            ycBleLog.e("initialize===>");
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            loadCfg();
//            setNotificationCallback(mButtonCharacteristic).with(mButtonCallback);
//            readCharacteristic(mLedCharacteristic).with(mLedCallback).enqueue();
//            readCharacteristic(mButtonCharacteristic).with(mButtonCallback).enqueue();
//            enableNotifications(mButtonCharacteristic).enqueue();
//            disableNotifications()
        }

        @Override
        protected void onDeviceDisconnected() {
            mBluetoothGatt = null;
        }


        @Override
        public boolean isRequiredServiceSupported(@NonNull final BluetoothGatt gatt) {
            mBluetoothGatt =gatt;
            ycBleLog.e("判断设备支持与否===>" + new Gson().toJson(mustUUIDList));
            if (mustUUIDList == null || mustUUIDList.size() < 0) return true;
            int count = 0;
            for (BluetoothGattService bluetoothGattService : gatt.getServices()) {
                for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService.getCharacteristics()) {
                    for (UUID uuid : mustUUIDList) {
                        if (uuid.equals(bluetoothGattCharacteristic.getUuid())) {
                            count++;
                        }
                    }
                }
            }
            if (count == mustUUIDList.size()) {
                return true;
            } else {
                ycBleLog.e("uuid对不上，情况不对");
                return false;
            }
        }

    };


    private LogSession mLogSession;

    /**
     * Sets the log session to be used for low level logging.
     *
     * @param session the session, or null, if nRF Logger is not installed.
     */
    public void setLogger(@Nullable final LogSession session) {
        this.mLogSession = session;
    }

    @Override
    public void log(final int priority, @NonNull final String message) {
        // The priority is a Log.X constant, while the Logger accepts it's log levels.
        Logger.log(mLogSession, LogContract.Log.Level.fromPriority(priority), message);
    }

    private NpBleCallback npBleCallback = new NpBleCallback() {
        @Override
        public void onDeviceConnecting(@NonNull BluetoothDevice device) {
            ycBleLog.e("onDeviceConnecting : " + device.getAddress());
        }

        @Override
        public void onDeviceConnected(@NonNull BluetoothDevice device) {
            ycBleLog.e("onDeviceConnected : " + device.getAddress());
        }

        @Override
        public void onDeviceDisconnecting(@NonNull BluetoothDevice device) {
            ycBleLog.e("onDeviceDisconnecting : " + device.getAddress());
        }

        @Override
        public void onDeviceDisconnected(@NonNull BluetoothDevice device) {
            ycBleLog.e("onDeviceDisconnected : " + device.getAddress());

            connDevice(device.getAddress());
        }

        @Override
        public void onLinkLossOccurred(@NonNull BluetoothDevice device) {
            ycBleLog.e("onLinkLossOccurred : " + device.getAddress());
        }

        @Override
        public void onServicesDiscovered(@NonNull BluetoothDevice device, boolean optionalServicesFound) {
            ycBleLog.e("onServicesDiscovered : " + device.getAddress());
        }

        @Override
        public void onDeviceReady(@NonNull BluetoothDevice device) {
            ycBleLog.e("onDeviceReady : " + device.getAddress());
        }

        @Override
        public void onBondingRequired(@NonNull BluetoothDevice device) {
            ycBleLog.e("onBondingRequired : " + device.getAddress());
        }

        @Override
        public void onBonded(@NonNull BluetoothDevice device) {
            ycBleLog.e("onBonded : " + device.getAddress());
        }

        @Override
        public void onBondingFailed(@NonNull BluetoothDevice device) {
            ycBleLog.e("onBondingFailed : " + device.getAddress());
        }

        @Override
        public void onError(@NonNull BluetoothDevice device, @NonNull String message, int errorCode) {
            ycBleLog.e("onError : " + device.getAddress());
        }

        @Override
        public void onDeviceNotSupported(@NonNull BluetoothDevice device) {
            ycBleLog.e("onDeviceNotSupported : " + device.getAddress());
        }
    };


    /**
     * 处理连接后的时序，用户根据需求添加一系列的同步指令，比如同步时间，打开通知，读取数据等等,不需要的话就不管
     */
    public abstract void loadCfg();


    /**
     * 读取特征
     *
     * @param serviceUUId
     * @param uuid
     * @throws BleUUIDNullException
     */
    public void readCharacteristic(UUID serviceUUId, UUID uuid) throws BleUUIDNullException {
        readCharacteristic(BleUtil.getCharacteristic(mBluetoothGatt, serviceUUId, uuid)).with(new NpDataReceivedCallback(uuid) {
            @Override
            public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data, UUID uuid) {

            }
        }).enqueue();
    }

    /**
     * 写特征
     *
     * @param serviceUUId
     * @param uuid
     * @throws BleUUIDNullException
     */
    public void writeCharacteristic(UUID serviceUUId, UUID uuid, byte[] data) throws BleUUIDNullException {
        writeCharacteristic(BleUtil.getCharacteristic(mBluetoothGatt, serviceUUId, uuid), data).with(new NpDataSentCallback(uuid) {
            @Override
            public void onDataSent(@NonNull BluetoothDevice device, @NonNull Data data, UUID uuid) {
                ycBleLog.e("onDataSent : " + uuid.toString() + "{ " + BleUtil.byte2HexStr(data.getValue()) + " }");
            }
        }).enqueue();
    }

    /**
     * 设置监听
     *
     * @param serviceUUId
     * @param uuid
     * @throws BleUUIDNullException
     */
    public void setNotificationCallback(UUID serviceUUId, UUID uuid) throws BleUUIDNullException {
        setNotificationCallback(BleUtil.getCharacteristic(mBluetoothGatt, serviceUUId, uuid)).with(new NpDataReceivedCallback(uuid) {
            @Override
            public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data, UUID uuid) {
                ycBleLog.e("onDataReceived : " + uuid.toString() + "{ " + BleUtil.byte2HexStr(data.getValue()) + " }");
            }
        });
    }

    /**
     * 使能通知
     *
     * @param serviceUUId
     * @param uuid
     * @throws BleUUIDNullException
     */
    public void enableNotifications(UUID serviceUUId, UUID uuid) throws BleUUIDNullException {
        enableNotifications(BleUtil.getCharacteristic(mBluetoothGatt, serviceUUId, uuid)).with(new NpDataSentCallback(uuid) {
            @Override
            public void onDataSent(@NonNull BluetoothDevice device, @NonNull Data data, UUID uuid) {
                ycBleLog.e("onDataSent : " + uuid.toString() + "{ enableNotifications }");
            }
        }).enqueue();
    }


}

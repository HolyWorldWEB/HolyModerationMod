package me.zyouime.holymoderation.core.obs;

public final class ObsMessage {

    public static final int HELLO = 0;
    public static final int IDENTIFY = 1;
    public static final int IDENTIFIED = 2;
    public static final int EVENT = 5;
    public static final int REQUEST = 6;
    public static final int REQUEST_RESPONSE = 7;
    public static final int RPC_VERSION = 1;
    public static final String START_RECORD = "StartRecord";
    public static final String STOP_RECORD = "StopRecord";
    public static final String GET_RECORD_STATUS = "GetRecordStatus";
    public static final String RECORD_STATE_CHANGED = "RecordStateChanged";
    public static final String OUTPUT_STOPPED = "OBS_WEBSOCKET_OUTPUT_STOPPED";
}
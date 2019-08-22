package za.co.technoris.iamfit.common.thirdpartyplatform;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import za.co.technoris.iamfit.ble.HeartRate;
import za.co.technoris.iamfit.ble.SportDataDay;
import za.co.technoris.iamfit.common.utils.DateUtil;

public class GoogleFitDataHelper {
    public static final String LAST_SYS_TIME = "LAST_SYS_TIME";
    public static final String googleFitKey = "iamfit";
    float allCalory = 0.0f;
    float allDistance = 0.0f;
    float avgRate;
    public long currentUploadTime;
    float height;
    boolean isUpload = false;
    int lastUpdateItem = 0;
    public long lastUploadTime;
    String log;
    private List<HeartRate> mAllData = new ArrayList();
//    protected AppSharedPreferencesUtils share = AppSharedPreferencesUtils.getInstance();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    int stepAllCount = 0;
    float weight;

    private void initData() {
        this.mAllData.clear();
        this.stepAllCount = 0;
        this.allDistance = 0.0f;
        this.allCalory = 0.0f;
        this.avgRate = 0.0f;
        this.isUpload = false;
        this.height = 0.0f;
    }

    public void getUploadData() {
        initData();
        int[] yearMonthDays = DateUtil.todayYearMonthDay();
//        SportDataDay sport = new SportDataDay(yearMonthDays[0], yearMonthDays[1], yearMonthDays[2]);
    }
}

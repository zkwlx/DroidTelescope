package andr.perf.monitor.cpu.models;

import java.util.List;

/**
 * Created by ZhouKeWen on 17/3/28.
 */
public class BlockInfo {

    private long useMsTime;

    private long useThreadTime;

    private List<MethodInfo> rootMethodList;

    public List<MethodInfo> getRootMethodList() {
        return rootMethodList;
    }

    public void setRootMethodList(List<MethodInfo> rootMethodList) {
        this.rootMethodList = rootMethodList;
    }

    public long getUseThreadTime() {
        return useThreadTime;
    }

    public void setUseThreadTime(long useThreadTime) {
        this.useThreadTime = useThreadTime;
    }

    public long getUseMsTime() {
        return useMsTime;
    }

    public void setUseMsTime(long useMsTime) {
        this.useMsTime = useMsTime;
    }
}

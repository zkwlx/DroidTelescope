package andr.perf.monitor.memory.models;

import java.util.LinkedList;
import java.util.List;

import andr.perf.monitor.memory.SuspectWeakReference;

/**
 * Created by ZhouKeWen on 2017/4/7.
 */
public class LeakInfo {

    private List<SuspectWeakReference> referenceList;

    public LeakInfo() {
    }

    public void addGarbageReference(SuspectWeakReference reference) {
        if (referenceList == null) {
            referenceList = new LinkedList<>();
        }
        referenceList.add(reference);
    }

    public List<SuspectWeakReference> getReferenceList() {
        return referenceList;
    }
}

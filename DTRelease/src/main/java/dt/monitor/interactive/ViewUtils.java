package dt.monitor.interactive;

import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewParent;

import java.util.ArrayList;

/**
 * Created by ZhouKeWen on 2017/5/15.
 */
public class ViewUtils {

    /**
     * 为了提高性能，使用一个boolean
     * TODO 注意多线程安全
     */
//    private static boolean isRootParent = false;

    /**
     * 获取一个View的Parent列表
     *
     * @param view
     * @return
     */
    public static String[] getParentArray(View view) {
        ArrayList<String> arrayList = new ArrayList<>(8);
        ViewParent parent = view.getParent();
//        isRootParent = false;
        while (parent != null && parent instanceof View) {
            String parentSign = getViewSign((View) parent);
            arrayList.add(parentSign);
            if (parentSign.contains("android:id/content")) {
                //找到android:id/content的父View，认为是根View了，不再查找
                break;
            } else {
                parent = parent.getParent();
            }
//            if (isRootParent) {
//                //找到android:id/content的父View，认为是根View了，不再查找
//                parent = null;
//            } else {
//                parent = parent.getParent();
//            }
        }
        String[] result = new String[]{};
        return arrayList.toArray(result);
    }

    /**
     * 获取View的签名
     *
     * @param view
     * @return
     */
    public static String getViewSign(View view) {
        StringBuilder sign = new StringBuilder(128);
        sign.append(view.getClass().getName());
        sign.append('{');
        sign.append(Integer.toHexString(System.identityHashCode(view)));
        sign.append(' ');
        sign.append(view.getLeft());
        sign.append(',');
        sign.append(view.getTop());
        sign.append('-');
        sign.append(view.getRight());
        sign.append(',');
        sign.append(view.getBottom());
        final int id = view.getId();
        if (id != View.NO_ID) {
            sign.append(" #");
            sign.append(Integer.toHexString(id));
            String resourceId = getResourceId(view.getResources(), id);
            if (!TextUtils.isEmpty(resourceId)) {
                sign.append(" ");
                sign.append(resourceId);
            }
//            final Resources r = view.getResources();
//            if (id > 0 && resourceHasPackage(id) && r != null) {
//                try {
//                    String pkgname;
//                    switch (id & 0xff000000) {
//                        case 0x7f000000:
//                            pkgname = "app";
//                            break;
//                        case 0x01000000:
//                            pkgname = "android";
//                            break;
//                        default:
//                            pkgname = r.getResourcePackageName(id);
//                            break;
//                    }
//                    String typename = r.getResourceTypeName(id);
//                    String entryname = r.getResourceEntryName(id);
//                    sign.append(" ");
//                    sign.append(pkgname);
//                    sign.append(":");
//                    sign.append(typename);
//                    sign.append("/");
//                    sign.append(entryname);
////                    if ("android".equals(pkgname) && "id".equals(typename) && "content".equals(entryname)) {
////                        //找到android:id/content的父View，认为是根View了，不再查找
////                        isRootParent = true;
////                    }
//                } catch (Resources.NotFoundException e) {
//                }
//            }
        }
        sign.append('}');
        return sign.toString();
    }

    /**
     * 传入 int 类型的资源 ID，
     * 返回类似 app:id/search_box 或 android:id/content 结构的 id 名字
     *
     * @param r
     * @param id
     * @return
     */
    public static String getResourceId(final Resources r, final int id) {
        if (id > 0 && resourceHasPackage(id) && r != null) {
            StringBuilder sign = new StringBuilder(64);
            try {
                String pkgname;
                switch (id & 0xff000000) {
                    case 0x7f000000:
                        pkgname = "app";
                        break;
                    case 0x01000000:
                        pkgname = "android";
                        break;
                    default:
                        pkgname = r.getResourcePackageName(id);
                        break;
                }
                String typename = r.getResourceTypeName(id);
                String entryname = r.getResourceEntryName(id);
//                sign.append(" ");
                sign.append(pkgname);
                sign.append(":");
                sign.append(typename);
                sign.append("/");
                sign.append(entryname);
            } catch (Resources.NotFoundException e) {
            }
            return sign.toString();
        }
        return null;
    }

    private static boolean resourceHasPackage(int resId) {
        //实现来自android.content.res.Resources的隐藏方法
        return (resId >>> 24) != 0;
    }

}

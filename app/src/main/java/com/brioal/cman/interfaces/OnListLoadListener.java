package com.brioal.cman.interfaces;

import java.util.List;

/**Read Data From Local Listener
 * Created by Brioal on 2016/9/11.
 */
public interface OnListLoadListener {
    void succeed(List list);

    void failed(String errorMessage);
}

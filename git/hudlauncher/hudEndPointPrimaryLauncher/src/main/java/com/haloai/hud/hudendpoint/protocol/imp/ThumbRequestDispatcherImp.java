package com.haloai.hud.hudendpoint.protocol.imp;

import android.content.Context;

import com.haloai.hud.carrecorderlibrary.utils.HudCarcorderConstants;
import com.haloai.hud.hudendpoint.phoneconnection.IDataDispatcher;
import com.haloai.hud.model.v2.AHudListResponse;
import com.haloai.hud.model.v2.AHudResponse;
import com.haloai.hud.model.v2.Phone2HudMessages;
import com.haloai.hud.model.v2.ThumbRequest;
import com.haloai.hud.model.v2.ThumbnailTransferResp;
import com.haloai.hud.proto.v2.CommonMessagesProtoDef;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：hudlauncher
 * 类描述：
 * 创建人：zhang
 * 创建时间：2016/5/9 14:42
 * 修改人：zhang
 * 修改时间：2016/5/9 14:42
 * 修改备注：
 */
public class ThumbRequestDispatcherImp implements IDataDispatcher {

    private Context mContext;
    public ThumbRequestDispatcherImp(Context context) {
        this.mContext = context;
    }

    @Override
    public AHudResponse dispatchP2HData(Phone2HudMessages phone2HudMessages) {
        List<ThumbRequest> thumbRequestList = phone2HudMessages.getThumbRequestList();
        if (thumbRequestList == null || thumbRequestList.size() <= 0) {
            return null;
        }

        // 需要返回信息
        AHudListResponse aHudListResponse = new AHudListResponse();
        List<ThumbnailTransferResp> thumbnailTransferRespList = aHudListResponse.getThumbnailTransferRespList();


        for (ThumbRequest thumbRequest : thumbRequestList) {
            CommonMessagesProtoDef.RecorderTypeProto recorderType = thumbRequest.getRecorderType();
            int thumbnailIndex = thumbRequest.getThumbnailIndex();
            int thumbnailNumber = thumbRequest.getThumbnailNumber();

            /*//TODO 测试程序，之后删除
            final String print = "thumbnailIndex:" + thumbnailIndex + "***"+  "thumbnailNumber:" + thumbnailNumber;
            Handler testHandler=new Handler(Looper.getMainLooper());
            testHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext,"已连接设备:" + print,Toast.LENGTH_LONG).show();
                }
            });
            HaloLogger.logE("ThumbRequestDispatcherImp", print);*/

            /*String path = null;
            // 更新对应视频列表
            switch (recorderType) {
                case LOOP_VIDEO:
                    path = HudCarcorderConstants.LOOPINGVIDEOPATH;
                    break;
                case LOCK_VIDEO:
                    path = HudCarcorderConstants.LOCKEDVIDEOPATH;
                    break;
            }
*/
            ThumbnailTransferResp thumbnailTransferResp = new ThumbnailTransferResp();
            thumbnailTransferResp.setRecorderType(recorderType);
            thumbnailTransferResp.setThumbnailIndex(thumbnailIndex);
            thumbnailTransferResp.setThumbnailNumber(thumbnailNumber);

            List<byte[]> thumbnailBytesList = new ArrayList<>();
            thumbnailTransferResp.setThumbnailBytesList(thumbnailBytesList);

            thumbnailTransferRespList.add(thumbnailTransferResp);
        }

        return aHudListResponse;
    }
}

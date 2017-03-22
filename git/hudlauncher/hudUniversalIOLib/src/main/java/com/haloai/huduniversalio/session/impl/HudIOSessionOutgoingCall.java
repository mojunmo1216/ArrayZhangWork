
package com.haloai.huduniversalio.session.impl;

import android.util.Log;

import com.haloai.hud.utils.EndpointsConstants;
import com.haloai.hud.utils.HaloLogger;
import com.haloai.huduniversalio.GesturePoint;
import com.haloai.huduniversalio.HudIOConstants;
import com.haloai.huduniversalio.HudIOConstants.ContactInfo;
import com.haloai.huduniversalio.HudIOConstants.HudIOSessionType;
import com.haloai.huduniversalio.HudIOConstants.HudOutputerType;
import com.haloai.huduniversalio.R;
import com.haloai.huduniversalio.SpeechResourceManager;
import com.haloai.huduniversalio.gesture.PointGestureResult;
import com.haloai.huduniversalio.impl.HudIOController;
import com.haloai.huduniversalio.outputer.IHudOutputer.HudOutputContentType;
import com.haloai.huduniversalio.session.IHudIOSessionCallback;
import com.haloai.huduniversalio.session.IHudIOSessionOutgoingCallCallback;
import com.haloai.huduniversalio.session.wakeupwords.SpeechWakeupWords;
import com.haloai.huduniversalio.session.wakeupwords.SpeechWakeupWordsInCommingCall;
import com.haloai.huduniversalio.session.wakeupwords.SpeechWakeupWordsList;
import com.haloai.huduniversalio.session.wakeupwords.SpeechWakeupWordsListPage;
import com.haloai.huduniversalio.speech.ISpeechParserOutgoingCall;

import java.util.ArrayList;
import java.util.List;


public class HudIOSessionOutgoingCall extends HudIOSession {
    private static final String TAG = HudIOSessionOutgoingCall.class.getName();

    private static final int SESSION_STATE_CALL_START_SESSION = SESSION_STATE_BASE + 1;
    private static final int SESSION_STATE_CALL_CONTACT_CHOOSE = SESSION_STATE_BASE + 2;
    private static final int SESSION_STATE_CALL_PHONENUM_CHOOSE = SESSION_STATE_BASE + 3;
    private static final int SESSION_STATE_CALLING = SESSION_STATE_BASE + 4;
    private static final int ITEM_MAX = 4;
    private static final int PAGE_MAX = 3;
    private int mPageIndex, mPageCount, mItemCount;
    private List<ContactInfo> mContactList;
    private ContactInfo mContact;
    private IHudIOSessionOutgoingCallCallback mSessionCallback;

    public HudIOSessionOutgoingCall(HudIOController hudIOController) {
        super(hudIOController);
        mIOSessionType = HudIOSessionType.OUTGOING_CALL;
        mSpeechParser = mHudIOController.getHudSpeechManager().getSpeechParserFactory().getParserOutgoingCall();
    }

    @Override
    public void onWakeupWord(String wakeupWord) {
        if (mCurrentSpeechWakeupWords == null) {
            // TODO 错误播报
            return;
        }
        int res = mCurrentSpeechWakeupWords.getWakeupResult(wakeupWord);
        if (this.mSessionState == SESSION_STATE_CALL_CONTACT_CHOOSE) {
            if (res != SpeechWakeupWords.WAKEUP_RESULT_NOT_FOUND) {
                if (res <= SpeechWakeupWordsListPage.WAKEUP_RESULT_ITEM_3 && res > SpeechWakeupWordsListPage.WAKEUP_RESULT_EXIT) {
                    pickContactItem(res - 1);
                } else if (res == SpeechWakeupWordsListPage.WAKEUP_RESULT_NEXT_PAGE) {
                    if (mPageIndex < mPageCount - 1) {
                        mPageIndex++;
                        mSessionCallback.onSessionOutgoingCallNextPage();
                    }
                } else if (res == SpeechWakeupWordsListPage.WAKEUP_RESULT_PREV_PAGE) {
                    if (mPageIndex > 0) {
                        mPageIndex--;
                        mSessionCallback.onSessionOutgoingCallPrevPage();
                    }
                } else if (res == SpeechWakeupWordsListPage.WAKEUP_RESULT_EXIT) {
                    mSessionCallback.onSessionOutgoingCallEnd(true);
                    endIOSession(true);
                }
            } else {
                this.mHudIOController.getHudOutputer(HudOutputerType.SPEECH_OUTPUT).output(
                        mCurrentSpeechWakeupWords.getPromptText(), HudOutputContentType.CONTENT_TYPE_CUSTOM, false);
            }
        } else if (this.mSessionState == SESSION_STATE_CALL_PHONENUM_CHOOSE) {
            if (res != SpeechWakeupWords.WAKEUP_RESULT_NOT_FOUND) {
                if (res <= SpeechWakeupWordsList.WAKEUP_RESULT_ITEM_4) {
                    if (mContact != null && res < mContact.phone.size()) {
                        mSessionCallback.onSessionOutgoingCalling(mContact.name, mContact.phone.get(res),true);
                    }
                } else if (res == SpeechWakeupWordsList.WAKEUP_RESULT_EXIT) {
                    mSessionCallback.onSessionOutgoingCallEnd(true);
                    endIOSession(true);
                }
            } else {
                this.mHudIOController.getHudOutputer(HudOutputerType.SPEECH_OUTPUT).output(
                        mCurrentSpeechWakeupWords.getPromptText(), HudOutputContentType.CONTENT_TYPE_CUSTOM, false);
            }
        } else if (this.mSessionState == SESSION_STATE_CALLING) {
            if (res != SpeechWakeupWords.WAKEUP_RESULT_NOT_FOUND) {
                if (res == SpeechWakeupWordsInCommingCall.WAKEUP_RESULT_CANCLE) {
                    mSessionCallback.onSessionOutgoingCallEnd(false);
                    endIOSession(false);
                }
            }
        }

    }

    private void pickContactItem(int res) {
        int index = mPageIndex * ITEM_MAX + res;
        if (index < mItemCount && mContactList != null) {
            mContact = mContactList.get(index);
            if (mContact.phone.size() == 1) {//只有一个号码，直接拨号
                this.mSessionState = SESSION_STATE_CALL_PHONENUM_CHOOSE;
                mSessionCallback.onSessionOutgoingCalling(mContact.name, mContact.phone.get(0),true);
            } else if (mContact.phone.size() > 1) {//多号，显示列表选择
                mSessionCallback.onSessionOutgoingCallPhoneNumList(mContact.name, mContact.phone);
                this.mHudIOController.getHudOutputer(HudOutputerType.SPEECH_OUTPUT).output(
                        SpeechResourceManager.getInstanse().getString(R.string.tts_session_navi_poi_pick), HudOutputContentType.CONTENT_TYPE_CUSTOM, false);
            } else {//没有号码，语音播报重试
                this.mHudIOController.getHudOutputer(HudOutputerType.SPEECH_OUTPUT).output(
                        SpeechResourceManager.getInstanse().getString(IHudIOSessionOutgoingCallCallback.MESSAGE_ERROR_EMPTY_PHONE_ID), HudOutputContentType.CONTENT_TYPE_CUSTOM, false);
            }
        }
    }

    @Override
    public void onProtocol(String protocol) {
        ISpeechParserOutgoingCall speechParserOutgoingCall = (ISpeechParserOutgoingCall) mSpeechParser;
        int res = speechParserOutgoingCall.parseSpeechText(protocol);
        if (res == ISpeechParserOutgoingCall.PARSE_RES_CODE_CALL_CONTACT_LIST) {
            this.mSessionState = SESSION_STATE_CALL_START_SESSION;
            mContactList = speechParserOutgoingCall.getContactListInfo();
            if (mContactList != null && mContactList.size() > 0) {
                mPageIndex = 0;
                mPageCount = mContactList.size() == mContactList.size() / ITEM_MAX * ITEM_MAX ? mContactList.size() / ITEM_MAX : mContactList.size() / ITEM_MAX + 1;
                mItemCount = mContactList.size() > PAGE_MAX * ITEM_MAX ? PAGE_MAX * ITEM_MAX : mContactList.size();
                postOutGoingmContactList();
            }
        } else if (res == ISpeechParserOutgoingCall.PARSE_RES_CODE_INTERNAL_ERROR) {
            this.mHudIOController.getHudOutputer(HudOutputerType.SPEECH_OUTPUT).output(
                    SpeechResourceManager.getInstanse().getString(IHudIOSessionOutgoingCallCallback.MESSAGE_ERROR_EMPTY_PHONE_ID), HudOutputContentType.CONTENT_TYPE_CUSTOM, false);
            endIOSession(false);
        }
    }

    @Override
    public boolean onRemoteKey(int keyCode) {
        return false;

    }

    public void onOutCalling(String name,String phoneNumber,boolean isHud){
        this.mSessionState = SESSION_STATE_CALL_PHONENUM_CHOOSE;
        mSessionCallback.onSessionOutgoingCalling(name, phoneNumber,isHud);
    }

    @Override
    public boolean onGestureKey(int gestureCode) {
        if(mSessionState==SESSION_STATE_CALL_CONTACT_CHOOSE){
            if(mPageCount>1){
                if(mPageIndex==0){
                    mPageIndex++;
                    mSessionCallback.onSessionOutgoingCallNextPage();
                    HaloLogger.postI(EndpointsConstants.BTCALL_TAG,"gesture_slide next_contact");
                    return true;
                }else if(mPageIndex==mPageCount-1){
                    mPageIndex--;
                    mSessionCallback.onSessionOutgoingCallPrevPage();
                    HaloLogger.postI(EndpointsConstants.BTCALL_TAG,"gesture_slide prev_contact");
                    return true;
                }
            }else{
                if(gestureCode== HudIOConstants.INPUTER_GESTURE_SLIDE){
                    mSessionCallback.onSessionOutgoingCallEnd(true);
                    endIOSession(false);
                    HaloLogger.postI(EndpointsConstants.BTCALL_TAG,"gesture_slide exit contact");
                    return true;
                }
            }
        }else if(mSessionState==SESSION_STATE_CALL_PHONENUM_CHOOSE){
            if(gestureCode== HudIOConstants.INPUTER_GESTURE_SLIDE){
                mSessionCallback.onSessionOutgoingCallEnd(true);
                endIOSession(false);
                HaloLogger.postI(EndpointsConstants.BTCALL_TAG,"gesture_slide exit phonenum");
                return true;
            }
        }else if(mSessionState==SESSION_STATE_CALLING){
            if(gestureCode== HudIOConstants.INPUTER_GESTURE_PALM) {
                mSessionCallback.onSessionOutgoingCallEnd(false);
                endIOSession(false);
                HaloLogger.postI(EndpointsConstants.BTCALL_TAG,"gesture_slide hangup outcall");
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onGesturePoint(GesturePoint point,PointGestureResult result) {
        return false;
    }

    @Override
    public void setSessionCallback(IHudIOSessionCallback sessionCallback) {
        if (!(sessionCallback instanceof IHudIOSessionOutgoingCallCallback)) {
            throw new IllegalArgumentException("Requirst the IHudIOSessionOutgoingCallCallback");
        }
        this.mSessionCallback = (IHudIOSessionOutgoingCallCallback) sessionCallback;
    }

    @Override
    public void continueSession() {
        if (mSessionState == SESSION_STATE_CALL_START_SESSION) {
            mSessionState = SESSION_STATE_CALL_CONTACT_CHOOSE;
        } else if (mSessionState == SESSION_STATE_CALL_CONTACT_CHOOSE) {
            mSessionState = SESSION_STATE_CALL_PHONENUM_CHOOSE;
        } else if (mSessionState == SESSION_STATE_CALL_PHONENUM_CHOOSE) {
            mSessionState = SESSION_STATE_CALLING;
        }
        setWakeupWordsForCurrentState(mSessionState);
    }

    private void postOutGoingmContactList() {
        List<String> nameList = new ArrayList<String>();
        if (mContactList.size() == 1) {
            this.mSessionState = SESSION_STATE_CALL_CONTACT_CHOOSE;
            pickContactItem(0);
            return;
        }
        for (int i = 0; i < mContactList.size(); i++) {
            String name = mContactList.get(i).name;
            nameList.add(name);
        }
        Log.e(TAG, "nameList:" + nameList.toString());
        mSessionCallback.onSessionOutgoingCallContactList(nameList, mPageCount);
        if (nameList.size() > 4) {
            this.mHudIOController.getHudOutputer(HudOutputerType.SPEECH_OUTPUT).output(
                    SpeechResourceManager.getInstanse().getString(R.string.tts_session_contact_pick), HudOutputContentType.CONTENT_TYPE_CUSTOM, false);
        } else {
            this.mHudIOController.getHudOutputer(HudOutputerType.SPEECH_OUTPUT).output(
                    SpeechResourceManager.getInstanse().getString(R.string.tts_session_contact_pick_num), HudOutputContentType.CONTENT_TYPE_CUSTOM, false);
        }

    }


    private void setWakeupWordsForCurrentState(int currentState) {
        if (currentState == SESSION_STATE_CALL_CONTACT_CHOOSE) {
            mCurrentSpeechWakeupWords = new SpeechWakeupWordsListPage();
        } else if (currentState == SESSION_STATE_CALL_PHONENUM_CHOOSE) {
            mCurrentSpeechWakeupWords = new SpeechWakeupWordsList();
        } else if (currentState == SESSION_STATE_CALLING) {
            mCurrentSpeechWakeupWords = new SpeechWakeupWordsInCommingCall();
        }

        if (mCurrentSpeechWakeupWords != null) {
            mSpeechEngine.setWakupWords(mCurrentSpeechWakeupWords.getAllWakeupWords());
        }
    }

    private List<String> getWakeupWordsBySize() {
        if (mSessionState == SESSION_STATE_CALL_CONTACT_CHOOSE) {
            if (mContactList.size() <= 4 && mContactList.size() > 1) {
                List<String> wakeUpList = new ArrayList<String>();
                wakeUpList.addAll(mCurrentSpeechWakeupWords.getAllWakeupWords());
                for (int i = mContactList.size() + 1; i < SpeechWakeupWordsListPage.WAKEUP_RESULT_PREV_PAGE; i++) {
                    wakeUpList.remove(wakeUpList.size() - 1);
                }
                return wakeUpList;
            }
        }
        return mCurrentSpeechWakeupWords.getAllWakeupWords();
    }

}

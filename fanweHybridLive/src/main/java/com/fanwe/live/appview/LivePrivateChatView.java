package com.fanwe.live.appview;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.fanwe.hybrid.dao.InitActModelDao;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.library.adapter.SDAdapter;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.event.EOnActivityResult;
import com.fanwe.library.handler.PhotoHandler;
import com.fanwe.library.listener.SDSizeListener;
import com.fanwe.library.media.player.SDMediaPlayer;
import com.fanwe.library.media.recorder.SDMediaRecorder;
import com.fanwe.library.media.recorder.SDSimpleMediaRecorderListener;
import com.fanwe.library.model.ImageModel;
import com.fanwe.library.title.SDTitleItem;
import com.fanwe.library.title.SDTitleSimple;
import com.fanwe.library.title.SDTitleSimple.SDTitleSimpleListener;
import com.fanwe.library.utils.ImageFileCompresser;
import com.fanwe.library.utils.SDCollectionUtil;
import com.fanwe.library.utils.SDCountDownTimer;
import com.fanwe.library.utils.SDDateUtil;
import com.fanwe.library.utils.SDFileUtil;
import com.fanwe.library.utils.SDOtherUtil;
import com.fanwe.library.utils.SDToast;
import com.fanwe.library.utils.SDViewSizeListener;
import com.fanwe.library.utils.SDViewSizeLocker;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.library.view.SDRecordView;
import com.fanwe.library.view.SDRecordView.RecordViewListener;
import com.fanwe.library.view.SDRecyclerView;
import com.fanwe.live.IMHelper;
import com.fanwe.live.LiveConstant;
import com.fanwe.live.LiveInformation;
import com.fanwe.live.R;
import com.fanwe.live.activity.LiveUserHomeActivity;
import com.fanwe.live.adapter.LivePrivateChatRecyclerAdapter;
import com.fanwe.live.adapter.viewholder.privatechat.PrivateChatViewHolder;
import com.fanwe.live.common.AppRuntimeWorker;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.dao.UserModelDao;
import com.fanwe.live.dialog.LiveGameExchangeDialog;
import com.fanwe.live.dialog.PrivateChatLongClickMenuDialog;
import com.fanwe.live.event.EImOnNewMessages;
import com.fanwe.live.event.ESDMediaPlayerStateChanged;
import com.fanwe.live.event.ESelectImage;
import com.fanwe.live.model.App_userinfoActModel;
import com.fanwe.live.model.Deal_send_propActModel;
import com.fanwe.live.model.LiveExpressionModel;
import com.fanwe.live.model.LiveGiftModel;
import com.fanwe.live.model.UserModel;
import com.fanwe.live.model.custommsg.CustomMsgPrivateGift;
import com.fanwe.live.model.custommsg.CustomMsgPrivateImage;
import com.fanwe.live.model.custommsg.CustomMsgPrivateText;
import com.fanwe.live.model.custommsg.CustomMsgPrivateVoice;
import com.fanwe.live.model.custommsg.MsgModel;
import com.fanwe.live.model.custommsg.MsgStatus;
import com.fanwe.live.model.custommsg.TIMMsgModel;
import com.fanwe.live.span.LiveExpressionSpan;
import com.fanwe.live.view.SDProgressPullToRefreshRecyclerView;
import com.fanwe.live.view.ScrollListView;
import com.fanwe.live.view.ScrollListView.ScrollListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tencent.TIMConversation;
import com.tencent.TIMMessage;
import com.tencent.TIMValueCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 私聊界面
 */
public class LivePrivateChatView extends BaseAppView implements ScrollListener
{

    /**
     * 延迟多少毫秒执行滚动逻辑
     */
    private static final long SCROLL_DELAY = 10;
    /**
     * 最大输入字符长度
     */
    private static final int MAX_INPUT_LENGTH = 255;


    private SDTitleSimple title;
    private LivePrivateChatRecordView view_record;
    private LivePrivateChatBarView view_chat_bar;
    private SDProgressPullToRefreshRecyclerView lv_content;
    private ViewGroup fl_extend;

    private LiveExpressionView view_expression;
    private LivePrivateChatMoreView view_more;
    private LiveSendGiftView view_gift;

    private String userId;
    private LivePrivateChatRecyclerAdapter adapter;

    private ImageFileCompresser imageFileCompresser;
    private PhotoHandler photoHandler;

    private UserModel user;
    private TIMMessage lastMsg;
    private boolean onUpCancelView;
    private ClickListener clickListener;
    private ExtendVisibilityChangeListener extendVisibilityChangeListener;

    private boolean lockHeightEnable;
    private SDViewSizeListener sizeListener;
    private int lockHeight;
    private int viewFirstHeight;
    private SDViewSizeLocker sizeLocker;
    private boolean shouldUnlockContentWhenKeyboardHide;

    public LivePrivateChatView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public LivePrivateChatView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public LivePrivateChatView(Context context)
    {
        super(context);
        init();
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
        LiveInformation.getInstance().setCurrentChatPeer(userId);
        requestUserInfo(userId);
        loadHistoryMessage();
    }

    public boolean shouldUnlockContentWhenKeyboardHide()
    {
        return shouldUnlockContentWhenKeyboardHide;
    }

    public void setShouldUnlockContentWhenKeyboardHide(boolean shouldUnlockContentWhenKeyboardHide)
    {
        this.shouldUnlockContentWhenKeyboardHide = shouldUnlockContentWhenKeyboardHide;
    }

    /**
     * 设置内容高度锁定功能是否可用
     *
     * @param lockHeightEnable
     */
    public void setLockHeightEnable(boolean lockHeightEnable)
    {
        this.lockHeightEnable = lockHeightEnable;
    }

    /**
     * 设置内容锁定高度
     *
     * @param lockHeight
     */
    public void setLockHeight(int lockHeight)
    {
        this.lockHeight = lockHeight;
    }

    public int getLockHeight()
    {
        return lockHeight;
    }

    /**
     * 获得view第一次测量到的高度
     *
     * @return
     */
    public int getViewFirstHeight()
    {
        return viewFirstHeight;
    }

    /**
     * 获得标题栏高度
     *
     * @return
     */
    public int getTitleViewHeight()
    {
        return SDViewUtil.getViewHeight(title);
    }

    /**
     * 获得底部菜单栏高度
     *
     * @return
     */
    public int getChatBarHeight()
    {
        return SDViewUtil.getViewHeight(view_chat_bar);
    }

    /**
     * 获得聊天内容布局高度
     *
     * @return
     */
    public int getChatContentHeight()
    {
        return SDViewUtil.getViewHeight(lv_content);
    }

    /**
     * 设置扩展布局显示隐藏监听
     *
     * @param extendVisibilityChangeListener
     */
    public void setExtendVisibilityChangeListener(ExtendVisibilityChangeListener extendVisibilityChangeListener)
    {
        this.extendVisibilityChangeListener = extendVisibilityChangeListener;
    }

    public void setClickListener(ClickListener clickListener)
    {
        this.clickListener = clickListener;
    }

    @Override
    protected void init()
    {
        super.init();
        setContentView(R.layout.view_live_private_chat);

        title = find(R.id.title);
        view_record = find(R.id.view_record);
        view_chat_bar = find(R.id.view_chat_bar);
        lv_content = find(R.id.lv_content);
        fl_extend = find(R.id.fl_extend);

        sizeLocker = new SDViewSizeLocker(lv_content);
        initSDViewSizeListener();
        initTitle();

        initPullView();

        view_chat_bar.setClickListener(chatbarClickListener);
        view_chat_bar.et_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_INPUT_LENGTH)});
        view_record.setRecordViewListener(recordViewListener);
        view_record.setRecordView(view_chat_bar.tv_record);


        SDMediaRecorder.getInstance().registerListener(recorderListener);
        SDMediaRecorder.getInstance().registerTimerListener(recordTimerListener);
        SDMediaRecorder.getInstance().setMaxRecordTime(60 * 1000);

        initImageFileCompresser();
        initPhotoHandler();


        if (LiveInformation.getInstance().getRoomId() > 0)
        {
            setVoiceModeEnable(false);
            setTakePhotoEnable(false);
        } else
        {
            setVoiceModeEnable(true);
            setTakePhotoEnable(true);
        }

        dealHasPrivateChat();
    }

    private void initSDViewSizeListener()
    {
        sizeListener = new SDViewSizeListener();
        sizeListener.listen(this, new SDSizeListener<View>()
        {
            @Override
            public void onWidthChanged(int newWidth, int oldWidth, int differ, View target)
            {
            }

            @Override
            public void onHeightChanged(int newHeight, int oldHeight, int differ, View target)
            {
                if (viewFirstHeight <= 0)
                {
                    viewFirstHeight = sizeListener.getFirstHeight();
                }
            }
        });
    }

    private void initImageFileCompresser()
    {
        imageFileCompresser = new ImageFileCompresser();
        imageFileCompresser.setmListener(new ImageFileCompresser.ImageFileCompresserListener()
        {
            @Override
            public void onStart()
            {
                showProgressDialog("正在处理图片");
            }

            @Override
            public void onSuccess(File fileCompressed)
            {
                sendImageFile(fileCompressed);
            }

            @Override
            public void onFailure(String msg)
            {
                SDToast.showToast(msg);
            }

            @Override
            public void onFinish()
            {
                dismissProgressDialog();
            }
        });
    }

    private void initPullView()
    {
        lv_content.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        lv_content.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<SDRecyclerView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<SDRecyclerView> pullToRefreshBase)
            {
                loadHistoryMessage();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<SDRecyclerView> pullToRefreshBase)
            {
            }
        });

        adapter = new LivePrivateChatRecyclerAdapter(getActivity());
        adapter.setClickListener(adapterClickListener);

        lv_content.getRefreshableView().setAdapter(adapter);

    }

    private void initPhotoHandler()
    {
        photoHandler = new PhotoHandler((FragmentActivity) getActivity());
        photoHandler.setListener(new PhotoHandler.PhotoHandlerListener()
        {
            @Override
            public void onResultFromAlbum(File file)
            {
                dealImage(file);
            }

            @Override
            public void onResultFromCamera(File file)
            {
                dealImage(file);
            }

            @Override
            public void onFailure(String msg)
            {

            }
        });
    }

    private void dealImage(File file)
    {
        if (file != null)
        {
            imageFileCompresser.compressImageFile(file);
        }
    }

    /**
     * 设置发送语音是否可用
     *
     * @param voiceModeEnable
     */
    public void setVoiceModeEnable(boolean voiceModeEnable)
    {
        if (view_chat_bar != null)
        {
            view_chat_bar.setVoiceModeEnable(voiceModeEnable);
        }
    }

    /**
     * 设置拍照是否可用
     *
     * @param takePhotoEnable
     */
    public void setTakePhotoEnable(boolean takePhotoEnable)
    {
        getMoreView();
        if (view_more != null)
        {
            view_more.setTakePhotoEnable(takePhotoEnable);
        }
    }

    public void hideInputMethod()
    {
        view_chat_bar.hideInputMethod();
    }

    /**
     * 获得表情布局
     *
     * @return
     */
    private View getExpressionView()
    {
        if (view_expression == null)
        {
            view_expression = new LiveExpressionView(getContext());
            view_expression.setClickListener(expressionListener);
        }
        return view_expression;
    }

    /**
     * 获得更多布局
     *
     * @return
     */
    private View getMoreView()
    {
        if (view_more == null)
        {
            view_more = new LivePrivateChatMoreView(getContext());
            view_more.setSendCoinsEnable(InitActModelDao.query().getOpen_send_coins_module() == 1);
            view_more.setSendDiamondsEnable(InitActModelDao.query().getOpen_send_diamonds_module() == 1);
            view_more.setClickListener(moreClickListener);
        }
        return view_more;
    }

    /**
     * 获得礼物布局
     *
     * @return
     */
    private View getGiftView()
    {
        if (view_gift == null)
        {
            view_gift = new LiveSendGiftView(getContext());
            view_gift.requestData();
            view_gift.setClickListener(giftListener);
        }
        CommonInterface.requestMyUserInfo(null);
        return view_gift;
    }

    private void initTitle()
    {
        title.initRightItem(1);
        title.getItemRight(0).setImageRight(R.drawable.ic_private_chat_title_bar_user);
        title.setLeftImageLeft(R.drawable.ic_arrow_left_white);
        title.setmListener(new SDTitleSimpleListener()
        {

            @Override
            public void onCLickRight_SDTitleSimple(SDTitleItem v, int index)
            {
                Intent intent = new Intent(getActivity(), LiveUserHomeActivity.class);
                intent.putExtra(LiveUserHomeActivity.EXTRA_USER_ID, userId);
                getActivity().startActivity(intent);
            }

            @Override
            public void onCLickMiddle_SDTitleSimple(SDTitleItem v)
            {
            }

            @Override
            public void onCLickLeft_SDTitleSimple(SDTitleItem v)
            {
                if (clickListener != null)
                {
                    clickListener.onClickBack();
                }
            }
        });
    }

    /**
     * 获取本地聊天信息
     */
    private void loadHistoryMessage()
    {
        TIMConversation conversation = IMHelper.getConversationC2C(userId);
        if (conversation == null)
        {
            return;
        }
        final List<MsgModel> listLocal = new ArrayList<>();
        conversation.getLocalMessage(20, lastMsg, new TIMValueCallBack<List<TIMMessage>>()
        {

            @Override
            public void onSuccess(List<TIMMessage> list)
            {
                if (!SDCollectionUtil.isEmpty(list))
                {
                    Collections.reverse(list);
                    lastMsg = list.get(0);

                    for (TIMMessage msg : list)
                    {
                        MsgModel msgModel = new TIMMsgModel(msg);
                        if (msgModel.isPrivateMsg() && msgModel.getStatus() != MsgStatus.HasDeleted)
                        {
                            listLocal.add(msgModel);
                        }
                    }

                    int position = listLocal.size();

                    adapter.insertData(0, listLocal);
                    lv_content.getRefreshableView().scrollToPosition(position - 1);
                }
                lv_content.onRefreshComplete();
            }

            @Override
            public void onError(int arg0, String str)
            {
                lv_content.onRefreshComplete();
            }
        });
    }


    /**
     * 礼物监听
     */
    private LiveSendGiftView.ClickListener giftListener = new LiveSendGiftView.ClickListener()
    {
        @Override
        public void onClickSend(LiveGiftModel model, int is_plus)
        {
            requestSend_prop(model);
        }
    };

    /**
     * 发送礼物
     *
     * @param model
     */
    private void requestSend_prop(final LiveGiftModel model)
    {
        if (model != null)
        {
            CommonInterface.requestSendGiftPrivate(model.getId(), 1, userId, new AppRequestCallback<Deal_send_propActModel>()
            {
                @Override
                protected void onSuccess(SDResponse resp)
                {
                    if (actModel.isOk())
                    {
                        view_gift.sendGiftSuccess(model);
                        sendGift(actModel);
                    }
                }
            });
        }
    }

    /**
     * 表情点击监听
     */
    private LiveExpressionView.ClickListener expressionListener = new LiveExpressionView.ClickListener()
    {
        @Override
        public void onClickExpression(LiveExpressionModel model)
        {
            String key = model.getKey();

            if (MAX_INPUT_LENGTH > 0)
            {
                if (key != null)
                {
                    int length = view_chat_bar.et_content.getText().toString().length();
                    if ((length + key.length()) > MAX_INPUT_LENGTH)
                    {
                        return;
                    }
                }
            }

            view_chat_bar.et_content.insertSpan(new LiveExpressionSpan(getContext(), model.getResId()), key);
        }

        @Override
        public void onClickDelete()
        {
            view_chat_bar.et_content.delete();
        }
    };

    /**
     * 更多里面的内容点击监听
     */
    private LivePrivateChatMoreView.ClickListener moreClickListener = new LivePrivateChatMoreView.ClickListener()
    {
        @Override
        public void onClickGift()
        {
            showExtendGift();
        }

        @Override
        public void onClickPhoto()
        {
            photoHandler.getPhotoFromAlbum();
//            Intent intent = new Intent(getActivity(), SelectPhotoActivity.class);
//            getActivity().startActivity(intent);
        }

        @Override
        public void onClickCamera()
        {
            photoHandler.getPhotoFromCamera();
        }

        @Override
        public void onClickSendCoin()
        {
            long coins = UserModelDao.query().getCoin();
            LiveGameExchangeDialog dialog = new LiveGameExchangeDialog(getActivity(), LiveGameExchangeDialog.TYPE_COIN_SEND, mListener);
            dialog.setCurrency(coins);
            dialog.setToUserId(userId);
            dialog.showCenter();
            CommonInterface.requestMyUserInfo(null);
        }

        @Override
        public void onClickSendDialond()
        {
            long diamonds = UserModelDao.query().getDiamonds();
            LiveGameExchangeDialog dialog = new LiveGameExchangeDialog(getActivity(), LiveGameExchangeDialog.TYPE_DIAMOND_SEND, mListener);
            dialog.setCurrency(diamonds);
            dialog.setToUserId(userId);
            dialog.showCenter();
            CommonInterface.requestMyUserInfo(null);
        }
    };

    /**
     * 触摸监听
     */
    private SDRecordView.RecordViewListener recordViewListener = new RecordViewListener()
    {
        @Override
        public void onUpCancelView()
        {
            onUpCancelView = true;
            view_chat_bar.tv_record.setText("按住说话");
            view_chat_bar.tv_record.setBackgroundResource(R.drawable.layer_white_stroke_corner_item_single);
            SDMediaRecorder.getInstance().stop();
        }

        @Override
        public void onUp()
        {
            onUpCancelView = false;
            view_chat_bar.tv_record.setText("按住说话");
            view_chat_bar.tv_record.setBackgroundResource(R.drawable.layer_white_stroke_corner_item_single);
            SDMediaRecorder.getInstance().stop();
        }

        @Override
        public void onLeaveCancelView()
        {
            view_chat_bar.tv_record.setText("松开结束");
        }

        @Override
        public void onEnterCancelView()
        {
            view_chat_bar.tv_record.setText("松开手指,取消发送");
        }

        @Override
        public boolean onDownRecordView()
        {
            view_chat_bar.tv_record.setText("松开结束");
            view_chat_bar.tv_record.setBackgroundResource(R.drawable.layer_gray_stroke_corner_item_single);
            SDMediaRecorder.getInstance().start(null);

            return true;
        }

        @Override
        public void onCancel()
        {
            SDMediaRecorder.getInstance().stop();
        }
    };

    /**
     * 录音计时监听
     */
    private SDCountDownTimer.SDCountDownTimerListener recordTimerListener = new SDCountDownTimer.SDCountDownTimerListener()
    {
        @Override
        public void onTick(long leftTime)
        {
            view_record.setTextRecordTime(SDDateUtil.formatDuring2mmss(leftTime));
        }

        @Override
        public void onFinish()
        {
            view_record.setTextRecordTime(String.valueOf(0));
            view_record.cancelGesture();
        }
    };

    /**
     * 录音监听
     */
    private SDSimpleMediaRecorderListener recorderListener = new SDSimpleMediaRecorderListener()
    {
        @Override
        public void onStopped(File file, long duration)
        {
            if (file != null)
            {
                if (onUpCancelView)
                {
                    SDFileUtil.deleteFileOrDir(file);
                } else
                {
                    if (duration < 1000)
                    {
                        SDFileUtil.deleteFileOrDir(file);
                        SDToast.showToast("录音时间太短");
                    } else
                    {
                        sendVoiceFile(file, duration);
                    }
                }
            }
        }
    };

    /**
     * 发送语音文件
     *
     * @param file
     */
    private void sendVoiceFile(File file, long duration)
    {
        CustomMsgPrivateVoice msg = new CustomMsgPrivateVoice();
        msg.setDuration(duration);
        msg.setPath(file.getAbsolutePath());

        MsgModel msgModel = msg.parseToMsgModel();
        adapter.appendData(msgModel);

        sendMsg(msgModel, false);
    }

    /**
     * 发送图片文件
     *
     * @param file
     */
    private void sendImageFile(File file)
    {
        CustomMsgPrivateImage msg = new CustomMsgPrivateImage();
        msg.setPath(file.getAbsolutePath());

        MsgModel msgModel = msg.parseToMsgModel();
        adapter.appendData(msgModel);

        sendMsg(msgModel, false);
    }

    /**
     * 发送礼物
     *
     * @param model
     */
    private void sendGift(Deal_send_propActModel model)
    {
        CustomMsgPrivateGift msg = new CustomMsgPrivateGift();

        msg.fillData(model);

        MsgModel msgModel = msg.parseToMsgModel();
        adapter.appendData(msgModel);

        sendMsg(msgModel, false);
    }

    /**
     * 锁定聊天内容高度
     */
    public void lockContent()
    {
        if (lockHeightEnable)
        {
            if (lockHeight > 0)
            {
                sizeLocker.lockHeight(lockHeight);
                SDViewUtil.setViewHeightMatchParent(fl_extend);
            }
        }
    }

    /**
     * 解锁聊天内容高度
     */
    public void unLockContent()
    {
        if (lockHeightEnable)
        {
            if (lockHeight > 0)
            {
                sizeLocker.unlockHeight();
                SDViewUtil.setViewHeightWrapContent(fl_extend);
            }
        }
    }

    /**
     * 底部聊天栏点击监听
     */
    private LivePrivateChatBarView.ClickListener chatbarClickListener = new LivePrivateChatBarView.ClickListener()
    {
        @Override
        public void onClickKeyboard()
        {
            lockContent();
            hideExtend();
            lv_content.getRefreshableView().scrollToEndDelayed(SCROLL_DELAY);
        }

        @Override
        public void onClickVoice()
        {
            unLockContent();
            hideExtend();
        }

        @Override
        public void onClickExpressionOff()
        {
            lockContent();
            showExtendExpression();
            shouldUnlockContentWhenKeyboardHide = false;
            view_chat_bar.hideInputMethod();
            lv_content.getRefreshableView().scrollToEndDelayed(SCROLL_DELAY);
        }

        @Override
        public void onClickExpressionOn()
        {
            hideExtend();
        }

        @Override
        public void onClickMore()
        {
            lockContent();
            showExtendMore();
            shouldUnlockContentWhenKeyboardHide = false;
            view_chat_bar.hideInputMethod();
            lv_content.getRefreshableView().scrollToEndDelayed(SCROLL_DELAY);
        }

        @Override
        public void onClickSend(String content)
        {
            sendText(content);
        }

        @Override
        public boolean onTouchEditText()
        {
            lockContent();
            hideExtend();
            lv_content.getRefreshableView().scrollToEndDelayed(SCROLL_DELAY);
            return false;
        }
    };

    /**
     * 显示底部表情布局
     */
    private void showExtendExpression()
    {
        replaceExtend(getExpressionView());
    }

    /**
     * 显示底部礼物布局
     */
    private void showExtendGift()
    {
        replaceExtend(getGiftView());
    }

    /**
     * 显示底部更多布局
     */
    private void showExtendMore()
    {
        replaceExtend(getMoreView());
    }

    private void replaceExtend(View view)
    {
        if (lockHeightEnable)
        {
            if (view instanceof ILivePrivateChatMoreView)
            {
                ILivePrivateChatMoreView moreView = (ILivePrivateChatMoreView) view;
                if (sizeLocker.hasLockHeight())
                {
                    moreView.setContentMatchParent();
                } else
                {
                    moreView.setContentWrapContent();
                }
            }
        }

        SDViewUtil.replaceView(fl_extend, view);
        showExtend();
    }

    /**
     * 显示底部布局
     */
    private void showExtend()
    {
        SDViewUtil.show(fl_extend);

        if (extendVisibilityChangeListener != null)
        {
            extendVisibilityChangeListener.onShow(fl_extend);
        }
    }

    /**
     * 隐藏底部布局
     */
    private void hideExtend()
    {
        SDViewUtil.hide(fl_extend);

        if (extendVisibilityChangeListener != null)
        {
            extendVisibilityChangeListener.onHide(fl_extend);
        }
    }

    /**
     * 发送文字消息
     *
     * @param content
     */
    private void sendText(String content)
    {
        if (TextUtils.isEmpty(content))
        {
            SDToast.showToast("请输入内容");
            return;
        }
        CustomMsgPrivateText msg = new CustomMsgPrivateText();
        msg.setText(content);

        MsgModel msgModel = msg.parseToMsgModel();
        adapter.appendData(msgModel);

        sendMsg(msgModel, false);
        view_chat_bar.et_content.setText("");
    }

    private void dealHasPrivateChat()
    {
        if (AppRuntimeWorker.hasPrivateChat())
        {
            SDViewUtil.show(view_chat_bar);
        } else
        {
            SDViewUtil.hide(view_chat_bar);
        }
    }

    private TIMMessage sendMsg(final MsgModel model, boolean isResend)
    {
        final int index = adapter.indexOf(model);

        TIMMessage timMessageSending = IMHelper.sendMsgC2C(userId, model.getCustomMsg(), new TIMValueCallBack<TIMMessage>()
        {

            @Override
            public void onSuccess(TIMMessage timMessage)
            {
                if (lastMsg == null)
                {
                    lastMsg = timMessage;
                }

                if (model.getStatus() == MsgStatus.SendFail)
                {
                    model.remove();
                }

                model.setTimMessage(timMessage);

                adapter.updateData(index, model);
            }

            @Override
            public void onError(int arg0, String arg1)
            {
                adapter.updateData(index);
            }
        });

        model.setTimMessage(timMessageSending);
        adapter.updateData(index, model);

        if (!isResend)
        {
            lv_content.getRefreshableView().scrollToEndDelayed(SCROLL_DELAY);
        }

        return timMessageSending;
    }

    /**
     * 接收新消息
     *
     * @param event
     */
    public void onEventMainThread(EImOnNewMessages event)
    {
        try
        {
            // 判断新消息来源是否是当前用户
            if (event.msg.getConversationPeer().equals(userId))
            {
                if (event.msg.isPrivateMsg())
                {
                    adapter.appendData(event.msg);
                    lv_content.getRefreshableView().scrollToEndDelayed(SCROLL_DELAY);
                }
            }
        } catch (Exception e)
        {
            SDToast.showToast(e.toString());
        }
    }

    /**
     * 获取用户信息
     *
     * @param userId 用户id
     */
    private void requestUserInfo(String userId)
    {
        CommonInterface.requestUserInfo(null, userId, 0, new AppRequestCallback<App_userinfoActModel>()
        {

            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (actModel.isOk())
                {
                    user = actModel.getUser();
                    title.setMiddleTextTop(user.getNick_name());
                }
            }
        });
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
        {
            boolean isAbove = ev.getRawY() < SDViewUtil.getViewRect(view_chat_bar).top;
            if (isAbove)
            {
                view_chat_bar.showNormalMode();
                hideExtend();
                unLockContent();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onPullDownToRefresh(ScrollListView view)
    {
        loadHistoryMessage();
    }

    /**
     * 聊天列表点击监听
     */
    private PrivateChatViewHolder.ClickListener adapterClickListener = new PrivateChatViewHolder.ClickListener()
    {

        @Override
        public void onClickResend(MsgModel model)
        {
            sendMsg(model, true);
        }

        @Override
        public void onClickHeadImage(MsgModel model)
        {
            Intent intent = new Intent(getActivity(), LiveUserHomeActivity.class);
            intent.putExtra(LiveUserHomeActivity.EXTRA_USER_ID, model.getCustomMsg().getSender().getUser_id());
            getActivity().startActivity(intent);
        }

        @Override
        public void onLongClick(final MsgModel model, View v)
        {
            final PrivateChatLongClickMenuDialog dialog = new PrivateChatLongClickMenuDialog(getActivity());
            switch (model.getCustomMsgType())
            {
                case LiveConstant.CustomMsgType.MSG_PRIVATE_TEXT:
                    dialog.setItems("复制");
                    dialog.setItemClickListener(new SDAdapter.ItemClickListener<String>()
                    {
                        @Override
                        public void onClick(int position, String item, View view)
                        {
                            SDOtherUtil.copyText(model.getCustomMsgPrivateText().getText());
                            SDToast.showToast("已复制");
                            dialog.dismiss();
                        }
                    });
                    SDViewUtil.showDialogTopCenter(dialog, v, 10, 0);
                    break;

                default:
                    break;
            }


        }
    };

    public void onEventMainThread(ESDMediaPlayerStateChanged event)
    {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEventMainThread(EOnActivityResult event)
    {
        super.onEventMainThread(event);
        if (event.activity == getActivity())
        {
            photoHandler.onActivityResult(event.requestCode, event.resultCode, event.data);
        }
    }

    @Override
    protected void onDetachedFromWindow()
    {
        SDMediaRecorder.getInstance().stop();
        SDMediaRecorder.getInstance().unregisterListener(recorderListener);
        SDMediaRecorder.getInstance().unregisterTimerListener(recordTimerListener);
        SDMediaPlayer.getInstance().reset();
        IMHelper.setSingleC2CReadMessage(userId);
        imageFileCompresser.deleteCompressedImageFile();
        LiveInformation.getInstance().setCurrentChatPeer("");
        super.onDetachedFromWindow();
    }


    public interface ClickListener
    {
        void onClickBack();
    }

    public interface ExtendVisibilityChangeListener
    {
        void onShow(View view);

        void onHide(View view);
    }

    /**
     * 相册选择图片
     *
     * @param event
     */
    public void onEventMainThread(ESelectImage event)
    {
        List<ImageModel> listImage = event.listImage;
        if (!listImage.isEmpty())
        {
            ImageModel model = listImage.get(0);
            dealImage(new File(model.getUri()));
        }
    }

    private LiveGameExchangeDialog.OnSuccessListener mListener = new LiveGameExchangeDialog.OnSuccessListener()
    {
        @Override
        public void onExchangeSuccess(long diamonds, long coins)
        {

        }

        @Override
        public void onSendCurrencySuccess(Deal_send_propActModel model)
        {
            sendGift(model);

        }
    };
}
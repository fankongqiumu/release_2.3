package com.fanwe.live.activity.room;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import com.fanwe.games.GameBusiness;
import com.fanwe.games.dialog.GamesBankerDialog;
import com.fanwe.games.dialog.GamesBankerListDialog;
import com.fanwe.games.model.App_requestGameIncomeActModel;
import com.fanwe.games.model.App_startGameActModel;
import com.fanwe.games.model.PluginModel;
import com.fanwe.games.model.custommsg.GameBankerMsg;
import com.fanwe.games.model.custommsg.GameMsgModel;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.model.BaseActModel;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.dialog.SDDialogConfirm;
import com.fanwe.library.dialog.SDDialogCustom;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.event.EUpdateUserInfo;
import com.fanwe.live.model.App_get_videoActModel;
import com.fanwe.live.model.custommsg.CustomMsgEndVideo;
import com.fanwe.live.model.custommsg.MsgModel;

/**
 * Created by Administrator on 2016/12/21.
 */

public class LiveLayoutGameActivity extends LiveLayoutActivity implements GameBusiness.GameBusinessListener, GameBusiness.IGameCtrlViewClickListener, GamesBankerListDialog.BankerSubmitListener {

    private GameBusiness gameBusiness;

    @Override
    protected void init(Bundle savedInstanceState)
    {
        super.init(savedInstanceState);
        gameBusiness = new GameBusiness(this);
        gameBusiness.setBusinessListener(this);
    }

    /**
     * 获得游戏基础业务类
     *
     * @return
     */
    public GameBusiness getGameBusiness()
    {
        return gameBusiness;
    }

    public void onEventMainThread(EUpdateUserInfo event)
    {
        gameBusiness.refreshCoins();
    }

    @Override
    public void onLiveRequestRoomInfoSuccess(App_get_videoActModel actModel)
    {
        super.onLiveRequestRoomInfoSuccess(actModel);
        gameBusiness.onSuccessRequestRoomInfo(actModel);
    }

    @Override
    protected void onSuccessJoinGroup(String groupId)
    {
        super.onSuccessJoinGroup(groupId);
        gameBusiness.requestGameInfo(getRoomId());
    }

    @Override
    public void onMsgGame(MsgModel msg)
    {
        super.onMsgGame(msg);
        gameBusiness.onLiveMsgGame(msg);
    }

    @Override
    protected void onClickCreaterPluginGame(PluginModel model)
    {
        super.onClickCreaterPluginGame(model);
        gameBusiness.selectGame(model);
    }

    @Override
    public void onClickGameCtrlStart(View view)
    {
        gameBusiness.requestStartGame();
        gameBusiness.requestGameInfoDelay();
    }

    @Override
    public void onClickGameCtrlClose(View view)
    {
        SDDialogConfirm dialog = new SDDialogConfirm(this);
        dialog.setTextContent("确定要关闭游戏？")
                .setmListener(new SDDialogCustom.SDDialogCustomListener()
                {
                    @Override
                    public void onClickCancel(View v, SDDialogCustom dialog)
                    {

                    }

                    @Override
                    public void onClickConfirm(View v, SDDialogCustom dialog)
                    {
                        gameBusiness.requestStopGame();
                    }

                    @Override
                    public void onDismiss(SDDialogCustom dialog)
                    {

                    }
                }).show();
    }

    @Override
    public void onClickGameCtrlOpenBanker(View view) {
        gameBusiness.requestOpenGameBanker();
    }

    @Override
    public void onClickGameCtrlStopBanker(View view) {
        SDDialogConfirm dialog = new SDDialogConfirm(this);
        dialog.setTextContent("确定要移除该庄家？")
                .setTextGravity(Gravity.CENTER)
                .setTextConfirm("移除")
                .setmListener(new SDDialogCustom.SDDialogCustomListener()
                {
                    @Override
                    public void onClickCancel(View v, SDDialogCustom dialog)
                    {

                    }

                    @Override
                    public void onClickConfirm(View v, SDDialogCustom dialog)
                    {
                        gameBusiness.requestStopGameBanker();
                    }

                    @Override
                    public void onDismiss(SDDialogCustom dialog)
                    {

                    }
                }).show();
    }

    @Override
    public void onClickGameCtrlBankerList(View view) {
        GamesBankerListDialog dialog = new GamesBankerListDialog(this, this);
        dialog.showCenter();
    }

    protected void onClickGameCtrlApplyBanker(long principal) {
        GamesBankerDialog dialog = new GamesBankerDialog(this, new GamesBankerDialog.BankerSubmitListener() {
            @Override
            public void onClickSubmit(long coins) {
                gameBusiness.requestApplyBanker(getRoomId(), coins);
            }
        });
        dialog.show(principal, gameBusiness.getCoins());

    }

    @Override
    public void onGameGameSelected(PluginModel model)
    {
        LogUtil.i("onGameGameSelected");
        gameBusiness.requestStartGame();
    }

    @Override
    public void onGameInitPanel(GameMsgModel msg)
    {
        LogUtil.i("onGameInitPanel");
    }

    @Override
    public void onGameMsg(GameMsgModel msg)
    {
        LogUtil.i("onGameMsg");
    }

    @Override
    public void onBankerMsg(GameBankerMsg msg) {

    }

    @Override
    public void onGameLogIdChange(int gameLogId)
    {
        LogUtil.i("onGameLogIdChange:" + gameLogId);
    }

    @Override
    public void onGameMsgStopGame()
    {
        LogUtil.i("onGameMsgStopGame");
    }

//    @Override
//    public void onGameRequestGameInfoSuccess(App_getGamesActModel actModel)
//    {
//        LogUtil.i("onGameRequestGameInfoSuccess:" + actModel);
//    }

    @Override
    public void onGameRequestGameIncomeSuccess(App_requestGameIncomeActModel actModel)
    {
        LogUtil.i("onGameRequestGameIncomeSuccess:" + actModel);
    }

    @Override
    public void onGameRequestStartGameSuccess(App_startGameActModel actModel)
    {
        LogUtil.i("onGameRequestStartGameSuccess:" + actModel);
    }

    @Override
    public void onGameRequestStopGameSuccess(BaseActModel actModel)
    {
        LogUtil.i("onGameRequestStopGameSuccess:" + actModel);
    }

    @Override
    public void onGameCtrlShowStart(boolean show, int gameId)
    {
        LogUtil.i("onGameCtrlShowStart:" + show);
    }

    @Override
    public void onGameCtrlShowClose(boolean show, int gameId)
    {
        LogUtil.i("onGameCtrlShowClose:" + show);
    }

    @Override
    public void onGameCtrlShowWaiting(boolean show, int gameId)
    {
        LogUtil.i("onGameCtrlShowWaiting:" + show);
    }

    @Override
    public void onGameCtrlShowBankerBtn(int status) {

    }

    @Override
    public void onGameUpdateCoins(long coins)
    {
        LogUtil.i("onGameUpdateCoins:" + coins);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        gameBusiness.onDestroy();
    }

    @Override
    public void onClickChoose(SDDialogCustom dialog, String bankerLogId) {
        CommonInterface.requestChooseBanker(bankerLogId, new AppRequestCallback<BaseActModel>() {
            @Override
            protected void onSuccess(SDResponse sdResponse) {

            }

            @Override
            protected void onError(SDResponse resp) {
                super.onError(resp);
            }
        });
        dialog.dismiss();
    }

    @Override
    public void onMsgEndVideo(CustomMsgEndVideo msg) {
        super.onMsgEndVideo(msg);

    }
}

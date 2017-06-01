package com.fanwe.live.activity.room;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.fanwe.games.GameBankerBusiness;
import com.fanwe.games.GoldFlowerGameBusiness;
import com.fanwe.games.model.App_GamesLogActModel;
import com.fanwe.games.model.App_requestGameIncomeActModel;
import com.fanwe.games.model.GameBankerModel;
import com.fanwe.games.model.custommsg.GameBankerMsg;
import com.fanwe.games.model.custommsg.GameMsgModel;
import com.fanwe.library.dialog.SDDialogConfirm;
import com.fanwe.library.dialog.SDDialogCustom;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.utils.SDToast;
import com.fanwe.live.R;
import com.fanwe.live.appview.room.RoomGameBankerView;
import com.fanwe.live.model.custommsg.CustomMsgEndVideo;
import com.librarygames.GameConstant;
import com.librarygames.model.PokerGoldFlowerModel;
import com.librarygames.view.PokerBasePanelView;
import com.librarygames.view.PokerBullPanelView;
import com.librarygames.view.PokerGoldFlowerPanelView;

import java.util.List;

/**
 * 游戏扩展
 */
public class LiveLayoutGameExtendActivity extends LiveLayoutGameActivity implements PokerBasePanelView.PokerPanelListener,
        GoldFlowerGameBusiness.GamesBusinessListener,GameBankerBusiness.BankerBusinessListener {

    protected PokerBasePanelView mGamePanelView;

    protected RoomGameBankerView mGameBankerView;

    private GoldFlowerGameBusiness goldFlowerGameBusiness;

    private GameBankerBusiness gameBankerBusiness;

    @Override
    protected void init(Bundle savedInstanceState)
    {
        super.init(savedInstanceState);

        goldFlowerGameBusiness = new GoldFlowerGameBusiness(getGameBusiness());
        gameBankerBusiness = new GameBankerBusiness(getGameBusiness());
        goldFlowerGameBusiness.setGameBusinessListener(this);
        gameBankerBusiness.setGameBusinessListener(this);
    }

    @Override
    public void onGameMsg(GameMsgModel msg)
    {
        super.onGameMsg(msg);
        goldFlowerGameBusiness.onGameMsg(msg);
        if(msg.getGame_action() != 2) {
            gameBankerBusiness.onBankerMsg(msg);
        }
    }

    @Override
    public void onBankerMsg(GameBankerMsg msg) {
        super.onBankerMsg(msg);
        gameBankerBusiness.onBankerMsg(msg);
    }

    @Override
    public void onGameInitPanel(GameMsgModel msg)
    {
        super.onGameInitPanel(msg);
        if (mGamePanelView != null && mGamePanelView.getmGameId() == msg.getGame_id())
        {
            return;
        }
        switch (msg.getGame_id())
        {
            case 1://扎金花
                mGamePanelView = new PokerGoldFlowerPanelView(this, isCreater(), this);
                break;
            case 2://斗牛
                mGamePanelView = new PokerBullPanelView(this, isCreater(), this);
                break;
            default:
                break;
        }
        mGamePanelView.setGameStatus(GameConstant.START);
        mGamePanelView.setBetsCoin(msg.getBet_option(), getGameBusiness().getCoins());
        mGamePanelView.setBetsArea(msg.getOption());
        mGamePanelView.updateAccount(getGameBusiness().getCoins());
        mGamePanelView.setmGameId(msg.getGame_id());
        mGamePanelView.openAutoFunction(getResources().getBoolean(R.bool.open_auto_deal));
        replacePluginExtend(mGamePanelView);
        if (isSendMsgViewVisible() || isSendGiftViewVisible())
        {
            hideGamePanelView();
        }
    }

    @Override
    public void onGameLogIdChange(int game_log_id)
    {
        super.onGameLogIdChange(game_log_id);
//        mGamePanelView.setmGameLogId(game_log_id);
    }

    @Override
    public void onGameUpdateCoins(long coins)
    {
        super.onGameUpdateCoins(coins);
        if (mGamePanelView != null)
            mGamePanelView.updateAccount(coins);
    }

    @Override
    public void onGameMsgStopGame()
    {
        super.onGameMsgStopGame();
        if(mGamePanelView == null)
            return ;
        getGameBusiness().requestGameIncome(mGamePanelView.getmGameLogId());
        removeGamePanel();
//        updatePluginExtendState();
    }

    @Override
    public void onClickBetArea(int game_log_id, int index)
    {
        int betGold = mGamePanelView.getSelBetGold();
        if (betGold <= 0)
        {
            return;
        }
        if (!getGameBusiness().canCoinsPay(betGold))
        {
            SDToast.showToast("余额不足，请先充值");
            return;
        }
        goldFlowerGameBusiness.requestDoBet(game_log_id, betGold, index);
    }

    @Override
    public void onClickBetRecharge()
    {
        showRechargeDialog();
    }

    @Override
    public void onClickHistory(String game_id)
    {
        goldFlowerGameBusiness.requestGameLog(game_id, getCreaterId());
    }

    @Override
    public void onClickAutoDeal()
    {
        boolean isAutoDeal = getGameBusiness().isAutoDeal();
        if(isAutoDeal) {
            SDToast.showToast("自动发牌取消");
            getGameBusiness().setAutoDeal(false);
            mGamePanelView.setAutoUi(false);
        } else {
            SDDialogConfirm dialog = new SDDialogConfirm(this);
            dialog.setTextGravity(Gravity.CENTER)
                    .setTextContent("是否开启自动发牌")
                    .setTextConfirm("开启")
                    .setTextTitle("自动发牌")
                    .setmListener(new SDDialogCustom.SDDialogCustomListener()
                    {
                        @Override
                        public void onClickCancel(View v, SDDialogCustom dialog)
                        {

                        }

                        @Override
                        public void onClickConfirm(View v, SDDialogCustom dialog)
                        {
                            getGameBusiness().setAutoDeal(true);
                            mGamePanelView.setAutoUi(true);
                        }

                        @Override
                        public void onDismiss(SDDialogCustom dialog)
                        {

                        }
                    }).showCenter();
        }
    }

    @Override
    public void onPokerSettle(int game_log_id, boolean isCreater)
    {
        getGameBusiness().setInGameRound(false);
        getGameBusiness().requestGameIncome(game_log_id);
    }

    @Override
    public void onClockFinish() {
        Log.i("poker","倒计时结束");
        getGameBusiness().requestGameInfoDelay();
        Log.i("poker","主动调用查询游戏信息接口");
    }

    @Override
    public void onGameRequestGameIncomeSuccess(App_requestGameIncomeActModel actModel)
    {
        super.onGameRequestGameIncomeSuccess(actModel);
        //游戏轮数收益
        int coin = actModel.getGain();
        if (coin > 0)
        {
            if (mGamePanelView != null)
            {
                String unit ;
                if(getGameBusiness().isUseGameCoin()) {
                    unit = "游戏币";
                } else
                    unit = "钻石";
                mGamePanelView.showWinnerDialog(coin, unit);
            }
        }
    }

    @Override
    public void onGameGlodFlowerMsgBegin(GameMsgModel msg, boolean isPush)
    {
        LogUtil.i("onGameGlodFlowerMsgBegin");
        mGamePanelView.cleanDatas();
        if (isPluginExtendVisible() && isPush)
        {
            mGamePanelView.startGame(msg.getTime(), true);
        } else
            mGamePanelView.startGame(msg.getTime(), false);
    }

    @Override
    public void onGameGlodFlowerUpdateBets(boolean isUpdateAll, List<Integer> listBet, List<Integer> listUserBet)
    {
        LogUtil.i("onGameGlodFlowerUpdateBets:" + listBet + "," + listUserBet);
        if(mGamePanelView == null) {
            return;
        }
        if (isUpdateAll)
        {
            mGamePanelView.updateBet(listBet, listUserBet);
        } else
        {
            mGamePanelView.updateBet(listBet);
        }
    }

    @Override
    public void onGameGlodFlowerUpdatePokerDatas(List<PokerGoldFlowerModel> listPokers, int indexWiner, boolean showDelay)
    {
        LogUtil.i("onGameGlodFlowerUpdatePokerDatas:" + listPokers + "," + indexWiner + "," + showDelay);
        mGamePanelView.updatePokerDatas(listPokers, indexWiner, showDelay);
    }

    @Override
    public boolean onGameGlodFlowerStatus(int gameLogId, int status)
    {
        LogUtil.i("onGameGlodFlowerStatus:" + status);
        return mGamePanelView.isMsgLegal(gameLogId, status);
    }

    @Override
    public void onGameGlodFlowerRequestGameLogSuccess(App_GamesLogActModel actModel)
    {
        mGamePanelView.showHistoryDialog(actModel.getList());
    }

    @Override
    protected void onShowSendMsgView(View view)
    {
        super.onShowSendMsgView(view);
        hideGamePanelView();
    }

    @Override
    protected void onHideSendMsgView(View view)
    {
        super.onHideSendMsgView(view);
        showGamePanelViewDelay();
    }

    @Override
    protected void onShowSendGiftView(View view)
    {
        super.onShowSendGiftView(view);
        hideGamePanelView();
    }

    @Override
    protected void onHideSendGiftView(View view)
    {
        super.onHideSendGiftView(view);
        showGamePanelViewDelay();
    }

    @Override
    public void onMsgOpenBanker(long principal) {

    }

    @Override
    public void onMsgApplyBanker(GameBankerMsg msg) {

    }

    @Override
    public void onMsgChooseBanker(GameBankerModel banker, String userId) {
        //展示庄家信息
        if (mGameBankerView == null) {
            mGameBankerView = new RoomGameBankerView(this);
        }

        mGameBankerView.setBnaker(banker);
        replaceBankerView(mGameBankerView);
        if(TextUtils.equals(banker.getBanker_id(), userId)) {
            mGamePanelView.setCreaterMode(true);
        }
        onGameCtrlShowBankerBtn(3);
    }

    @Override
    public void onMsgRemoveBanker(GameBankerMsg msg, String userId) {
        removeBankerView();
    }

    protected void showGamePanelViewDelay()
    {
        getGameBusiness().refreshCoins();
        showPluginExtend(200);
    }

    protected void hideGamePanelView()
    {
        hidePluginExtend();
    }

    private void removeGamePanel() {
        if(mGamePanelView != null) {
            removeView(mGamePanelView);
            updatePluginExtendState();
            mGamePanelView = null;
        }

    }

    private void removeBankerView() {
        if(mGameBankerView != null) {
            removeView(mGameBankerView);
            mGameBankerView = null;
        }
        onGameCtrlShowBankerBtn(1);
    }

    @Override
    public void onChangeRoomActionComplete()
    {
        super.onChangeRoomActionComplete();
        removeGamePanel();
        onMsgRemoveBanker(null, null);
        getGameBusiness().onStopGame();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        onGameMsgStopGame();
    }

    @Override
    public void onGameCtrlShowBankerBtn(int status) {

    }

    @Override
    public void onMsgEndVideo(CustomMsgEndVideo msg) {
        super.onMsgEndVideo(msg);
        if(mGamePanelView == null)
            return ;
        removeGamePanel();
    }
}

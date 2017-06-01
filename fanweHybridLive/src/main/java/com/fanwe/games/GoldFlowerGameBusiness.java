package com.fanwe.games;

import android.util.Log;

import com.fanwe.games.model.App_GamesLogActModel;
import com.fanwe.games.model.App_betGamesActModel;
import com.fanwe.games.model.App_getGamesActModel;
import com.fanwe.games.model.custommsg.GameMsgModel;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.live.common.CommonInterface;
import com.librarygames.GameConstant;
import com.librarygames.model.PokerGoldFlowerModel;

import java.util.List;

/**
 * Created by shibx on 2016/12/2.
 */

public class GoldFlowerGameBusiness
{
    private GameBusiness gameBusiness;
    private GamesBusinessListener mGameBusinessListener;

    public GoldFlowerGameBusiness(GameBusiness gameBusiness)
    {
        this.gameBusiness = gameBusiness;
    }

    public void setGameBusinessListener(GamesBusinessListener gameBusinessListener)
    {
        this.mGameBusinessListener = gameBusinessListener;
    }

    /**
     * 处理推送消息（外部需要调用）
     *
     * @param msg
     */
    public void onGameMsg(GameMsgModel msg)
    {
        Log.i("poker_pushMsg", "game_log_id:"+msg.getGame_log_id()+"--game_actionid:"+msg.getGame_action()+"--game_status:"+msg.getGame_status());
        switch (msg.getGame_id())
        {
            //以游戏id作为区分处理消息
            case 1:
                onGlodFlowerMsg(msg);
                break;
            case 2:
                onGlodFlowerMsg(msg);
                break;
            default:
                break;
        }
    }

    /**
     * 【开始：1；下注：2；停止：3；结算：4；翻牌：5】
     *
     * @param msg
     */
    private void onGlodFlowerMsg(GameMsgModel msg)
    {
        switch (msg.getGame_action())
        {
            case 1:
                onGameBegin(msg, true);
                break;
            case 2:
                onGameBet(msg);
                break;
            case 4:
                onGameSettlement(msg, true);
                break;
            case 6:
                if (msg.getGame_status() == 1)
                {
                    gameBusiness.setInGameRound(true);
                    onGameBegin(msg, false);
                } else
                {
                    gameBusiness.setInGameRound(false);
                    onGameSettlement(msg, false);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 玩家下注
     *
     * @param game_log_id 游戏轮数
     * @param coins       下注金额
     * @param betType     下注选项：1、2、3
     */
    public void requestDoBet(int game_log_id, final int coins, int betType)
    {
        CommonInterface.requestDoBet(game_log_id, coins, betType, new AppRequestCallback<App_betGamesActModel>()
        {
            @Override
            protected void onSuccess(SDResponse sdResponse)
            {
                if (actModel.getStatus() == 1)
                {
                    mGameBusinessListener.onGameGlodFlowerUpdateBets(true, actModel.getData().getGame_data().getBet(), actModel.getData().getGame_data().getUser_bet());
                    gameBusiness.updateCoins(actModel.getData().getAccount_diamonds(), actModel.getData().getCoin());
                }
            }

            @Override
            protected void onError(SDResponse resp)
            {
                super.onError(resp);
            }
        });
    }

    /**
     * 请求游戏记录接口
     *
     * @param gameId    游戏id
     * @param createrId 主播id
     */
    public void requestGameLog(String gameId, String createrId)
    {
        CommonInterface.requestGamesLog(gameId, createrId, new AppRequestCallback<App_GamesLogActModel>()
        {
            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (actModel.isOk())
                {
                    mGameBusinessListener.onGameGlodFlowerRequestGameLogSuccess(actModel);
                }
            }
        });
    }

    /**
     * 处理请求游戏信息回调（外部需要调用）
     *
     * @param actModel
     */
    public void onRequestGameInfoSuccess(App_getGamesActModel actModel)
    {
        if (actModel.getData().getGame_status() == 1)
        {
            onGameBegin(actModel.getData(), false);
            gameBusiness.setInGameRound(true);
        } else
        {
            onGameSettlement(actModel.getData(), false);
            gameBusiness.setInGameRound(false);
        }
    }

    /**
     * 游戏开始
     *
     * @param msg
     * @param isPush
     */
    private void onGameBegin(GameMsgModel msg, boolean isPush)
    {
        if(!mGameBusinessListener.onGameGlodFlowerStatus(msg.getGame_log_id(), GameConstant.BETABLE)) {
            Log.i("poker_onGameBegin","游戏信息不合理");
            return;
        }
        gameBusiness.cancelDelayQuery();
        mGameBusinessListener.onGameGlodFlowerMsgBegin(msg, isPush);
        if (!isPush)
        {
            mGameBusinessListener.onGameGlodFlowerUpdateBets(true, msg.getGame_data().getBet(), msg.getGame_data().getUser_bet());
        }

    }

    /**
     * 游戏下注
     *
     * @param msg
     */
    private void onGameBet(GameMsgModel msg)
    {
        mGameBusinessListener.onGameGlodFlowerUpdateBets(false, msg.getGame_data().getBet(), msg.getGame_data().getUser_bet());
    }

    /**
     * 游戏结算
     *
     * @param msg
     * @param isPush
     */
    private void onGameSettlement(GameMsgModel msg, boolean isPush)
    {
        if(gameBusiness.isAutoDeal()) {
            gameBusiness.requestStartGameDelay();
        }

        if(!mGameBusinessListener.onGameGlodFlowerStatus(msg.getGame_log_id(), GameConstant.SETTLEMENT)) {
            Log.i("poker_onGameSettlement","游戏信息不合理");
            return;
        }
        gameBusiness.cancelDelayQuery();
        mGameBusinessListener.onGameGlodFlowerUpdatePokerDatas(msg.getGame_data().getListPokers(), msg.getGame_data().getWin(), isPush);
        mGameBusinessListener.onGameGlodFlowerUpdateBets(!isPush, msg.getGame_data().getBet(), msg.getGame_data().getUser_bet());
    }

    public interface GamesBusinessListener
    {
        void onGameGlodFlowerMsgBegin(GameMsgModel msg, boolean isPush);

        /**
         * 更新投注信息
         *
         * @param isUpdateAll 是否更新全部(false 只更新总投注，用于下注推送和结算推送，无法推送各自投注数量)
         * @param listBet     总投注
         * @param listUserBet 用户投注
         */
        void onGameGlodFlowerUpdateBets(boolean isUpdateAll, List<Integer> listBet, List<Integer> listUserBet);

        /**
         * 更新扑克牌
         *
         * @param listPokers
         * @param indexWiner
         * @param showDelay
         */
        void onGameGlodFlowerUpdatePokerDatas(List<PokerGoldFlowerModel> listPokers, int indexWiner, boolean showDelay);

        /**
         * 状态改变
         *
         * @param status
         */
        boolean onGameGlodFlowerStatus(int gameLogId, int status);

        /**
         * 请求游戏记录接口成功回调
         *
         * @param actModel
         */
        void onGameGlodFlowerRequestGameLogSuccess(App_GamesLogActModel actModel);
    }
}

package com.fanwe.games;

import android.util.Log;
import android.view.View;

import com.fanwe.games.model.App_banker_applyActModel;
import com.fanwe.games.model.App_getGamesActModel;
import com.fanwe.games.model.App_requestGameIncomeActModel;
import com.fanwe.games.model.App_startGameActModel;
import com.fanwe.games.model.PluginModel;
import com.fanwe.games.model.custommsg.GameBankerMsg;
import com.fanwe.games.model.custommsg.GameMsgModel;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.model.BaseActModel;
import com.fanwe.library.adapter.http.handler.SDRequestHandler;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.common.SDHandlerManager;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.utils.SDTypeParseUtil;
import com.fanwe.live.LiveConstant;
import com.fanwe.live.activity.info.LiveInfo;
import com.fanwe.live.common.AppRuntimeWorker;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.dao.UserModelDao;
import com.fanwe.live.model.App_get_videoActModel;
import com.fanwe.live.model.UserModel;
import com.fanwe.live.model.custommsg.MsgModel;

/**
 * 游戏基础业务类
 */
public class GameBusiness
{

    private LiveInfo liveInfo;
    /**
     * 当前游戏id
     */
    private int currentGameId;
    /**
     * 游戏轮数id
     */
    private int gameLogId;
    /**
     * 游戏是否已经开始
     */
    private boolean isGameStarted;
    /**
     * 是否正在游戏轮数中
     */
    private boolean isInGameRound;
    private GameBusinessListener businessListener;

    private SDRequestHandler requestStartGameHandler;
    private SDRequestHandler requestGameInfoHandler;
    private SDRequestHandler requestUserCoinsHandler;
    private SDRequestHandler requestStopGameHandler;

    private TimeoutQueryRunnable mRunnableQuery;

    private AutoDealRunnable mRunnableAutoDeal;

    private static final long TIMEOUT_QUERY = 5000L;

    private boolean useGameCoin;

    private boolean isAutoDeal = false;

    private static final long DEFAULT_AUTO_DEAL_TIME = 7000L;

    public GameBusiness(LiveInfo liveInfo)
    {
        this.liveInfo = liveInfo;
        useGameCoin = AppRuntimeWorker.useGameCoin();
    }

    public boolean isAutoDeal()
    {
        return isAutoDeal;
    }

    public void setAutoDeal(boolean autoDeal)
    {
        isAutoDeal = autoDeal;
        if(autoDeal && ! isInGameRound()) {
            Log.d("自动发牌", "不在牌局周期内，立即发牌");
            requestStartGame();
        } else {
            cancelAutoDeal();
        }
    }

    public void setBusinessListener(GameBusinessListener businessListener)
    {
        this.businessListener = businessListener;
    }

    public LiveInfo getLiveInfo()
    {
        return liveInfo;
    }

    public boolean isInGameRound()
    {
        return isInGameRound;
    }

    public boolean isGameStarted()
    {
        return isGameStarted;
    }

    public int getCurrentGameId()
    {
        return currentGameId;
    }

    public int getGameLogId()
    {
        return gameLogId;
    }

    public boolean isUseGameCoin() {
        return useGameCoin;
    }
    /**
     * 设置是否正在游戏轮数中
     *
     * @param isInGameRound true-是
     */
    public void setInGameRound(boolean isInGameRound)
    {
        this.isInGameRound = isInGameRound;

        if (isGameStarted)
        {
            setShowStart(!isInGameRound);
        }
    }

    /**
     * 设置是否显示开始游戏
     *
     * @param showStart
     */
    private void setShowStart(boolean showStart)
    {
        if (showStart && ! isAutoDeal()) // 非自动发牌显示开始按钮
        {
            notifyShowGameCtrlStart(true); // 显示开始
            notifyShowGameCtrlClose(true); //显示关闭
            notifyShowGameCtrlWaiting(false); //隐藏等待
            //根据上庄状态显示按钮
//            notifyShowGameCtrlBankerBtn(1);
        } else
        {
            notifyShowGameCtrlStart(false); //隐藏开始
            notifyShowGameCtrlClose(true); // 显示关闭
            notifyShowGameCtrlWaiting(true); //显示等待
//            notifyShowGameCtrlBankerBtn(0);
        }
    }

    /**
     * 处理游戏消息（外部需要调用）
     *
     * @param msg
     */
    public void onLiveMsgGame(MsgModel msg)
    {
        if (msg.getCustomMsgType() == LiveConstant.CustomMsgType.MSG_GAMES_STOP)
        {
            notifyStopGame();
        } else if (msg.getCustomMsgType() == LiveConstant.CustomMsgType.MSG_GAME)
        {
            GameMsgModel msgModel = msg.getCustomMsgGame();
            String user_id = msgModel.getUser_id();
            if(!user_id.equals("0") && !user_id.equals(UserModelDao.query().getUser_id())) {
                return;
            }
            int logId = msgModel.getGame_log_id();
            if (isGameLogIdLegal(logId))
            {
                Log.i("poker_onLiveMsgGame" ,"同一轮或更新的游戏信息");
                notifyInitPanel(msg.getCustomMsgGame());
                updateGameLogId(logId);
                notifyGameMsg(msgModel);
            } else {
                Log.i("poker_onLiveMsgGame" ,"非同一轮或旧数据");
            }
        } else if(msg.getCustomMsgType() == LiveConstant.CustomMsgType.MSG_GAME_BANKER) {
            //处理上庄信息
            GameBankerMsg bankerModel = msg.getCustomMsgBanker();
            notifyBankerMsg(bankerModel);
        }
    }

    private boolean isGameLogIdLegal(int gameLogId)
    {
        return gameLogId >= this.gameLogId;
    }

    public void updateGameLogId(int gameLogId)
    {
        if (gameLogId > this.gameLogId)
        {
            this.gameLogId = gameLogId;
            Log.i("poker_updateGameLogId" ,"更新游戏轮数"+gameLogId);
            notifyLogIdChange(this.gameLogId);
        }
    }

    /**
     * 进入直播间成功回调处理（外部需要调用）
     *
     * @param actModel
     */
    public void onSuccessRequestRoomInfo(App_get_videoActModel actModel)
    {
        int gameLogId = actModel.getGame_log_id();
        if (gameLogId > 0)
        {
            // 等IM加入成功后再请求游戏推送
        } else
        {
            notifyStopGame();
        }
    }

    /**
     * 选择启动游戏插件（主播外部需要调用）
     *
     * @param model
     */
    public final void selectGame(PluginModel model)
    {
        if (currentGameId != 0)
        {
            return;
        }

        setCurrentGameModel(model);
        notifyShowGameCtrlStart(true); // 显示开始
        notifyShowGameCtrlClose(true); //显示关闭
        notifyShowGameCtrlWaiting(false); //隐藏等待
        notifyGameSelected(model);
    }

    private void setCurrentGameModel(PluginModel currentGameModel)
    {
        if (currentGameModel != null)
        {
            currentGameId = SDTypeParseUtil.getInt(currentGameModel.getChild_id());
        } else
        {
            currentGameId = 0;
        }
    }

    public void requestGameInfoDelay() {
        if(mRunnableQuery == null) {
            mRunnableQuery = new TimeoutQueryRunnable();
        }
        Log.i("poker","延时请求");
        SDHandlerManager.getMainHandler().postDelayed(mRunnableQuery, TIMEOUT_QUERY);
    }

    public void cancelDelayQuery() {
        if(mRunnableQuery != null) {
            Log.i("poker", "取消延时请求");
            SDHandlerManager.getMainHandler().removeCallbacks(mRunnableQuery);
        }
    }

    public void cancelAutoDeal() {
        if(mRunnableAutoDeal != null) {
            Log.i("自动发牌", "取消自动发牌");
            SDHandlerManager.getMainHandler().removeCallbacks(mRunnableAutoDeal);
        }
    }



    /**
     * 请求游戏信息
     *
     * @param roomId 房间id
     */
    public void requestGameInfo(int roomId)
    {
        Log.i("poker" ,"调用查询游戏信息接口");
        if (getLiveInfo().getRoomInfo() == null)
        {
            Log.i("poker" ,"房间id为空");
            return;
        }
        if (getLiveInfo().getRoomInfo().getGame_log_id() <= 0 &&this.gameLogId <= 0)
        {
            Log.i("poker" ,"非法game_log_id");
            return;
        }

        cancelRequestGamesInfo();
        requestGameInfoHandler = CommonInterface.requestGamesInfo(roomId, new AppRequestCallback<App_getGamesActModel>()
        {
            @Override
            protected void onSuccess(SDResponse sdResponse)
            {
                if (actModel.isOk())
                {
                    isGameStarted = true;
                    currentGameId = actModel.getGame_id();
                    //处理游戏信息
                    GameMsgModel msgModel = actModel.getData();
                    int logId = msgModel.getGame_log_id();
                    if (isGameLogIdLegal(logId))
                    {
                        Log.i("poker_onLiveMsgGame" ,"同一轮或更新的游戏信息");
                        notifyInitPanel(msgModel);//防止接口回调空指针
                        notifyGameMsg(msgModel);
                        updateGameLogId(logId);
                    } else {
                        Log.i("poker_onLiveMsgGame" ,"非同一轮或旧数据");
                    }
                }else {
                    Log.i("poker" ,"调用接口失败");
                }
            }
        });
    }

    /**
     * 请求游戏轮数钻石
     *
     * @param game_log_id 游戏轮数
     */
    public void requestGameIncome(int game_log_id)
    {
        cancelRequestUserCoinsHandler();
        requestUserCoinsHandler = CommonInterface.requestGameIncome(game_log_id, new AppRequestCallback<App_requestGameIncomeActModel>()
        {
            @Override
            protected void onSuccess(SDResponse sdResponse)
            {
                if (actModel.isOk())
                {
                    //更新游戏币
                    updateCoins(actModel.getUser_diamonds(), actModel.getCoin());//
                    notifyRequestGameIncomeSuccess(actModel);
                }
            }
        });
    }

    /**
     * 获得游戏币或钻石数量
     *
     * @return
     */
    public long getCoins()
    {
        UserModel model = UserModelDao.query();
        if (model != null)
        {
            if(useGameCoin) {
                return model.getCoin();
            } else
                return model.getDiamonds();
        }
        return 0;
    }

    /**
     * 更新钻石或游戏币
     *
     * @param coins
     */
    void updateCoins(long diamonds, long coins)
    {
        if(useGameCoin) {
            UserModelDao.updateCoins(coins);
        } else
            UserModelDao.updateDiamonds(diamonds);

        refreshCoins();
    }

    /**
     * 余额是否够支付
     *
     * @param coins
     * @return
     */
    public boolean canCoinsPay(long coins)
    {
        if(useGameCoin) {
            return UserModelDao.canCoinsPay(coins);
        } else
            return UserModelDao.canDiamondsPay(coins);

    }

    /**
     * 刷新游戏币
     */
    public void refreshCoins()
    {
        UserModel model = UserModelDao.query();
        if (model != null)
        {
            if(useGameCoin) {
                notifyUpdateCoins(model.getCoin());
            } else
                notifyUpdateCoins(model.getDiamonds());
        }
    }

    public void requestStartGameDelay() {
        if(mRunnableAutoDeal == null) {
            mRunnableAutoDeal = new AutoDealRunnable();
        }
        SDHandlerManager.getMainHandler().postDelayed(mRunnableAutoDeal, DEFAULT_AUTO_DEAL_TIME);
    }

    /**
     * 请求开始游戏
     */
    public void requestStartGame()
    {
        if (currentGameId == 0)
        {
            return;
        }

        setShowStart(false);

        cancelRequestStartGameHandler();
        requestStartGameHandler = CommonInterface.requestStartPlugin(String.valueOf(currentGameId), new AppRequestCallback<App_startGameActModel>()
        {
            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (actModel.isOk())
                {
                    isGameStarted = true;
                    setInGameRound(true);
                    notifyRequestStartGameSuccess(actModel);
                }
            }

            @Override
            protected void onError(SDResponse resp)
            {
                super.onError(resp);
                setShowStart(true);
            }
        });
    }

    /**
     * 请求结束游戏
     */
    public void requestStopGame()
    {
        cancelRequestStartGameHandler();
        if (isGameStarted)
        {
            cancelRequestStopGameHandler();
            requestStopGameHandler = CommonInterface.requestStopGame(new AppRequestCallback<BaseActModel>()
            {
                @Override
                protected void onSuccess(SDResponse sdResponse)
                {
                    if (actModel.isOk())
                    {
                        isGameStarted = false;
                        notifyRequestStopGameSuccess(actModel);
                    }
                }
            });
        } else
        {
            onStopGame();
        }
    }

    public void requestOpenGameBanker() {
        notifyShowGameCtrlBankerBtn(0);
        CommonInterface.requestOpenGameBanker(new AppRequestCallback<BaseActModel>() {
            @Override
            protected void onSuccess(SDResponse sdResponse) {
                if(actModel.isOk()) {
                    //
                    notifyShowGameCtrlBankerBtn(2);
                } else {
                    notifyShowGameCtrlBankerBtn(1);
                }
            }

            @Override
            protected void onError(SDResponse resp) {
                super.onError(resp);
                notifyShowGameCtrlBankerBtn(1);
            }
        });
    }

    public void requestStopGameBanker() {
        CommonInterface.requestStopBanker(new AppRequestCallback<BaseActModel>() {
            @Override
            protected void onSuccess(SDResponse sdResponse) {
                if(actModel.isOk()) {
                    notifyShowGameCtrlBankerBtn(1);
                } else {
                    notifyShowGameCtrlBankerBtn(3);
                }
            }

            @Override
            protected void onError(SDResponse resp) {
                super.onError(resp);
                notifyShowGameCtrlBankerBtn(3);
            }
        });
    }

    public void requestApplyBanker(int roomId, long principal) {
        CommonInterface.requestApplyBanker(roomId, principal, new AppRequestCallback<App_banker_applyActModel>() {
            @Override
            protected void onSuccess(SDResponse sdResponse) {
                if(actModel.isOk()) {
                    UserModelDao.updateCoins(actModel.getCoin());
                }
            }

            @Override
            protected void onError(SDResponse resp) {
                super.onError(resp);
            }
        });
    }

    public void onStopGame()
    {
        currentGameId = 0;
        gameLogId = 0;
        isGameStarted = false;
        setAutoDeal(false);
        setInGameRound(false);

        notifyShowGameCtrlStart(false); //隐藏开始
        notifyShowGameCtrlClose(false); //隐藏关闭
        notifyShowGameCtrlWaiting(false); //隐藏等待
        notifyShowGameCtrlBankerBtn(0);//隐藏上庄按钮
    }

    /**
     * 通知游戏插件被选中
     *
     * @param model
     */
    public void notifyGameSelected(PluginModel model)
    {
        businessListener.onGameGameSelected(model);
    }

    /**
     * 通知初始化面板
     *
     * @param msg
     */
    public void notifyInitPanel(GameMsgModel msg)
    {
        businessListener.onGameInitPanel(msg);
    }

    /**
     * 通知游戏轮数变化
     *
     * @param gameLogId
     */
    public void notifyLogIdChange(int gameLogId)
    {
        businessListener.onGameLogIdChange(gameLogId);
    }

    /**
     * 通知游戏消息
     *
     * @param msg
     */
    public void notifyGameMsg(GameMsgModel msg)
    {
        businessListener.onGameMsg(msg);
    }

    public void notifyBankerMsg(GameBankerMsg msg) {
        businessListener.onBankerMsg(msg);
    }

    /**
     * 通知游戏结束
     */
    public void notifyStopGame()
    {
        onStopGame();
        businessListener.onGameMsgStopGame();
    }

    /**
     * 通知刷新钻石
     *
     * @param coins
     */
    private void notifyUpdateCoins(long coins)
    {
        businessListener.onGameUpdateCoins(coins);
    }

    /**
     * 请求游戏信息接口成功回调
     *
     * @param actModel
     */
    public void notifyRequestGameInfoSuccess(App_getGamesActModel actModel)
    {
//        businessListener.onGameRequestGameInfoSuccess(actModel);
    }

    /**
     * 请求游戏轮数钻石成功回调
     *
     * @param actModel
     */
    public void notifyRequestGameIncomeSuccess(App_requestGameIncomeActModel actModel)
    {
        businessListener.onGameRequestGameIncomeSuccess(actModel);
    }

    /**
     * 请求开始游戏接口成功回调
     *
     * @param actModel
     */
    public void notifyRequestStartGameSuccess(App_startGameActModel actModel)
    {
        businessListener.onGameRequestStartGameSuccess(actModel);
    }

    /**
     * 请求结束游戏接口成功回调
     *
     * @param actModel
     */
    public void notifyRequestStopGameSuccess(BaseActModel actModel)
    {
        businessListener.onGameRequestStopGameSuccess(actModel);
    }

    /**
     * 显示隐藏开始按钮
     *
     * @param show
     */
    public void notifyShowGameCtrlStart(boolean show)
    {
        businessListener.onGameCtrlShowStart(show, currentGameId);
    }

    /**
     * 显示隐藏关闭按钮
     *
     * @param show
     */
    public void notifyShowGameCtrlClose(boolean show)
    {
        businessListener.onGameCtrlShowClose(show, currentGameId);
    }

    /**
     * 显示隐藏等待按钮
     *
     * @param show
     */
    public void notifyShowGameCtrlWaiting(boolean show)
    {
        businessListener.onGameCtrlShowWaiting(show, currentGameId);
    }

    /**
     * 是否显示上庄按钮
     * @param status
     */

    public void notifyShowGameCtrlBankerBtn(int status) {
        businessListener.onGameCtrlShowBankerBtn(status);
    }

    private void cancelRequestStartGameHandler()
    {
        if (requestStartGameHandler != null)
        {
            requestStartGameHandler.cancel();
        }
    }

    private void cancelRequestGamesInfo()
    {
        if (requestGameInfoHandler != null)
        {
            requestGameInfoHandler.cancel();
        }
    }

    private void cancelRequestUserCoinsHandler()
    {
        if (requestUserCoinsHandler != null)
        {
            requestUserCoinsHandler.cancel();
        }
    }

    private void cancelRequestStopGameHandler()
    {
        if (requestStopGameHandler != null)
        {
            requestStopGameHandler.cancel();
        }
    }

    /**
     * 主播获取游戏推送超时，主动调用该线程获取游戏信息
     */
    private class TimeoutQueryRunnable implements Runnable {

        @Override
        public void run() {
            requestGameInfo(getLiveInfo().getRoomId());
        }
    }

    private class AutoDealRunnable implements Runnable {

        @Override
        public void run()
        {
            requestStartGame();
        }
    }

    public void onDestroy()
    {
        cancelRequestStartGameHandler();
        cancelRequestGamesInfo();
        cancelRequestUserCoinsHandler();
        cancelRequestStopGameHandler();
        cancelDelayQuery();
        cancelAutoDeal();
    }

    public interface IGameCtrlViewClickListener
    {
        void onClickGameCtrlStart(View view);

        void onClickGameCtrlClose(View view);

        void onClickGameCtrlOpenBanker(View view);

        void onClickGameCtrlStopBanker(View view);

        void onClickGameCtrlBankerList(View view);

    }

    public interface IGameCtrlView
    {
        /**
         * 显示隐藏开始按钮
         *
         * @param show
         */
        void onGameCtrlShowStart(boolean show, int gameId);

        /**
         * 显示隐藏关闭按钮
         *
         * @param show
         */
        void onGameCtrlShowClose(boolean show, int gameId);

        /**
         * 显示隐藏等待按钮
         *
         * @param show
         */
        void onGameCtrlShowWaiting(boolean show, int gameId);

//        void onGameCtrlShowOpenBanker(boolean show, int gameId);
//
//        void onGameCtrlShowStopBanker(boolean show);
//
//        void onGameCtrlShowBankerList(boolean show);

        void onGameCtrlShowBankerBtn(int status);
    }

    public interface GameBusinessListener extends IGameCtrlView
    {

        /**
         * 游戏被选中回调
         *
         * @param model
         */
        void onGameGameSelected(PluginModel model);

        /**
         * 游戏面板初始化
         *
         * @param msg
         */
        void onGameInitPanel(GameMsgModel msg);

        /**
         * 游戏消息
         *
         * @param msg
         */
        void onGameMsg(GameMsgModel msg);

        void onBankerMsg(GameBankerMsg msg);

        /**
         * 游戏轮数变化
         *
         * @param gameLogId
         */
        void onGameLogIdChange(int gameLogId);

        /**
         * 游戏结束消息
         */
        void onGameMsgStopGame();

        /**
         * 请求游戏信息接口成功回调
         *
         * @param actModel
         */
//        void onGameRequestGameInfoSuccess(App_getGamesActModel actModel);

        /**
         * 请求游戏轮数钻石成功回调
         *
         * @param actModel
         */
        void onGameRequestGameIncomeSuccess(App_requestGameIncomeActModel actModel);

        /**
         * 请求开始游戏接口成功回调
         *
         * @param actModel
         */
        void onGameRequestStartGameSuccess(App_startGameActModel actModel);

        /**
         * 请求结束游戏接口成功回调
         *
         * @param actModel
         */
        void onGameRequestStopGameSuccess(BaseActModel actModel);


        /**
         * 钻石更新
         *
         * @param coins
         */
        void onGameUpdateCoins(long coins);
    }
}

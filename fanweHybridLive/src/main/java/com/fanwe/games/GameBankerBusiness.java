package com.fanwe.games;

import com.fanwe.games.model.GameBankerModel;
import com.fanwe.games.model.custommsg.GameBankerMsg;
import com.fanwe.games.model.custommsg.GameMsgModel;
import com.fanwe.live.dao.UserModelDao;

/**
 * Created by shibx on 2016/02/23.
 * 处理上庄信息的业务类
 */

public class GameBankerBusiness {

    private GameBusiness gameBusiness;
    private BankerBusinessListener mGameBusinessListener;

    public GameBankerBusiness(GameBusiness gameBusiness)
    {
        this.gameBusiness = gameBusiness;
    }

    public void setGameBusinessListener(BankerBusinessListener gameBusinessListener) {
        this.mGameBusinessListener = gameBusinessListener;
    }

    /**
     * 处理推送消息（外部需要调用）
     *  处理 type43
     * @param msg
     */
    public void onBankerMsg(GameBankerMsg msg) {
        //上庄模块推送操作号，1：开启上庄，2：申请上庄，3：选择庄家，4：下庄
        //上庄状态，0：未开启上庄，1：玩家申请上庄，2：玩家上庄
        String userId = UserModelDao.query().getUser_id();
        switch(msg.getAction()) {
            case 1 :
                mGameBusinessListener.onMsgOpenBanker(msg.getData().getPrincipal());
                break;
            case 2 :
                mGameBusinessListener.onMsgApplyBanker(msg);
                break;
            case 3 :
                mGameBusinessListener.onMsgChooseBanker(msg.getData().getBanker(),userId);
                break;
            case 4:
                mGameBusinessListener.onMsgRemoveBanker(msg,userId);
                break;
            default:
                break;
        }
    }

    /**
     *
     * @param msg
     */
    public void onBankerMsg(GameMsgModel msg) {
        int status = msg.getBanker_status();
        //上庄状态，0：未开启上庄，1：玩家申请上庄，2：玩家上庄
        String userId = UserModelDao.query().getUser_id();
        switch(status) {
            case 0 :
                mGameBusinessListener.onMsgRemoveBanker(null, userId);
                break;
            case 1 :
                long principal = msg.getPrincipal();
                mGameBusinessListener.onMsgOpenBanker(principal);
                break;
            case 2:
                GameBankerModel banker = msg.getBanker();
                mGameBusinessListener.onMsgChooseBanker(banker, userId);
                break;
            default:
                break;
        }
    }

    public interface BankerBusinessListener {

        void onMsgOpenBanker(long principal);

        void onMsgApplyBanker(GameBankerMsg msg);

        void onMsgChooseBanker(GameBankerModel msg, String userId);

        void onMsgRemoveBanker(GameBankerMsg msg, String userId);
    }
}

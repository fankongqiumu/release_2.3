package com.librarygames.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.fanwe.library.looper.impl.SDSimpleLooper;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.SDViewUtil;
import com.librarygames.GameConstant;
import com.librarygames.R;
import com.librarygames.dialog.GamesHistoryDialog;
import com.librarygames.dialog.GamesWinnerDialog;
import com.librarygames.model.PokerGoldFlowerModel;
import com.librarygames.utils.GamesAnimationUtil;
import com.librarygames.utils.GamesResUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by shibx on 2016/12/12.
 * {@link PokerGoldFlowerPanelView} 扎金花面板
 * {@link PokerBullPanelView} 斗牛面板
 * {@link GameConstant} 游戏常量
 * 游戏面板的业务处理
 */

public class PokerBasePanelView extends BaseGamesView implements PokerPanelBottomView.PokerBottomViewListener,
        GameClockView.OnFinishListener {

    private ImageView iv_poker;
    private ImageView iv_pokers;
    protected PokerBaseView pv_1;
    protected PokerBaseView pv_2;
    protected PokerBaseView pv_3;
    private RelativeLayout rel_pv_1;
    private RelativeLayout rel_pv_2;
    private RelativeLayout rel_pv_3;

    private LinearLayout ll_gold_amount;
    private PokerPanelBottomView pokerPanelBottomView;
    private LinearLayout ll_bet_area_1;
    private LinearLayout ll_bet_area_2;
    private LinearLayout ll_bet_area_3;
    private PokerBetAreaView pokerBetAreaView_1;
    private PokerBetAreaView pokerBetAreaView_2;
    private PokerBetAreaView pokerBetAreaView_3;

    private GameResultTextView gameResultTextView_1;
    private GameResultTextView gameResultTextView_2;
    private GameResultTextView gameResultTextView_3;

    private GameClockView mClockView;
    private GameToastView gametoastview;

    private GamesHistoryDialog mDialogHistory;
    private GamesWinnerDialog mDialogWinner;

    protected int mGameType;

    protected boolean isCreater;//是否主播
    protected PokerPanelListener mListener;

    private boolean isFirstInit = true;

    private int mGameStatus = GameConstant.START;
    private int mGameLogId;//当前游戏轮数
    private int mGameId;//游戏id
    private int mPositionWiner;//当前轮数胜者

    private SoundPool mSound;
    private SparseIntArray mArraySounds;

    private AnimatorSet mAnimatorSet;//发牌动画

    private SDSimpleLooper mLooper;
    private ShowPokerRunable mRunnable;

    private int mIndexShowPoker = 1;

    //    private long time;//距结算时长
    private boolean playAnimator;//是否播放发牌动画

    private List<PokerGoldFlowerModel> listPokers;
    private boolean showDelay;

    private static final float BLUR = 0.5f;//发牌面板变暗得透明度

    protected int imageRes_1;
    protected int imageRes_2;
    protected int imageRes_3;

    public PokerBasePanelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public PokerBasePanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PokerBasePanelView(Context context) {
        super(context);
    }

    protected void register() {
        initSounds();
        mLooper = new SDSimpleLooper();
        mRunnable = new ShowPokerRunable();
        //下注区域设置
        pokerBetAreaView_1.setPosition(1);
        pokerBetAreaView_2.setPosition(2);
        pokerBetAreaView_3.setPosition(3);

        //是否是主播
        iv_poker.setOnClickListener(this);
        ll_bet_area_1.setOnClickListener(this);
        ll_bet_area_2.setOnClickListener(this);
        ll_bet_area_3.setOnClickListener(this);
        pokerPanelBottomView.setPokerBottomViewListener(this);
        mClockView.setFinishListener(this);
        setCreaterMode(isCreater);
        pokerPanelBottomView.isCreater(isCreater);
    }

    /**
     * 主播模式
     * 无法投注，无投注金币，不同的提示文字
     */
    public void setCreaterMode(boolean isCreater) {
        pokerBetAreaView_1.hideUserBet(isCreater);
        pokerBetAreaView_2.hideUserBet(isCreater);
        pokerBetAreaView_3.hideUserBet(isCreater);
        pokerPanelBottomView.enableDoBet(isCreater);
        gametoastview.setIsCreater(isCreater);
        if(isCreater) {
            ll_bet_area_1.setOnClickListener(null);
            ll_bet_area_2.setOnClickListener(null);
            ll_bet_area_3.setOnClickListener(null);
        } else {
            ll_bet_area_1.setOnClickListener(this);
            ll_bet_area_2.setOnClickListener(this);
            ll_bet_area_3.setOnClickListener(this);
        }
    }

    /**
     * 是否开启自动发牌功能
     * @param open
     */
    public void openAutoFunction(boolean open) {
        pokerPanelBottomView.openAutoFunction(open);
    }

    public void setAutoUi(boolean isAutoDeal) {
        pokerPanelBottomView.setAutoUi(isAutoDeal);
    }

    protected void initViews(View view) {
        iv_poker = (ImageView) view.findViewById(R.id.iv_poker);
        iv_pokers = (ImageView) view.findViewById(R.id.iv_pokers);
        mClockView = (GameClockView) view.findViewById(R.id.gcv_clock);
        gametoastview = (GameToastView) view.findViewById(R.id.gametoastview);
        rel_pv_1 = (RelativeLayout) view.findViewById(R.id.rel_pv_1);
        rel_pv_2 = (RelativeLayout) view.findViewById(R.id.rel_pv_2);
        rel_pv_3 = (RelativeLayout) view.findViewById(R.id.rel_pv_3);
        pv_1 = (PokerBaseView) view.findViewById(R.id.pv_1);
        pv_2 = (PokerBaseView) view.findViewById(R.id.pv_2);
        pv_3 = (PokerBaseView) view.findViewById(R.id.pv_3);

        ll_gold_amount = (LinearLayout) view.findViewById(R.id.ll_gold_amount);
        pokerBetAreaView_1 = (PokerBetAreaView) view.findViewById(R.id.bet_area_one);
        pokerBetAreaView_2 = (PokerBetAreaView) view.findViewById(R.id.bet_area_two);
        pokerBetAreaView_3 = (PokerBetAreaView) view.findViewById(R.id.bet_area_three);
        ll_bet_area_1 = (LinearLayout) view.findViewById(R.id.ll_bet_area_one);
        ll_bet_area_2 = (LinearLayout) view.findViewById(R.id.ll_bet_area_two);
        ll_bet_area_3 = (LinearLayout) view.findViewById(R.id.ll_bet_area_three);
        pokerPanelBottomView = (PokerPanelBottomView) view.findViewById(R.id.poker_bottom_view);
        gameResultTextView_1 = (GameResultTextView) view.findViewById(R.id.grtv1);
        gameResultTextView_2 = (GameResultTextView) view.findViewById(R.id.grtv2);
        gameResultTextView_3 = (GameResultTextView) view.findViewById(R.id.grtv3);
        pv_1.setPokerBack(mGameType);
        pv_2.setPokerBack(mGameType);
        pv_3.setPokerBack(mGameType);
        iv_poker.setImageResource(GamesResUtil.getPokerBackResId(mGameType));
        iv_pokers.setImageResource(GamesResUtil.getPokersBackResId(mGameType));
    }

    /**
     * 设置游戏面板 和 历史记录 3个人物 的资源图片
     * @param resId_1 图片1
     * @param resId_2 图片2
     * @param resId_3 图片3
     */
    protected void setImageRes(int resId_1, int resId_2, int resId_3) {
        this.imageRes_1 = resId_1;
        this.imageRes_2 = resId_2;
        this.imageRes_3 = resId_3;
        pokerBetAreaView_1.iv_star.setImageResource(resId_1);
        pokerBetAreaView_2.iv_star.setImageResource(resId_2);
        pokerBetAreaView_3.iv_star.setImageResource(resId_3);
    }

    private void initSounds() {
        mSound = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
//        mSound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
//            @Override
//            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
//
//            }
//        });
        mArraySounds = new SparseIntArray();
        mArraySounds.put(GameConstant.PokerSounds.POKER_DEAL_SOUND,mSound.load(getContext(), R.raw.dealsound, 0));
    }

    public boolean isMsgLegal(int gameLogId, int gameStatus) {
        boolean isSameRound = this.mGameLogId == gameLogId;
        if(isSameRound && this.mGameStatus == gameStatus) {
            return false;
        } else {
            setmGameLogId(gameLogId);
            setGameStatus(gameStatus);
            return true;
        }
//        Log.i("poker_isStatusLegal",getmGameLogId() +"---"+ this.mGameStatus+"----"+gameStatus);
//        if((this.mGameStatus == GameConstant.BETABLE || this.mGameStatus == GameConstant.SETTLEMENT) && this.mGameStatus == gameStatus) {
//            Log.i("poker_isStatusLegal","false");
//            return false;
//        }
//        setGameStatus(gameStatus);
//        Log.i("poker_isStatusLegal","true");
    }

    public void setGameStatus(int gameStatus) {
        this.mGameStatus = gameStatus;
        if(!isFirstInit) {
            isClickBetArea(mGameStatus);
            gametoastview.start(mGameStatus);

            Log.i("poker_setGameStatus" ,"gameStatus="+mGameStatus);
            if(gameStatus != GameConstant.BETABLE) {
                mClockView.invisible();
            }
        }
    }

    public int getGameStatus() {
        return mGameStatus;
    }

    public int getmGameId() {
        return mGameId;
    }

    public void setmGameId(int mGameId) {
        this.mGameId = mGameId;
    }

    public int getmGameLogId() {
        return mGameLogId;
    }

    public void setmGameLogId(int mGameLogId) {
        this.mGameLogId = mGameLogId;
        Log.i("poker_setmGameLogId" ,"更新gamelogid");
    }

    private void isClickBetArea(int gameStatus) {
        switch (gameStatus){
            case GameConstant.BETABLE:
                setBetAreaEnabled(true);
                break;
            default:
                setBetAreaEnabled(false);
                break;
        }
    }

    /**
     * 下注区域是否可点击
     */
    private void setBetAreaEnabled(boolean isClick){
        ll_bet_area_1.setEnabled(isClick);
        ll_bet_area_2.setEnabled(isClick);
        ll_bet_area_3.setEnabled(isClick);
    }

    private void tranToView(View goldView, View starView){
        if (goldView != null && starView != null) {
            GamesAnimationUtil.translateViewToViewForWindowManager(goldView,starView,300);
        }
    }

    /**
     * 发牌，可投注
     * @param time 距结算时间
     * @param playAnimator 是否播放动画
     */
    public void startGame(long time, boolean playAnimator) {
//        this.time = time;
        this.playAnimator = playAnimator;
        mLooper.stop();
        if(!isFirstInit) {
            if(!playAnimator) {
                addPokerCard(false);
            } else {
                SDViewUtil.show(iv_pokers);
                showAnima();
            }
        }
        mClockView.setTimeCount(time);
        mClockView.start();
    }

    /**
     * 增加扑克牌--背面
     * @param playAnimator 是否播放动画，false则全部一起出现
     */
    private void addPokerCard(boolean playAnimator) {
        if(playAnimator) {
            if(!pv_1.isAllAdded()) {
                pv_1.addPoker();
            } else if(!pv_2.isAllAdded()) {
                pv_2.addPoker();
            } else {
                pv_3.addPoker();
            }
        } else {
            if(!pv_1.isAllAdded()) {
                pv_1.addPokerBack();
            }
            if(!pv_2.isAllAdded()) {
                pv_2.addPokerBack();
            }
            if(!pv_3.isAllAdded()) {
                pv_3.addPokerBack();
            }
        }
    }

    private void showAnima() {
        //重置牌局
        initAnimatorSet(getPokerViews());
//        if(mAnimatorSet == null) {
//
//        }
        mAnimatorSet.start();
    }

    private void initAnimatorSet(List<View> listViews) {
        mAnimatorSet = new AnimatorSet();
        Animator [] translationAnimators;
        List<Animator[]> list = new ArrayList<>();
        for (View view : listViews) {
            translationAnimators = GamesAnimationUtil.translateViewToView(iv_poker, view, 200);
            list.add(translationAnimators);
        }
        Animator [] scaleAnimators = GamesAnimationUtil.scaleView(iv_poker, listViews.get(0), 200);//发牌缩放动画

        Animator translationAnimator;
        if(!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                translationAnimator = list.get(i)[0];
                translationAnimator.addListener(mAnimatorListener);
                if(i == 0) {
                    mAnimatorSet.play(translationAnimator).with(list.get(i)[1]).with(scaleAnimators[0]).with(scaleAnimators[1]);
                } else {
                    mAnimatorSet.play(translationAnimator).with(list.get(i)[1]).with(scaleAnimators[0]).with(scaleAnimators[1]).after(list.get(i-1)[0]);
                }
            }
        }
        //每段动画执行的间隔时间
        mAnimatorSet.setDuration(200);
    }

    private List<View> getPokerViews() {
        List<View> viewList = new ArrayList<>();
        Collections.addAll(viewList, pv_1.getPokerViews());
        Collections.addAll(viewList, pv_2.getPokerViews());
        Collections.addAll(viewList, pv_3.getPokerViews());
        return viewList;
    }

    private void playSounds(int soundType) {
        if(mSound == null) {
            return ;
        }
        switch (soundType) {
            case GameConstant.PokerSounds.POKER_DEAL_SOUND:
                mSound.play(mArraySounds.get(soundType),1.0f,1.0f,0,0,1.0f);
                break;
            default:
                break;
        }
    }

    /**
     * 更新所有扑克牌数据
     * @param listPokers 扑克牌数据
     * @param showDelay 是否延迟开牌
     */
    public void updatePokerDatas(List<PokerGoldFlowerModel> listPokers, int indexWiner, boolean showDelay) {
        this.listPokers = listPokers;
        this.showDelay = showDelay;
        this.mPositionWiner = indexWiner;
        if(!isFirstInit) {
            pv_1.setPokerCards(listPokers.get(0));
            pv_2.setPokerCards(listPokers.get(1));
            pv_3.setPokerCards(listPokers.get(2));
            gameResultTextView_1.setType(mGameType,listPokers.get(0).getType());
            gameResultTextView_2.setType(mGameType,listPokers.get(1).getType());
            gameResultTextView_3.setType(mGameType,listPokers.get(2).getType());
            setPanelAlpha(0);
            hideText();
            if(mAnimatorSet != null && mAnimatorSet.isRunning()) {
                mAnimatorSet.cancel();
            }
            if(showDelay) {
                mIndexShowPoker = 1;
                //接受通知开牌
                //文字动画
                mLooper.start(1000,1000,mRunnable);

            } else {
                showPokerCards(false);
                setPanelAlpha(indexWiner);
                mListener.onPokerSettle(mGameLogId, isCreater);
            }
        }
    }

    /**
     * 展示扑克牌 ---正面
     * @param showDelay 是否延迟展示
     */
    private void showPokerCards(boolean showDelay) {

        if(showDelay) {
            if(mIndexShowPoker == 1) {
                pv_1.showPokersInfo();
                gameResultTextView_1.start();
            } else if(mIndexShowPoker == 2) {
                pv_2.showPokersInfo();
                gameResultTextView_2.start();
            } else if(mIndexShowPoker == 3) {
                pv_3.showPokersInfo();
                gameResultTextView_3.start();
            }
        } else {
            pv_1.showPokersInfo();
            gameResultTextView_1.start();
            pv_2.showPokersInfo();
            gameResultTextView_2.start();
            pv_3.showPokersInfo();
            gameResultTextView_3.start();
        }
    }

    private void setPanelAlpha(int position) {
        switch(position) {
            case 1:
                rel_pv_2.setAlpha(BLUR);
                rel_pv_3.setAlpha(BLUR);
                ll_bet_area_2.setAlpha(BLUR);
                ll_bet_area_3.setAlpha(BLUR);
                break;
            case 2 :
                rel_pv_1.setAlpha(BLUR);
                rel_pv_3.setAlpha(BLUR);
                ll_bet_area_1.setAlpha(BLUR);
                ll_bet_area_3.setAlpha(BLUR);
                break;
            case 3:
                rel_pv_2.setAlpha(BLUR);
                rel_pv_1.setAlpha(BLUR);
                ll_bet_area_2.setAlpha(BLUR);
                ll_bet_area_1.setAlpha(BLUR);
                break;
            default:
                SDViewUtil.resetView(rel_pv_1);
                SDViewUtil.resetView(rel_pv_2);
                SDViewUtil.resetView(rel_pv_3);
                SDViewUtil.resetView(ll_bet_area_1);
                SDViewUtil.resetView(ll_bet_area_2);
                SDViewUtil.resetView(ll_bet_area_3);
                break;
        }
    }

    /**
     * 更新 投注数据 ,由下注接口回调时调用
     * @param listAll 所有投注
     * @param listUser 自己的投注
     */
    public void updateBet(List<Integer> listAll, List<Integer> listUser) {
        if(listAll == null || listUser == null || listAll.isEmpty() || listUser.isEmpty()) {
            return;
        }
        pokerBetAreaView_1.setBetGold(listAll.get(pokerBetAreaView_1.getIndex()),listUser.get(pokerBetAreaView_1.getIndex()));
        pokerBetAreaView_2.setBetGold(listAll.get(pokerBetAreaView_2.getIndex()),listUser.get(pokerBetAreaView_2.getIndex()));
        pokerBetAreaView_3.setBetGold(listAll.get(pokerBetAreaView_3.getIndex()),listUser.get(pokerBetAreaView_3.getIndex()));
    }

    /**
     * 更新投注数据，由投注推送回调时调用
     * @param listAll 所有投注
     */
    public void updateBet(List<Integer> listAll) {
        pokerBetAreaView_1.setBetGold(listAll.get(pokerBetAreaView_1.getIndex()));
        pokerBetAreaView_2.setBetGold(listAll.get(pokerBetAreaView_2.getIndex()));
        pokerBetAreaView_3.setBetGold(listAll.get(pokerBetAreaView_3.getIndex()));
    }

    /**
     * 设置投注数量跟余额
     * @param listCoins 下注金币列表
     * @param account 账户余额
     */
    public void setBetsCoin(List<Integer> listCoins, long account) {

        pokerPanelBottomView.setData(listCoins,account);
    }

    /**
     * 下注区倍数
     * @param listTimes 集合长度为 下注区数量，对应下注区 收益倍数
     */
    public void setBetsArea(List<String> listTimes) {
        SDViewBinder.setTextView(pokerBetAreaView_1.tv_mul,listTimes.get(pokerBetAreaView_1.getIndex()) + "倍");
        SDViewBinder.setTextView(pokerBetAreaView_2.tv_mul,listTimes.get(pokerBetAreaView_2.getIndex()) + "倍");
        SDViewBinder.setTextView(pokerBetAreaView_3.tv_mul,listTimes.get(pokerBetAreaView_3.getIndex()) + "倍");
    }

    public int getSelBetGold(){
        return pokerPanelBottomView.getSelectBetGold();
    }

    /**
     * 设置余额
     * @param account 账户余额
     */
    public void updateAccount(long account){
        pokerPanelBottomView.setAccount(account);
    }

    /**
     * 动画取消调用，判断牌是否发放完全，停止音效、隐藏扑克牌堆
     * @param isCancel 是否取消
     */
    private void animationFinish(boolean isCancel) {
        boolean isAllAdded = pv_1.isAllAdded() && pv_2.isAllAdded() && pv_3.isAllAdded();
        if(!isAllAdded) {
            if(!isCancel)
                return ;
            addPokerCard(false);
        }
        SDViewUtil.invisible(iv_pokers);
        if(mSound != null) {
            mSound.autoPause();
        }
    }

    /**
     * 动画监听器
     */
    private Animator.AnimatorListener mAnimatorListener = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animation) {
            //播放发牌音效
            playSounds(0);
            //显示单张扑克牌
            iv_poker.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            //重置View状态
            SDViewUtil.resetView(iv_poker);
            //增加扑克牌
            addPokerCard(true);
            //隐藏单张扑克牌
            iv_poker.setVisibility(View.INVISIBLE);
            //隐藏扑克牌堆，停止音效
            animationFinish(false);
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            animationFinish(true);
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    @Override
    public void onFinish() {
        //倒计时结束调用
        mListener.onClockFinish();
    }

    private class ShowPokerRunable implements Runnable {

        @Override
        public void run() {
            if(mIndexShowPoker <= 3) {
                mClockView.invisible();
                showPokerCards(true);
                mIndexShowPoker ++;
            } else {
                setPanelAlpha(mPositionWiner);
                mLooper.stop();
                mListener.onPokerSettle(mGameLogId, isCreater);
            }
        }
    }

    public interface PokerPanelListener {
        void onClickBetArea(int game_log_id, int index);
        void onClickBetRecharge();
        void onClickHistory(String game_id);
        void onPokerSettle(int game_log_id, boolean isCreater);
        void onClockFinish();
        void onClickAutoDeal();
    }

    public void cleanDatas() {
        cleanBet();
        resetPokers();
        hideText();
        setPanelAlpha(0);
//        gametoastview.start(GameConstant.GoldFlowerStatus.START);
//        Log.i("poker_cleanDatas","清空数据，显示游戏即将开始");
    }

    private void cleanBet() {
        pokerBetAreaView_1.resetBets();
        pokerBetAreaView_2.resetBets();
        pokerBetAreaView_3.resetBets();
    }

    private void resetPokers() {
        pv_1.resetPoker();
        pv_2.resetPoker();
        pv_3.resetPoker();
    }

    private void hideText() {
        gameResultTextView_1.end();
        gameResultTextView_2.end();
        gameResultTextView_3.end();
    }

    public void showHistoryDialog(List<Integer> list) {
        if(mDialogHistory == null) {
            mDialogHistory = new GamesHistoryDialog(getActivity());
            mDialogHistory.setImageRes(imageRes_1, imageRes_2, imageRes_3);
        }
        mDialogHistory.setHistory(list);
        mDialogHistory.showCenter();
    }

    public void showWinnerDialog(int coin, String unit) {
        if(mDialogWinner == null) {
            mDialogWinner = new GamesWinnerDialog(getActivity());
        }
        mDialogWinner.setWinInfo(coin, unit);
        mDialogWinner.show();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed) {
            if(isFirstInit) {
                isFirstInit = false;
                Log.i("poker__onLayout","游戏面板初始化完成，为游戏状态赋值" + mGameStatus);
                setGameStatus(mGameStatus);
                switch(mGameStatus) {
                    case GameConstant.START :
                        cleanDatas();
                        break;
                    case GameConstant.BETABLE :
                        startGame(mClockView.getTime(), playAnimator);
                        break;
                    case GameConstant.SETTLEMENT :
                        updatePokerDatas(listPokers, mPositionWiner, showDelay);
                        break;
                    case GameConstant.END :
                        cleanDatas();
                        break;
                    default:
                        cleanDatas();
                        break;
                }
            }
        }
    }

    @Override
    public void onClickRecharge() {
        mListener.onClickBetRecharge();
    }

    @Override
    public void onClickHistory() {
        mListener.onClickHistory(String.valueOf(getmGameId()));
    }

    @Override
    public void onClickAutoDeal()
    {
        mListener.onClickAutoDeal();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == ll_bet_area_1) {
//            tranToView(pokerPanelBottomView.getGoldView(), pokerBetAreaView_1.getImgView());
            mListener.onClickBetArea(getmGameLogId(), pokerBetAreaView_1.getPosition());
        }else if (v == ll_bet_area_2) {
//            tranToView(pokerPanelBottomView.getGoldView(), pokerBetAreaView_2.getImgView());
            mListener.onClickBetArea(getmGameLogId(), pokerBetAreaView_2.getPosition());
        } else if (v == ll_bet_area_3) {
//            tranToView(pokerPanelBottomView.getGoldView(), pokerBetAreaView_3.getImgView());
            mListener.onClickBetArea(getmGameLogId(), pokerBetAreaView_3.getPosition());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mSound != null) {
            mSound.autoPause();
            mSound.release();
            mSound = null;
        }
        if(mDialogHistory != null)
            mDialogHistory = null;
        if(mDialogWinner != null) {
            mDialogWinner.stopDismissRunnable();
            mDialogWinner = null;
        }

        mClockView.cancel();
    }
}

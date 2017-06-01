package com.librarygames;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by shibx on 2016/12/5.
 * 扑克牌游戏各常量
 */

public class GameConstant {

    /**
     * 游戏类型
     *  1、扎金花
     *  2、斗牛
     */
    @IntDef({POKER_GOLDFLOWER,POKER_BULL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GameType {}

    public static final int POKER_GOLDFLOWER = 1;
    public static final int POKER_BULL = 2;

    /**
     * 扑克牌型
     *  1、黑桃
     *  2、红桃
     *  3、梅花
     *  4、方块
     */
    @IntDef({TYPE_SPADE,TYPE_HEART,TYPE_CLUB,TYPE_DIAMOND})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PokerTypes{}

    public static final int TYPE_SPADE = 0;
    public static final int TYPE_HEART = 1;
    public static final int TYPE_CLUB = 2;
    public static final int TYPE_DIAMOND = 3;

    /**
     * 扑克牌数字
     */
    @IntDef({POKER_1,POKER_2,POKER_3,POKER_4,POKER_5,POKER_6,POKER_7,POKER_8,POKER_9,POKER_10,POKER_11,POKER_12,POKER_13})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PokerNumber {}

    public static final int POKER_1 = 1;
    public static final int POKER_2 = 2;
    public static final int POKER_3 = 3;
    public static final int POKER_4 = 4;
    public static final int POKER_5 = 5;
    public static final int POKER_6 = 6;
    public static final int POKER_7 = 7;
    public static final int POKER_8 = 8;
    public static final int POKER_9 = 9;
    public static final int POKER_10 = 10;
    public static final int POKER_11 = 11;
    public static final int POKER_12 = 12;
    public static final int POKER_13 = 13;

    /**
     * 扎金花游戏状态
     */
    @IntDef({START,BETABLE,END,SETTLEMENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GoldFlowerStatus {}

    public static final int START = 1;//开始
    public static final int BETABLE = 2;//下注
    public static final int END = 3;//停止
    public static final int SETTLEMENT = 4;//结算

    /**
     * 扎金花牌面类型
     */
    @IntDef({GLODFLOWER_TYPE_BAOZI,GLODFLOWER_TYPE_TONGHUASHUN,GLODFLOWER_TYPE_TONGHUA,GLODFLOWER_TYPE_SHUNZI,GLODFLOWER_TYPE_DUIZI,GLODFLOWER_TYPE_DANPAI})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GlodFlowerTypes {}

    public static final int GLODFLOWER_TYPE_BAOZI = 0;
    public static final int GLODFLOWER_TYPE_TONGHUASHUN = 1;
    public static final int GLODFLOWER_TYPE_TONGHUA = 2;
    public static final int GLODFLOWER_TYPE_SHUNZI = 3;
    public static final int GLODFLOWER_TYPE_DUIZI = 4;
    public static final int GLODFLOWER_TYPE_DANPAI = 5;

    /**
     * 斗牛 牌型
     */
    @IntDef({BULL_TYPE_WXN,BULL_TYPE_ZD,BULL_TYPE_WHN,BULL_TYPE_SHN,BULL_TYPE_NN,BULL_TYPE_N9,BULL_TYPE_N8,
            BULL_TYPE_N7,BULL_TYPE_N6,BULL_TYPE_N5,BULL_TYPE_N4,BULL_TYPE_N3,BULL_TYPE_N2,BULL_TYPE_N1,BULL_TYPE_MN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BullTypes {}

    public static final int BULL_TYPE_WXN = 0;//五小牛
    public static final int BULL_TYPE_ZD = 1;//炸弹
    public static final int BULL_TYPE_WHN = 2;//五花牛
    public static final int BULL_TYPE_SHN = 3;//四小牛
    public static final int BULL_TYPE_NN = 4;//牛牛
    public static final int BULL_TYPE_N9 = 5;//牛9
    public static final int BULL_TYPE_N8 = 6;//牛8
    public static final int BULL_TYPE_N7 = 7;//牛7
    public static final int BULL_TYPE_N6 = 8;//牛6
    public static final int BULL_TYPE_N5 = 9;//牛5
    public static final int BULL_TYPE_N4 = 10;//牛4
    public static final int BULL_TYPE_N3 = 11;//牛3
    public static final int BULL_TYPE_N2 = 12;//牛2
    public static final int BULL_TYPE_N1 = 13;//牛1
    public static final int BULL_TYPE_MN = 14;//没牛

    /**
     * 扑克音效
     */
    public static final class PokerSounds {
        public static final int POKER_DEAL_SOUND = 0;//发牌音效
    }
}

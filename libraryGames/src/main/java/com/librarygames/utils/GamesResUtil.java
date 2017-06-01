package com.librarygames.utils;

import com.librarygames.GameConstant;
import com.librarygames.R;

/**
 * Created by shibx on 2016/11/22.
 * 获取扑克花色、数字图片资源
 */

public class GamesResUtil {

    /**
     * 获取扑克类型图片
     * @param type 扑克类型
     * @see GameConstant.PokerTypes
     * @return
     */
    public static int getPokerTypeResId(@GameConstant.PokerTypes int type) {
        switch (type) {
            case GameConstant.TYPE_CLUB:
                return R.drawable.poker_club;
            case GameConstant.TYPE_HEART:
                return R.drawable.poker_heart;
            case GameConstant.TYPE_DIAMOND:
                return R.drawable.poker_diamond;
            case GameConstant.TYPE_SPADE:
                return R.drawable.poker_spade;
            default:
                return 0;
        }
    }

    public static int getPokerCenterResId(@GameConstant.PokerNumber int num, @GameConstant.PokerTypes int type) {
        switch (num) {
            case GameConstant.POKER_11:
                return getJResId(type);
            case GameConstant.POKER_12:
                return getQResId(type);
            case GameConstant.POKER_13:
                return getKResId(type);
            default:
                return getPokerTypeResId(type);
        }
    }

    private static int getJResId(@GameConstant.PokerTypes int type) {
        switch(type) {
            case GameConstant.TYPE_CLUB:
                return R.drawable.poker_club_j;
            case GameConstant.TYPE_HEART:
                return R.drawable.poker_heart_j;
            case GameConstant.TYPE_DIAMOND:
                return R.drawable.poker_diamond_j;
            case GameConstant.TYPE_SPADE:
                return R.drawable.poker_spade_j;
            default:
                return 0;
        }
    }

    private static int getQResId(@GameConstant.PokerTypes int type) {
        switch(type) {
            case GameConstant.TYPE_CLUB:
                return R.drawable.poker_club_q;
            case GameConstant.TYPE_HEART:
                return R.drawable.poker_heart_q;
            case GameConstant.TYPE_DIAMOND:
                return R.drawable.poker_diamond_q;
            case GameConstant.TYPE_SPADE:
                return R.drawable.poker_spade_q;
            default:
                return 0;
        }
    }

    private static int getKResId(@GameConstant.PokerTypes int type) {
        switch(type) {
            case GameConstant.TYPE_CLUB:
                return R.drawable.poker_club_k;
            case GameConstant.TYPE_HEART:
                return R.drawable.poker_heart_k;
            case GameConstant.TYPE_DIAMOND:
                return R.drawable.poker_diamond_k;
            case GameConstant.TYPE_SPADE:
                return R.drawable.poker_spade_k;
            default:
                return 0;
        }
    }

    /**
     * 获取扑克数字
     * @param num
     * @return
     */
    public static int getPokerNumResId(@GameConstant.PokerNumber int num, @GameConstant.PokerTypes int type) {
        boolean isBlack = (type == GameConstant.TYPE_CLUB) || (type == GameConstant.TYPE_SPADE);
        switch (num) {
            case GameConstant.POKER_1:
                return isBlack ? R.drawable.img_poker_ace_black : R.drawable.img_poker_ace_red;
            case GameConstant.POKER_2:
                return isBlack ? R.drawable.img_poker_2_black : R.drawable.img_poker_2_red;
            case GameConstant.POKER_3:
                return isBlack ? R.drawable.img_poker_3_black : R.drawable.img_poker_3_red;
            case GameConstant.POKER_4:
                return isBlack ? R.drawable.img_poker_4_black : R.drawable.img_poker_4_red;
            case GameConstant.POKER_5:
                return isBlack ? R.drawable.img_poker_5_black : R.drawable.img_poker_5_red;
            case GameConstant.POKER_6:
                return isBlack ? R.drawable.img_poker_6_black : R.drawable.img_poker_6_red;
            case GameConstant.POKER_7:
                return isBlack ? R.drawable.img_poker_7_black : R.drawable.img_poker_7_red;
            case GameConstant.POKER_8:
                return isBlack ? R.drawable.img_poker_8_black : R.drawable.img_poker_8_red;
            case GameConstant.POKER_9:
                return isBlack ? R.drawable.img_poker_9_black : R.drawable.img_poker_9_red;
            case GameConstant.POKER_10:
                return isBlack ? R.drawable.img_poker_10_black : R.drawable.img_poker_10_red;
            case GameConstant.POKER_11:
                return isBlack ? R.drawable.img_poker_jack_black : R.drawable.img_poker_jack_red;
            case GameConstant.POKER_12:
                return isBlack ? R.drawable.img_poker_queen_black : R.drawable.img_poker_queen_red;
            case GameConstant.POKER_13:
                return isBlack ? R.drawable.img_poker_king_black : R.drawable.img_poker_king_red;
            default:
                return 0;
        }
    }

    public static int getPokerBackResId(@GameConstant.GameType int gameType) {
        switch(gameType) {
            case GameConstant.POKER_GOLDFLOWER :
                return R.drawable.poker_back_goldflower;
            case GameConstant.POKER_BULL :
                return R.drawable.poker_back_bull;
            default:
                return 0;
        }
    }

    public static int getPokersBackResId(@GameConstant.GameType int gameType) {
        switch(gameType) {
            case GameConstant.POKER_GOLDFLOWER :
                return R.drawable.img_goldflower;
            case GameConstant.POKER_BULL :
                return R.drawable.img_bullflight;
            default:
                return 0;
        }
    }


    /**
     * 获取牌面类型文字资源图片
     * @param type 牌面类型
     * @return 牌面类型对应的文字
     */
    public static int getGlodFlowerType(@GameConstant.GlodFlowerTypes int type) {
        switch(type) {
            case GameConstant.GLODFLOWER_TYPE_BAOZI :
                return R.drawable.text_baozi;
            case GameConstant.GLODFLOWER_TYPE_TONGHUASHUN :
                return R.drawable.text_tonghuashun;
            case GameConstant.GLODFLOWER_TYPE_TONGHUA :
                return R.drawable.text_tonghua;
            case GameConstant.GLODFLOWER_TYPE_SHUNZI :
                return R.drawable.text_shunzi;
            case GameConstant.GLODFLOWER_TYPE_DUIZI :
                return R.drawable.text_duizi;
            case GameConstant.GLODFLOWER_TYPE_DANPAI :
                return R.drawable.text_danpai;
            default:
                return 0;
        }
    }

    public static int getBullType(@GameConstant.BullTypes int type) {
        switch(type) {
            case GameConstant.BULL_TYPE_WXN :
                return R.drawable.text_bull_wxn;
            case GameConstant.BULL_TYPE_ZD :
                return R.drawable.text_bull_zd;
            case GameConstant.BULL_TYPE_WHN :
                return R.drawable.text_bull_whn;
            case GameConstant.BULL_TYPE_SHN :
                return R.drawable.text_bull_shn;
            case GameConstant.BULL_TYPE_NN :
                return R.drawable.text_bull_nn;
            case GameConstant.BULL_TYPE_N9 :
                return R.drawable.text_bull_n9;
            case GameConstant.BULL_TYPE_N8 :
                return R.drawable.text_bull_n8;
            case GameConstant.BULL_TYPE_N7 :
                return R.drawable.text_bull_n7;
            case GameConstant.BULL_TYPE_N6 :
                return R.drawable.text_bull_n6;
            case GameConstant.BULL_TYPE_N5 :
                return R.drawable.text_bull_n5;
            case GameConstant.BULL_TYPE_N4 :
                return R.drawable.text_bull_n4;
            case GameConstant.BULL_TYPE_N3 :
                return R.drawable.text_bull_n3;
            case GameConstant.BULL_TYPE_N2 :
                return R.drawable.text_bull_n2;
            case GameConstant.BULL_TYPE_N1 :
                return R.drawable.text_bull_n1;
            case GameConstant.BULL_TYPE_MN :
                return R.drawable.text_bull_mn;
            default:
                return 0;
        }
    }

    /**
     * 游戏提示文字
     * @param status 游戏状态
     * @return
     */
    public static int getGameToastTextResId(@GameConstant.GoldFlowerStatus int status) {
        switch(status) {
            case GameConstant.START:
                return R.drawable.toast_yxjjks;
            case GameConstant.BETABLE:
                return R.drawable.toast_xzxyqy;
            case GameConstant.SETTLEMENT:
                return R.drawable.toast_bpks;
            case GameConstant.END:
                return R.drawable.toast_jy;
            default:
                return 0;
        }
    }
}

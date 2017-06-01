package com.librarygames.dialog;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.ListView;
import com.fanwe.library.dialog.SDDialogBase;
import com.fanwe.library.utils.SDViewUtil;
import com.librarygames.R;
import com.librarygames.adpter.GamesLogAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luodong on 2016/12/6.
 */

public class GamesHistoryDialog extends SDDialogBase {

    private ListView lv_history;
    private GamesLogAdapter adapter;
    private List<Integer> mListHistory;

    private ImageView iv_starred;
    private ImageView iv_starblue;
    private ImageView iv_staryello;

    public GamesHistoryDialog(Activity activity) {
        super(activity);
        init();
    }

    private void init() {
        setContentView(R.layout.dialog_games_history);
        lv_history = (ListView) findViewById(R.id.lv_history);
        iv_starred = (ImageView) findViewById(R.id.iv_starred);
        iv_starblue = (ImageView) findViewById(R.id.iv_starblue);
        iv_staryello = (ImageView) findViewById(R.id.iv_staryello);
        paddingLeft(SDViewUtil.dp2px(50));
        paddingRight(SDViewUtil.dp2px(50));
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setAdapter();
    }

    public void setImageRes(int res_1, int res_2, int res_3) {
        iv_starred.setImageResource(res_1);
        iv_starblue.setImageResource(res_2);
        iv_staryello.setImageResource(res_3);
    }



    private void setAdapter() {
        mListHistory = new ArrayList<>();
        adapter = new GamesLogAdapter(mListHistory, getOwnerActivity());
        lv_history.setAdapter(adapter);
    }

    public void setHistory(List<Integer> listHistory) {
        this.mListHistory = listHistory;
    }

    @Override
    public void showCenter() {
        super.showCenter();
        adapter.updateData(mListHistory);
    }
}

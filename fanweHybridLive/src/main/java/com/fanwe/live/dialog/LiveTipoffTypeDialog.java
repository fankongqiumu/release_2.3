package com.fanwe.live.dialog;

import java.util.List;

import org.xutils.x;
import org.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.model.BaseActModel;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.utils.SDToast;
import com.fanwe.live.R;
import com.fanwe.live.adapter.LiveTipoffTypeAdapter;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.model.App_tipoff_typeActModel;
import com.fanwe.live.model.App_tipoff_typeModel;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016-5-26 下午6:16:49 类说明
 */
public class LiveTipoffTypeDialog extends LiveBaseDialog
{
    @ViewInject(R.id.btn_cancel)
    private Button btn_cancel;

    @ViewInject(R.id.btn_confim)
    private Button btn_confim;

    @ViewInject(R.id.list)
    private ListView list_tipofftype;

    private LiveTipoffTypeAdapter adapter;

    private String to_user_id;

    public LiveTipoffTypeDialog(Activity activity, String to_user_id)
    {
        super(activity);
        this.to_user_id = to_user_id;
        init();

    }

    private void init()
    {
        setContentView(R.layout.dialog_tipoff_type);
        setCanceledOnTouchOutside(true);
        paddingLeft(100);
        paddingRight(100);
        x.view().inject(this, getContentView());
        register();
        requestTipoff_type();
    }

    private void register()
    {
        btn_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        btn_confim.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                if (adapter != null)
                {
                    App_tipoff_typeModel model = adapter.getCurrentSelectedModel();
                    if (model != null)
                    {
                        requestTipoff(model.getId());
                    } else
                    {
                        SDToast.showToast("请选择您要举报的类型");
                    }
                } else
                {
                    dismiss();
                }
            }
        });
    }

    // 举报类型列表
    private void requestTipoff_type()
    {
        CommonInterface.requestTipoff_type(new AppRequestCallback<App_tipoff_typeActModel>()
        {
            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (actModel.getStatus() == 1)
                {
                    List<App_tipoff_typeModel> list = actModel.getList();
                    if (list != null && list.size() > 0)
                    {
                        bindData(list);
                    }
                } else
                {
                    SDToast.showToast("举报列表为空");
                }
            }

            private void bindData(List<App_tipoff_typeModel> list)
            {
                adapter = new LiveTipoffTypeAdapter(list, getOwnerActivity());
                list_tipofftype.setAdapter(adapter);
            }
        });
    }

    private void requestTipoff(long id)
    {
        CommonInterface.requestTipoff(getLiveInfo().getRoomId(), to_user_id, id, new AppRequestCallback<BaseActModel>()
        {
            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (actModel.getStatus() == 1)
                {
                    SDToast.showToast("已收到举报消息,我们将尽快落实处理");
                    dismiss();
                }
            }
        });
    }

}

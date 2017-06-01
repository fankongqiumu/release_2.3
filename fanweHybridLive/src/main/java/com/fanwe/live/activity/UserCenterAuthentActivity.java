package com.fanwe.live.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fanwe.hybrid.activity.BaseTitleActivity;
import com.fanwe.hybrid.app.App;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.model.BaseActModel;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.handler.PhotoHandler;
import com.fanwe.library.utils.SDToast;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.adapter.LiveUserCenterAuthentAdapter;
import com.fanwe.live.common.AppRuntimeWorker;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.common.ImageCropManage;
import com.fanwe.live.dao.UserModelDao;
import com.fanwe.live.event.EUpLoadImageComplete;
import com.fanwe.live.model.App_AuthentActModel;
import com.fanwe.live.model.App_AuthentItemModel;
import com.fanwe.live.model.UserModel;
import com.fanwe.live.utils.GlideUtil;
import com.fanwe.live.utils.PhotoBotShowUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2016/7/22.
 */
public class UserCenterAuthentActivity extends BaseTitleActivity
{
    public int current_selected_index = 0;//1 点击第一个ImageView 2 点击第二个ImageView 3 点击第三个ImageView

    @ViewInject(R.id.tv_reason)
    private TextView tv_reason;

    @ViewInject(R.id.btn_submit)
    private Button btn_submit;

    @ViewInject(R.id.iv_positive)
    private ImageView iv_positive;
    @ViewInject(R.id.iv_negative)
    private ImageView iv_negative;
    @ViewInject(R.id.iv_hand)
    private ImageView iv_hand;

    @ViewInject(R.id.et_identify_number)
    private EditText et_identify_number;
    @ViewInject(R.id.et_mobile)
    private EditText et_mobile;
    @ViewInject(R.id.et_name)
    private EditText et_name;
    @ViewInject(R.id.tv_type)
    private TextView tv_type;

    @ViewInject(R.id.tv_sex)
    private TextView tv_sex;
    @ViewInject(R.id.tv_nick_name)
    private TextView tv_nick_name;

    @ViewInject(R.id.tv_yinke)
    private TextView tv_yinke;

    private PopupWindow popupWindow;

    private PhotoHandler mPhotoHandler;

    private App_AuthentActModel app_AuthentActModel;

    private String authentication_type;
    private String authentication_name;
    private String mobile;
    private String identify_number;

    private String identify_hold_image;
    private String identify_positive_image;
    private String identify_nagative_image;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_user_center_authent);
        x.view().inject(this);
        init();
    }

    private void init()
    {
        register();
        bindOtherData();
        initPhotoHandler();
        requestAuthent();
    }

    private void register()
    {
        btn_submit.setOnClickListener(this);
        iv_hand.setOnClickListener(this);
        iv_negative.setOnClickListener(this);
        iv_positive.setOnClickListener(this);
        tv_type.setOnClickListener(this);

        mTitle.setMiddleTextTop("认证");
    }

    private void bindOtherData()
    {
        String str_yinke = "适用于用户个人身份证的认证";
        tv_yinke.setText(str_yinke);

        UserModel user = UserModelDao.query();
        if (user != null)
        {
            String nick_name = user.getNick_name();
            SDViewBinder.setTextView(tv_nick_name, nick_name);
            int sex = user.getSex();
            if (sex == 1)
            {
                SDViewBinder.setTextView(tv_sex, "男");
            } else if (sex == 2)
            {
                SDViewBinder.setTextView(tv_sex, "女");
            } else
            {
                SDViewBinder.setTextView(tv_sex, "未知");
            }

            int is_authentication = user.getIs_authentication();
            if (is_authentication == 0)
            {
                setViewClickable(true);
            } else if (is_authentication == 1)
            {
                setViewClickable(false);
            } else if (is_authentication == 2)
            {
                setViewClickable(false);
            } else if (is_authentication == 3)
            {
                setViewClickable(true);
            }
        }
    }

    private void setViewClickable(boolean isClickable)
    {
        if (isClickable)
        {
            SDViewUtil.show(btn_submit);
        } else
        {
            SDViewUtil.hide(btn_submit);
        }

        btn_submit.setClickable(isClickable);
        btn_submit.setEnabled(isClickable);
        iv_positive.setClickable(isClickable);
        iv_positive.setEnabled(isClickable);
        iv_negative.setClickable(isClickable);
        iv_negative.setEnabled(isClickable);
        iv_hand.setClickable(isClickable);
        iv_hand.setEnabled(isClickable);

        et_identify_number.setFocusable(isClickable);
        et_mobile.setFocusable(isClickable);
        et_name.setFocusable(isClickable);

        tv_type.setClickable(isClickable);
        tv_type.setEnabled(isClickable);
    }

    private void initPhotoHandler()
    {
        mPhotoHandler = new PhotoHandler(this);
        mPhotoHandler.setListener(new PhotoHandler.PhotoHandlerListener()
        {

            @Override
            public void onResultFromCamera(File file)
            {
                if (file != null && file.exists())
                {
                    dealImageFile(file);
                }
            }

            @Override
            public void onResultFromAlbum(File file)
            {
                if (file != null && file.exists())
                {
                    dealImageFile(file);
                }
            }

            @Override
            public void onFailure(String msg)
            {
                SDToast.showToast(msg);
            }

            private void dealImageFile(File file)
            {
                if (AppRuntimeWorker.getOpen_sts() == 1)
                {
                    ImageCropManage.startCropActivity(UserCenterAuthentActivity.this, file.getAbsolutePath());
                } else
                {
                    Intent intent = new Intent(UserCenterAuthentActivity.this, LiveUploadImageActivity.class);
                    intent.putExtra(LiveUploadImageActivity.EXTRA_IMAGE_URL, file.getAbsolutePath());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        switch (v.getId())
        {
            case R.id.tv_type:
                clickTvType();
                break;
            case R.id.btn_submit:
                clickBtnSubmit();
                break;
            case R.id.iv_hand:
                clickIvHand();
                break;
            case R.id.iv_negative:
                clickIvNegative();
                break;
            case R.id.iv_positive:
                clickIvPositive();
                break;
        }
    }

    private void requestAuthent()
    {
        CommonInterface.requestAuthent(new AppRequestCallback<App_AuthentActModel>()
        {
            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (actModel.isOk())
                {
                    app_AuthentActModel = actModel;
                    bindActModel(actModel);
                }
            }
        });
    }

    private void bindActModel(App_AuthentActModel actModel)
    {
        mTitle.setMiddleTextTop("认证");

        String investor_send_info = actModel.getInvestor_send_info();
        UserModel user = actModel.getUser();
        if (user != null)
        {
            if (user.getIs_authentication() == 0)
            {
                tv_reason.setText("带星号项为必填项,为了保证您的利益,请如实填写");
            } else if (user.getIs_authentication() == 1)
            {
                tv_reason.setText("您的资料正在审核中,不可编辑哦");
            } else if (user.getIs_authentication() == 2)
            {
                tv_reason.setText("您的资料已经审核通过,不可编辑哦");
            } else if (user.getIs_authentication() == 3)
            {
                if (!TextUtils.isEmpty(investor_send_info))
                {
                    tv_reason.setText("认证失败原因:" + investor_send_info);
                } else
                {
                    tv_reason.setText("认证失败原因:" + "不知道啥原因被拒绝了");
                }
            }

            if (!TextUtils.isEmpty(user.getIdentify_positive_image()))
            {
                identify_positive_image = user.getIdentify_positive_image();
                GlideUtil.load(user.getIdentify_positive_image()).into(iv_positive);
            }
            if (!TextUtils.isEmpty(user.getIdentify_nagative_image()))
            {
                identify_nagative_image = user.getIdentify_nagative_image();
                GlideUtil.load(user.getIdentify_nagative_image()).into(iv_negative);
            }
            if (!TextUtils.isEmpty(user.getIdentify_hold_image()))
            {
                identify_hold_image = user.getIdentify_hold_image();
                GlideUtil.load(user.getIdentify_hold_image()).into(iv_hand);
            }
            if (!TextUtils.isEmpty(user.getAuthentication_type()))
            {
                tv_type.setText(user.getAuthentication_type());
            }

            if (!TextUtils.isEmpty(user.getAuthentication_name()))
            {
                et_name.setText(user.getAuthentication_name());
            }
            if (!TextUtils.isEmpty(user.getContact()))
            {
                et_mobile.setText(user.getContact());
            }

            if (!TextUtils.isEmpty(user.getIdentify_number()))
            {
                et_identify_number.setText(user.getIdentify_number());
            }
        }
    }

    private boolean verifySubmitParams()
    {
        authentication_type = tv_type.getText().toString();
        if (TextUtils.isEmpty(authentication_type) || authentication_type.equals("选择类型"))
        {
            SDToast.showToast("请选择认证类型");
            return false;
        }
        authentication_name = et_name.getText().toString();
        if (TextUtils.isEmpty(authentication_name))
        {
            SDToast.showToast("请输入真实姓名");
            return false;
        }

        mobile = et_mobile.getText().toString();
        if (TextUtils.isEmpty(mobile))
        {
            SDToast.showToast("请输入手机号码");
            return false;
        }

        identify_number = et_identify_number.getText().toString();
        if (TextUtils.isEmpty(identify_number))
        {
            SDToast.showToast("请填写身份证号码");
            return false;
        }

        if (TextUtils.isEmpty(identify_positive_image))
        {
            SDToast.showToast("请上传身份证正面");
            return false;
        }
        if (TextUtils.isEmpty(identify_nagative_image))
        {
            SDToast.showToast("请上传身份证背面");
            return false;
        }
        if (TextUtils.isEmpty(identify_hold_image))
        {
            SDToast.showToast("请上传手持身份证");
            return false;
        }
        return true;
    }

    private void clickBtnSubmit()
    {
        if (!verifySubmitParams())
        {
            return;
        }

        CommonInterface.requestAttestation(authentication_type, authentication_name, mobile, identify_number, identify_hold_image, identify_positive_image, identify_nagative_image, new AppRequestCallback<BaseActModel>()
        {

            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (actModel.isOk())
                {
                    finish();
                }
            }
        });
    }

    private void clickTvType()
    {
        if (app_AuthentActModel != null)
        {
            final List<App_AuthentItemModel> list = app_AuthentActModel.getAuthent_list();
            if (list != null && list.size() > 0)
            {
                View view = getPopListView(this, list, new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        popupWindow.dismiss();
                        ;
                        App_AuthentItemModel model = list.get((int) id);
                        authentication_type = model.getName();
                        SDViewBinder.setTextView(tv_type, model.getName());
                    }
                });
                view.setBackgroundResource(R.drawable.bg_spinner_rectangle_radius);

                popupWindow = new PopupWindow(view, tv_type.getMeasuredWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
                popupWindow.setFocusable(true);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(new ColorDrawable());
                popupWindow.showAsDropDown(tv_type, 0, 10);
            }
        }


    }

    private void clickIvHand()
    {
        current_selected_index = 3;
        chooseImage();
    }

    private void clickIvNegative()
    {
        current_selected_index = 2;
        chooseImage();
    }

    private void clickIvPositive()
    {
        current_selected_index = 1;
        chooseImage();
    }

    private void chooseImage()
    {
        PhotoBotShowUtils.openBotPhotoView(this, mPhotoHandler, PhotoBotShowUtils.DIALOG_BOTH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        mPhotoHandler.onActivityResult(requestCode, resultCode, data);
        ImageCropManage.onActivityResult(this, requestCode, resultCode, data);
    }

    public void onEventMainThread(EUpLoadImageComplete event)
    {
        String image_local_path = event.server_full_path;
        if (current_selected_index == 1)
        {
            identify_positive_image = event.server_path;
            GlideUtil.load(image_local_path).into(iv_positive);
        } else if (current_selected_index == 2)
        {
            identify_nagative_image = event.server_path;
            GlideUtil.load(image_local_path).into(iv_negative);
        } else if (current_selected_index == 3)
        {
            identify_hold_image = event.server_path;
            GlideUtil.load(image_local_path).into(iv_hand);
        }

    }

    private View getPopListView(Activity activity, List<App_AuthentItemModel> list, AdapterView.OnItemClickListener itemClickListener)
    {
        View view = LayoutInflater.from(App.getApplication()).inflate(R.layout.pop_list, null);
        ListView lsv = (ListView) view.findViewById(R.id.list);
        LiveUserCenterAuthentAdapter adapter = new LiveUserCenterAuthentAdapter(list, activity);
        lsv.setAdapter(adapter);
        lsv.setOnItemClickListener(itemClickListener);
        return view;
    }
}
